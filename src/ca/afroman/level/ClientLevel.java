package ca.afroman.level;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.ClientPlayerEntity;
import ca.afroman.entity.Entity;
import ca.afroman.entity.Hitbox;
import ca.afroman.entity.SpriteEntity;
import ca.afroman.entity.TextureEntity;
import ca.afroman.gfx.LightMap;
import ca.afroman.gfx.PointLight;
import ca.afroman.packet.PacketAddLevelHitbox;
import ca.afroman.packet.PacketAddLevelTile;
import ca.afroman.packet.PacketRemoveLevelHitboxLocation;
import ca.afroman.packet.PacketRemoveLevelTileLocation;
import ca.afroman.player.Role;

public class ClientLevel extends Level
{
	private LightMap lightmap;
	private double xOffset = 0;
	private double yOffset = 0;
	
	public ClientLevel(LevelType type)
	{
		super(type);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, new Color(0F, 0F, 0F, 0.5F));
	}
	
	public synchronized void setCameraCenterInWorld(double x, double y)
	{
		xOffset = x - (ClientGame.WIDTH / 2);
		yOffset = y - (ClientGame.HEIGHT / 2);
	}
	
	public synchronized void render(Texture renderTo)
	{
		// Renders Tiles
		for (Entity tile : tiles)
		{
			// If it has a texture, render it
			if (tile instanceof TextureEntity) ((TextureEntity) tile).render(renderTo);
		}
		
		for (Entity entity : entities)
		{
			if (entity instanceof TextureEntity) ((TextureEntity) entity).render(renderTo);
			if (entity instanceof SpriteEntity) ((SpriteEntity) entity).render(renderTo);
		}
		
		for (Entity player : players)
		{
			((ClientPlayerEntity) player).render(renderTo);
		}
		
		if (!ClientGame.instance().isLightingDebugging())
		{
			// Draws all the lighting over everything else
			lightmap.clear();
			
			for (PointLight light : lights)
			{
				light.renderCentered(lightmap);
			}
			
			// Draws the light on the cursor if there is one
			if (ClientGame.instance().isBuildMode() && currentBuildMode == 2)
			{
				lightmap.drawLight(ClientGame.instance().input.getMouseX() - currentBuildLightRadius, ClientGame.instance().input.getMouseY() - currentBuildLightRadius, currentBuildLightRadius);
			}
			
			lightmap.patch();
			
			// TODO add back
			renderTo.draw(lightmap, 0, 0);
		}
		
		// Draws out the hitboxes
		if (ClientGame.instance().isHitboxDebugging())
		{
			for (Hitbox box : hitboxes)
			{
				int x = worldToScreenX((int) box.getX());
				int y = worldToScreenY((int) box.getY());
				int width = (int) box.getWidth() - 1;
				int height = (int) box.getHeight() - 1;
				
				renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 0.3F));
				renderTo.getGraphics().fillRect(x, y, width, height);
				renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
				renderTo.getGraphics().drawRect(x, y, width, height);
			}
			
			for (Entity entity : entities)
			{
				for (Hitbox box : entity.hitboxInLevel())
				{
					int x = worldToScreenX((int) box.getX());
					int y = worldToScreenY((int) box.getY());
					int width = (int) box.getWidth() - 1;
					int height = (int) box.getHeight() - 1;
					
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 0.3F));
					renderTo.getGraphics().fillRect(x, y, width, height);
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
					renderTo.getGraphics().drawRect(x, y, width, height);
				}
			}
			
			for (Entity entity : players)
			{
				for (Hitbox box : entity.hitboxInLevel())
				{
					int x = worldToScreenX((int) box.getX());
					int y = worldToScreenY((int) box.getY());
					int width = (int) box.getWidth() - 1;
					int height = (int) box.getHeight() - 1;
					
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 0.3F));
					renderTo.getGraphics().fillRect(x, y, width, height);
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
					renderTo.getGraphics().drawRect(x, y, width, height);
				}
			}
		}
		
		// Draws the hitbox that's currently being drawn if there is one
		if (ClientGame.instance().isBuildMode())
		{
			if (currentBuildMode == 1)
			{
				renderTo.draw(Assets.getTexture(AssetType.fromOrdinal(currentBuildTextureOrdinal)), ClientGame.instance().input.getMouseX(), ClientGame.instance().input.getMouseY());
			}
			else if (currentBuildMode == 3 && hitboxClickCount == 1)
			{
				renderTo.getGraphics().drawRect(worldToScreenX(hitboxX), worldToScreenY(hitboxY), (int) hitboxWidth - 1, (int) hitboxHeight - 1);
			}
		}
	}
	
	private int currentBuildMode = 1;
	private int buildModes = 3;
	
	// Mode 1, Tiles
	private int currentBuildTextureOrdinal = 1;
	
	// Mode 2, PointLights
	private int currentBuildLightRadius = 10;
	
	// Mode 3, HitBoxes
	private double hitboxX1 = 0;
	private double hitboxY1 = 0;
	private double hitboxX2 = 0;
	private double hitboxY2 = 0;
	private double hitboxX = 0;
	private double hitboxY = 0;
	private double hitboxWidth = 0;
	private double hitboxHeight = 0;
	private int hitboxClickCount = 0;
	
	@Override
	public void tick()
	{
		if (ClientGame.instance().isBuildMode())
		{
			boolean isShifting = ClientGame.instance().input.shift.isPressed();
			
			int speed = (isShifting ? 5 : 1);
			
			if (ClientGame.instance().input.up.isPressed())
			{
				yOffset -= speed;
			}
			if (ClientGame.instance().input.down.isPressed())
			{
				yOffset += speed;
			}
			if (ClientGame.instance().input.left.isPressed())
			{
				xOffset -= speed;
			}
			if (ClientGame.instance().input.right.isPressed())
			{
				xOffset += speed;
			}
			
			if (ClientGame.instance().input.e.isPressedFiltered())
			{
				currentBuildMode++;
				
				if (currentBuildMode > buildModes)
				{
					currentBuildMode = 1;
				}
			}
			
			if (ClientGame.instance().input.q.isPressedFiltered())
			{
				currentBuildMode--;
				
				if (currentBuildMode < 1)
				{
					currentBuildMode = buildModes;
				}
			}
			
			// Placing and removing blocks
			if (currentBuildMode == 1)
			{
				if (ClientGame.instance().input.mouseLeft.isPressedFiltered())
				{
					AssetType asset = AssetType.fromOrdinal(currentBuildTextureOrdinal);
					Texture texture = Assets.getTexture(asset);
					
					Entity tileToAdd = new Entity(this, asset, screenToWorldX(ClientGame.instance().input.getMouseX()), screenToWorldY(ClientGame.instance().input.getMouseY()), texture.getWidth(), texture.getHeight());
					PacketAddLevelTile pack = new PacketAddLevelTile(tileToAdd);
					ClientGame.instance().socket().sendPacket(pack);
				}
				
				if (ClientGame.instance().input.mouseRight.isPressedFiltered())
				{
					PacketRemoveLevelTileLocation pack = new PacketRemoveLevelTileLocation(this.getType(), screenToWorldX(ClientGame.instance().input.getMouseX()), screenToWorldY(ClientGame.instance().input.getMouseY()));
					ClientGame.instance().socket().sendPacket(pack);
				}
				
				int ordinalDir = 0;
				
				if (ClientGame.instance().input.mouseWheelDown.isPressedFiltered())
				{
					ordinalDir -= speed;
				}
				
				if (ClientGame.instance().input.mouseWheelUp.isPressedFiltered())
				{
					ordinalDir += speed;
				}
				
				// If it goes over the index bounds, loop back to 0
				currentBuildTextureOrdinal += ordinalDir;
				
				if (currentBuildTextureOrdinal > AssetType.values().length - 1)
				{
					currentBuildTextureOrdinal = 0;
				}
				
				if (currentBuildTextureOrdinal < 0)
				{
					currentBuildTextureOrdinal = AssetType.values().length - 1;
				}
				
				while (Assets.getTexture(AssetType.fromOrdinal(currentBuildTextureOrdinal)) == null)
				{
					currentBuildTextureOrdinal += ordinalDir;
					
					if (currentBuildTextureOrdinal > AssetType.values().length - 1)
					{
						currentBuildTextureOrdinal = 0;
					}
					
					if (currentBuildTextureOrdinal < 0)
					{
						currentBuildTextureOrdinal = AssetType.values().length - 1;
					}
				}
			}
			
			// PointLight mode
			else if (currentBuildMode == 2)
			{
				if (ClientGame.instance().input.mouseLeft.isPressedFiltered())
				{
					// TODO add light
					// PointLight light = new PointLight(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY()), currentBuildLightRadius);
					// light.addToLevel(this);
				}
				
				if (ClientGame.instance().input.mouseRight.isPressedFiltered())
				{
					// TODO Remove light
					// lights.remove(getLight(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY())));
				}
				
				if (ClientGame.instance().input.mouseWheelDown.isPressedFiltered())
				{
					currentBuildLightRadius -= speed;
					if (currentBuildLightRadius < 1) currentBuildLightRadius = 1;
				}
				
				if (ClientGame.instance().input.mouseWheelUp.isPressedFiltered())
				{
					currentBuildLightRadius += speed;
				}
			}
			
			// HitBox mode
			else if (currentBuildMode == 3)
			{
				if (ClientGame.instance().input.mouseLeft.isPressedFiltered())
				{
					if (hitboxClickCount == 0)
					{
						hitboxX1 = screenToWorldX(ClientGame.instance().input.getMouseX());
						hitboxY1 = screenToWorldY(ClientGame.instance().input.getMouseY());
						hitboxClickCount = 1;
					}
					else if (hitboxClickCount == 1)
					{
						PacketAddLevelHitbox pack = new PacketAddLevelHitbox(this.getType(), new Hitbox(hitboxX, hitboxY, hitboxWidth, hitboxHeight));
						ClientGame.instance().socket().sendPacket(pack);
						
						hitboxClickCount = 0;
					}
				}
				
				if (ClientGame.instance().input.mouseRight.isPressedFiltered())
				{
					if (hitboxClickCount == 1)
					{
						hitboxClickCount = 0;
					}
					else
					{
						PacketRemoveLevelHitboxLocation pack = new PacketRemoveLevelHitboxLocation(this.getType(), screenToWorldX(ClientGame.instance().input.getMouseX()), screenToWorldY(ClientGame.instance().input.getMouseY()));
						ClientGame.instance().socket().sendPacket(pack);
					}
				}
				
				// Sets the new X and Y ordinates for point 2 to that of the mouse
				hitboxX2 = screenToWorldX(ClientGame.instance().input.getMouseX());
				hitboxY2 = screenToWorldY(ClientGame.instance().input.getMouseY());
				
				// Finds the x, y, and height depending on which ones are greater than the other.
				// Have to do this because Java doesn't support rectangles with negative width or height
				if (hitboxX1 > hitboxX2)
				{
					hitboxX = hitboxX2;
					hitboxWidth = hitboxX1 - hitboxX2 + 1;
				}
				else
				{
					hitboxX = hitboxX1;
					hitboxWidth = hitboxX2 - hitboxX1 + 1;
				}
				
				if (hitboxY1 > hitboxY2)
				{
					hitboxY = hitboxY2;
					hitboxHeight = hitboxY1 - hitboxY2 + 1;
				}
				else
				{
					hitboxY = hitboxY1;
					hitboxHeight = hitboxY2 - hitboxY1 + 1;
				}
			}
		}
		
		// Resets the click count if the mode is exited
		if (currentBuildMode != 3 || !ClientGame.instance().isBuildMode())
		{
			hitboxClickCount = 0;
		}
		
		for (PointLight light : lights)
		{
			light.tick();
		}
		
		// playerLight.setX(Game.instance().player.getX() + 8);
		// playerLight.setY(Game.instance().player.getY() + 8);
		
		// TODO keep the super tick?
		super.tick();
	}
	
	public double screenToWorldX(double x)
	{
		return x + xOffset;
	}
	
	public double screenToWorldY(double y)
	{
		return y + yOffset;
	}
	
	public int worldToScreenX(double x)
	{
		return (int) (x - xOffset);
	}
	
	public int worldToScreenY(double y)
	{
		return (int) (y - yOffset);
	}
	
	public double getCameraXOffset()
	{
		return xOffset;
	}
	
	public double getCameraYOffset()
	{
		return yOffset;
	}
	
	/**
	 * Gets the player at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the player. <b>null</b> if there are no players at that given location.
	 */
	@Override
	public synchronized Entity getPlayer(double x, double y)
	{
		for (Entity entity : players)
		{
			if (entity instanceof ClientPlayerEntity)
			{
				for (Hitbox hitbox : entity.hitboxInLevel())
				{
					if (hitbox.contains(x, y)) return entity;
				}
			}
			else
			{
				System.out.println("[LEVEL] Non-ClientPlayerEntity in the player list of level " + this.getType());
			}
		}
		return null;
	}
	
	/**
	 * Gets the player with the given role.
	 * 
	 * @param role whether it's player 1 or 2
	 * @return the player.
	 */
	@Override
	public synchronized Entity getPlayer(Role role)
	{
		for (Entity entity : players)
		{
			if (entity instanceof ClientPlayerEntity)
			{
				if (((ClientPlayerEntity) entity).getRole() == role) return entity;
			}
			else
			{
				System.out.println("[LEVEL] Non-ClientPlayerEntity in the player list of level " + this.getType());
			}
		}
		return null;
	}
}
