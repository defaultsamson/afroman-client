package ca.afroman.level;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import ca.afroman.assets.Asset;
import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.ClientAssetEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.YComparator;
import ca.afroman.gfx.LightMap;
import ca.afroman.gfx.PointLight;
import ca.afroman.gui.GuiBuildModeLayer;
import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;
import ca.afroman.log.ALogType;
import ca.afroman.packet.PacketAddLevelHitbox;
import ca.afroman.packet.PacketAddLevelLight;
import ca.afroman.packet.PacketAddLevelTile;
import ca.afroman.packet.PacketRemoveLevelHitboxLocation;
import ca.afroman.packet.PacketRemoveLevelLightLocation;
import ca.afroman.packet.PacketRemoveLevelTileLocation;
import ca.afroman.util.ListUtil;

public class ClientLevel extends Level
{
	private LightMap lightmap;
	private double xOffset = 0;
	private double yOffset = 0;
	
	public ClientLevel(LevelType type)
	{
		super(type);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT);
	}
	
	public void setCameraCenterInWorld(double x, double y)
	{
		xOffset = x - (ClientGame.WIDTH / 2);
		yOffset = y - (ClientGame.HEIGHT / 2);
	}
	
	public void render(Texture renderTo)
	{
		List<List<Entity>> tiles = getTiles();
		
		try
		{
			synchronized (tiles)
			{
				// Renders Tiles
				for (int i = 0; i <= 2; i++)
				{
					boolean draw = true;
					
					if (ClientGame.instance().isBuildMode() && currentBuildMode == 1)
					{
						switch (i)
						{
							case 0:
								draw = showLayer0;
								break;
							case 1:
								draw = showLayer1;
								break;
							case 2:
								draw = showLayer2;
								break;
						}
					}
					
					if (draw)
					{
						for (Entity tile : tiles.get(i))
						{
							// If it has a texture, render it
							if (tile instanceof ClientAssetEntity) ((ClientAssetEntity) tile).render(renderTo);
						}
					}
				}
				
				List<Entity> entities = new ArrayList<Entity>(this.getEntities());
				for (Entity player : this.getPlayers())
				{
					entities.add(player);
				}
				
				// TODO sort()
				// entities.sort(new YComparator());
				
				ListUtil.sort(entities, new YComparator());
				
				for (Entity entity : entities)
				{
					if (entity instanceof ClientAssetEntity) ((ClientAssetEntity) entity).render(renderTo);
				}
				
				// Renders Tiles
				for (int i = 3; i <= 5; i++)
				{
					boolean draw = true;
					
					if (ClientGame.instance().isBuildMode() && currentBuildMode == 1)
					{
						switch (i)
						{
							case 3:
								draw = showLayer3;
								break;
							case 4:
								draw = showLayer4;
								break;
							case 5:
								draw = showLayer5;
								break;
						}
					}
					
					if (draw)
					{
						for (Entity tile : tiles.get(i))
						{
							// If it has a texture, render it
							if (tile instanceof ClientAssetEntity) ((ClientAssetEntity) tile).render(renderTo);
						}
					}
				}
			}
		}
		catch (ConcurrentModificationException e)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Fuck my ass, Jim");
		}
		
		if (ClientGame.instance().isLightingOn())
		{
			// Draws all the lighting over everything else
			lightmap.clear();
			
			List<PointLight> lights = this.getLights();
			
			synchronized (lights)
			{
				for (PointLight light : lights)
				{
					light.renderCentered(lightmap);
				}
			}
			
			// Draws the light on the cursor if there is one
			if (ClientGame.instance().isBuildMode() && currentBuildMode == 2)
			{
				lightmap.drawLight(ClientGame.instance().input().getMouseX() - currentBuildLightRadius, ClientGame.instance().input().getMouseY() - currentBuildLightRadius, currentBuildLightRadius);
			}
			
			lightmap.patch();
			
			renderTo.draw(lightmap, 0, 0);
		}
		
		// Draws out the hitboxes
		if (ClientGame.instance().isHitboxDebugging() || (currentBuildMode == 3 && ClientGame.instance().isBuildMode()))
		{
			for (Hitbox box : this.getHitboxes())
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
			
			for (Entity entity : this.getEntities())
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
			
			for (Entity entity : this.getPlayers())
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
		
		// Draws the building hitbox, cursor asset, the grid, and the tooltips
		if (ClientGame.instance().isBuildMode())
		{
			if (currentBuildMode == 1)
			{
				if (cursorAsset != null && cursorAsset instanceof IRenderable) ((IRenderable) cursorAsset).render(renderTo, ClientGame.instance().input().getMouseX(), ClientGame.instance().input().getMouseY());
				
				// Draws the grid
				if (grid > 0)
				{
					// The amount of extra lines to draw off the bottom and right sides of the screen to prevent any drawing loss
					int bleed = 2;
					int xOffset = (int) this.getCameraXOffset() % grid; // Gets the grid offsets so the grid draws to the screen with the world position in mind
					int yOffset = (int) this.getCameraYOffset() % grid;
					
					Paint oldPaint = renderTo.getGraphics().getPaint();
					
					renderTo.getGraphics().setPaint(new Color(1.0F, 1.0F, 1.0F, 0.5F));
					
					// Vertical lines
					for (int i = 0; i < Math.ceil(ClientGame.WIDTH / grid) + bleed; i++)
					{
						int x = (i * grid) - xOffset;
						renderTo.getGraphics().drawLine(x, 0, x, ClientGame.HEIGHT);
					}
					
					// Horizontal lines
					for (int i = 0; i < Math.ceil(ClientGame.HEIGHT / grid) + bleed; i++)
					{
						int y = (i * grid) - yOffset;
						renderTo.getGraphics().drawLine(0, y, ClientGame.WIDTH, y);
					}
					
					renderTo.getGraphics().setPaint(oldPaint);
				}
			}
			else if (currentBuildMode == 3 && hitboxClickCount == 1)
			{
				renderTo.getGraphics().drawRect(worldToScreenX(hitboxX), worldToScreenY(hitboxY), (int) hitboxWidth - 1, (int) hitboxHeight - 1);
			}
			
			if (timeOnTool < MAX_TOOLTIP_TIME)
			{
				String text1 = "";
				String text2 = "";
				String text3 = "";
				String text4 = "";
				
				switch (currentBuildMode)
				{
					case 1:
						text2 = "Tiles";
						text3 = "Scroll to switch texture";
						break;
					case 2:
						text2 = "Lights";
						text3 = "Scroll to change size";
						break;
					case 3:
						text2 = "Hitboxes";
						text3 = "Click to place both corners";
						text4 = "Right click to cancel corner";
						break;
				}
				
				Assets.getFont(AssetType.FONT_BLACK).renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT - 46, text1);
				Assets.getFont(AssetType.FONT_BLACK).renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT - 36, text2);
				Assets.getFont(AssetType.FONT_BLACK).renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT - 26, text3);
				Assets.getFont(AssetType.FONT_BLACK).renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT - 16, text4);
			}
		}
	}
	
	private int currentBuildMode = 1;
	private int buildModes = 3;
	private int timeOnTool = 0;
	private static final int MAX_TOOLTIP_TIME = (60 * 3); // Time in ticks
	
	// Mode 1, Tiles
	private int currentBuildTextureOrdinal = 1;
	private Asset cursorAsset = null;
	public boolean showLayer0 = true;
	public boolean showLayer1 = true;
	public boolean showLayer2 = true;
	public boolean showLayer3 = true;
	public boolean showLayer4 = true;
	public boolean showLayer5 = true;
	public int grid = 16;
	
	public int editLayer = 0;
	
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
			timeOnTool++;
			boolean isShifting = ClientGame.instance().input().shift.isPressed();
			
			int speed = (isShifting ? 5 : 1);
			
			if (ClientGame.instance().input().up.isPressed())
			{
				yOffset -= speed;
			}
			if (ClientGame.instance().input().down.isPressed())
			{
				yOffset += speed;
			}
			if (ClientGame.instance().input().left.isPressed())
			{
				xOffset -= speed;
			}
			if (ClientGame.instance().input().right.isPressed())
			{
				xOffset += speed;
			}
			
			if (ClientGame.instance().input().e.isPressedFiltered())
			{
				currentBuildMode++;
				timeOnTool = 0; // On change, reset tooltips
				
				if (currentBuildMode > buildModes)
				{
					currentBuildMode = 1;
				}
			}
			
			if (ClientGame.instance().input().q.isPressedFiltered())
			{
				currentBuildMode--;
				timeOnTool = 0; // On change, reset tooltips
				
				if (currentBuildMode < 1)
				{
					currentBuildMode = buildModes;
				}
			}
			
			// Placing and removing blocks
			if (currentBuildMode == 1)
			{
				if (!(ClientGame.instance().getCurrentScreen() instanceof GuiBuildModeLayer))
				{
					ClientGame.instance().setCurrentScreen(new GuiBuildModeLayer());
				}
				
				if (ClientGame.instance().input().mouseLeft.isPressedFiltered())
				{
					double x = screenToWorldX(ClientGame.instance().input().getMouseX());
					double y = screenToWorldY(ClientGame.instance().input().getMouseY());
					
					// If the grid is on, link to it
					if (grid > 0)
					{
						// Offsets the clicked position to fall to the nearest grid position
						if (x < 0 && y < 0)
						{
							x = -(Math.abs(x) - (Math.abs(x) % grid)) - grid;
							y = -(Math.abs(y) - (Math.abs(y) % grid)) - grid;
						}
						else if (x < 0)
						{
							x = -(Math.abs(x) - (Math.abs(x) % grid)) - grid;
							y = (y - (y % grid));
						}
						else if (y < 0)
						{
							x = (x - (x % grid));
							y = -(Math.abs(y) - (Math.abs(y) % grid)) - grid;
						}
						else
						{
							x = (x - (x % grid));
							y = (y - (y % grid));
						}
					}
					
					Entity tileToAdd = new Entity(-1, cursorAsset.getAssetType(), x, y);
					PacketAddLevelTile pack = new PacketAddLevelTile(editLayer, this.getType(), tileToAdd);
					ClientGame.instance().sockets().sender().sendPacket(pack);
				}
				
				if (ClientGame.instance().input().mouseRight.isPressedFiltered())
				{
					PacketRemoveLevelTileLocation pack = new PacketRemoveLevelTileLocation(editLayer, this.getType(), screenToWorldX(ClientGame.instance().input().getMouseX()), screenToWorldY(ClientGame.instance().input().getMouseY()));
					ClientGame.instance().sockets().sender().sendPacket(pack);
				}
				
				boolean assetUpdated = false;
				
				int ordinalDir = 0;
				
				if (ClientGame.instance().input().mouseWheelDown.isPressedFiltered())
				{
					ordinalDir -= speed;
					assetUpdated = true;
				}
				
				if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
				{
					ordinalDir += speed;
					assetUpdated = true;
				}
				
				if (assetUpdated || cursorAsset == null)
				{
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
					
					// Only allow ordinals of assets that are Textures or SpriteAnimations
					while (!(Assets.getAsset(AssetType.fromOrdinal(currentBuildTextureOrdinal)) instanceof IRenderable))
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
					
					cursorAsset = Assets.getAsset(AssetType.fromOrdinal(currentBuildTextureOrdinal)).clone();
				}
				
				if (cursorAsset instanceof ITickable) ((ITickable) cursorAsset).tick();
			}
			else
			{
				if (ClientGame.instance().getCurrentScreen() instanceof GuiBuildModeLayer)
				{
					ClientGame.instance().setCurrentScreen(null);
				}
			}
			
			// PointLight mode
			if (currentBuildMode == 2)
			{
				if (ClientGame.instance().input().mouseLeft.isPressedFiltered())
				{
					PointLight light = new PointLight(-1, screenToWorldX(ClientGame.instance().input().getMouseX()), screenToWorldY(ClientGame.instance().input().getMouseY()), currentBuildLightRadius);
					
					ClientGame.instance().sockets().sender().sendPacket(new PacketAddLevelLight(this.getType(), light));
				}
				
				if (ClientGame.instance().input().mouseRight.isPressedFiltered())
				{
					ClientGame.instance().sockets().sender().sendPacket(new PacketRemoveLevelLightLocation(this.getType(), screenToWorldX(ClientGame.instance().input().getMouseX()), screenToWorldY(ClientGame.instance().input().getMouseY())));
				}
				
				if (ClientGame.instance().input().mouseWheelDown.isPressedFiltered())
				{
					currentBuildLightRadius -= speed;
					if (currentBuildLightRadius < 1) currentBuildLightRadius = 1;
				}
				
				if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
				{
					currentBuildLightRadius += speed;
				}
			}
			
			// HitBox mode
			if (currentBuildMode == 3)
			{
				if (ClientGame.instance().input().mouseLeft.isPressedFiltered())
				{
					if (hitboxClickCount == 0)
					{
						hitboxX1 = screenToWorldX(ClientGame.instance().input().getMouseX());
						hitboxY1 = screenToWorldY(ClientGame.instance().input().getMouseY());
						hitboxClickCount = 1;
					}
					else if (hitboxClickCount == 1)
					{
						PacketAddLevelHitbox pack = new PacketAddLevelHitbox(this.getType(), new Hitbox(hitboxX, hitboxY, hitboxWidth, hitboxHeight));
						ClientGame.instance().sockets().sender().sendPacket(pack);
						
						hitboxClickCount = 0;
					}
				}
				
				if (ClientGame.instance().input().mouseRight.isPressedFiltered())
				{
					if (hitboxClickCount == 1)
					{
						hitboxClickCount = 0;
					}
					else
					{
						PacketRemoveLevelHitboxLocation pack = new PacketRemoveLevelHitboxLocation(this.getType(), screenToWorldX(ClientGame.instance().input().getMouseX()), screenToWorldY(ClientGame.instance().input().getMouseY()));
						ClientGame.instance().sockets().sender().sendPacket(pack);
					}
				}
				
				// Sets the new X and Y ordinates for point 2 to that of the mouse
				hitboxX2 = screenToWorldX(ClientGame.instance().input().getMouseX());
				hitboxY2 = screenToWorldY(ClientGame.instance().input().getMouseY());
				
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
			else
			{
				hitboxClickCount = 0;
			}
		}
		else
		{
			// If exited from build mode, don't display tooltips
			timeOnTool = 0;
			hitboxClickCount = 0;
			
			if (ClientGame.instance().getCurrentScreen() instanceof GuiBuildModeLayer)
			{
				ClientGame.instance().setCurrentScreen(null);
			}
		}
		
		if (ClientGame.instance().isLightingOn())
		{
			List<PointLight> lights = this.getLights();
			
			synchronized (lights)
			{
				for (PointLight light : lights)
				{
					light.tick();
				}
			}
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
	
	public LightMap getLightMap()
	{
		return lightmap;
	}
}
