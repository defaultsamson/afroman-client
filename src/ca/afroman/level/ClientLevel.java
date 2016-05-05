package ca.afroman.level;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.entity.Entity;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.gfx.PointLight;
import ca.afroman.server.AssetType;

public class ClientLevel extends Level
{
	private List<PointLight> lights;
	private LightMap lightmap;
	private PointLight playerLight;
	private double xOffset = 0;
	private double yOffset = 0;
	
	public ClientLevel()
	{
		lights = new ArrayList<PointLight>();
		lightmap = new LightMap(Game.WIDTH, Game.HEIGHT);
		playerLight = new FlickeringLight(0, 0, 50, 47, 4);
		// TODO playerLight.addToLevel(this);
	}
	
	// TODO move into client-side level
	public void setCameraCenterInWorld(double x, double y)
	{
		xOffset = x - (Game.WIDTH / 2);
		yOffset = y - (Game.HEIGHT / 2);
	}
	
	public void render(Texture renderTo)
	{
		// Renders Tiles
		for (Entity tile : tiles)
		{
			// TODO add rendering
			// tile.render(renderTo);
		}
		
		for (Entity entity : entities)
		{
			// TODO add rendering
			// entity.render(renderTo);
		}
		
		if (!Game.instance().isLightingDebugging())
		{
			// Draws all the lighting over everything else
			lightmap.clear();
			
			for (PointLight light : lights)
			{
				light.renderCentered(lightmap);
			}
			
			// Draws the light on the cursor if there is one
			if (Game.instance().isBuildMode() && currentBuildMode == 2)
			{
				lightmap.drawLight(Game.instance().input.getMouseX() - currentBuildLightRadius, Game.instance().input.getMouseY() - currentBuildLightRadius, currentBuildLightRadius);
			}
			
			lightmap.patch();
			
			renderTo.draw(lightmap, 0, 0);
		}
		
		// Draws out the hitboxes
		if (Game.instance().isHitboxDebugging())
		{
			for (Rectangle2D.Double box : hitboxes)
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
		
		// Draws the hitbox that's currently being drawn if there is one
		if (Game.instance().isBuildMode())
		{
			if (currentBuildMode == 1)
			{
				renderTo.draw(Assets.getTexture(AssetType.fromOrdinal(currentBuildTextureOrdinal)), Game.instance().input.getMouseX(), Game.instance().input.getMouseY());
			}
			else if (currentBuildMode == 3 && hitboxClickCount == 1)
			{
				renderTo.getGraphics().drawRect(worldToScreenX(hitboxX1), worldToScreenY(hitboxY1), Game.instance().input.getMouseX() - worldToScreenX(hitboxX1), Game.instance().input.getMouseY() - worldToScreenY(hitboxY1));
			}
		}
	}
	
	private int currentBuildMode = 1;
	private int buildModes = 3;
	
	// Mode 1, Tiles
	private int currentBuildTextureOrdinal = 0;
	
	// Mode 2, PointLights
	private int currentBuildLightRadius = 10;
	
	// Mode 3, HitBoxes
	private double hitboxX1 = 0;
	private double hitboxY1 = 0;
	private int hitboxClickCount = 0;
	
	public void tick()
	{
		if (Game.instance().isBuildMode())
		{
			boolean isShifting = Game.instance().input.shift.isPressed();
			
			int speed = (isShifting ? 5 : 1);
			
			if (Game.instance().input.up.isPressed())
			{
				yOffset -= speed;
			}
			if (Game.instance().input.down.isPressed())
			{
				yOffset += speed;
			}
			if (Game.instance().input.left.isPressed())
			{
				xOffset -= speed;
			}
			if (Game.instance().input.right.isPressed())
			{
				xOffset += speed;
			}
			
			if (Game.instance().input.e.isPressedFiltered())
			{
				currentBuildMode++;
				
				if (currentBuildMode > buildModes)
				{
					currentBuildMode = 1;
				}
			}
			
			if (Game.instance().input.q.isPressedFiltered())
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
				if (Game.instance().input.mouseLeft.isPressedFiltered())
				{
					// TODO Place Tile
					// new Tile(this, screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY()), Assets.getTexture(Assets.fromOrdinal(currentBuildTextureOrdinal)), false, false);
				}
				
				if (Game.instance().input.mouseRight.isPressedFiltered())
				{
					// TODO Remove Tile
					// tiles.remove(getTile(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY())));
				}
				
				int ordinalDir = 0;
				
				if (Game.instance().input.mouseWheelDown.isPressedFiltered())
				{
					ordinalDir -= speed;
				}
				
				if (Game.instance().input.mouseWheelUp.isPressedFiltered())
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
				if (Game.instance().input.mouseLeft.isPressedFiltered())
				{
					// TODO add light
					// PointLight light = new PointLight(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY()), currentBuildLightRadius);
					// light.addToLevel(this);
				}
				
				if (Game.instance().input.mouseRight.isPressedFiltered())
				{
					// TODO Remove light
					// lights.remove(getLight(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY())));
				}
				
				if (Game.instance().input.mouseWheelDown.isPressedFiltered())
				{
					currentBuildLightRadius -= speed;
					if (currentBuildLightRadius < 1) currentBuildLightRadius = 1;
				}
				
				if (Game.instance().input.mouseWheelUp.isPressedFiltered())
				{
					currentBuildLightRadius += speed;
				}
			}
			
			// HitBox mode
			else if (currentBuildMode == 3)
			{
				if (Game.instance().input.mouseLeft.isPressedFiltered())
				{
					if (hitboxClickCount == 0)
					{
						hitboxX1 = screenToWorldX(Game.instance().input.getMouseX());
						hitboxY1 = screenToWorldY(Game.instance().input.getMouseY());
						hitboxClickCount = 1;
					}
					else if (hitboxClickCount == 1)
					{
						// TODO add a hitbox
						// levelHitboxes.add(new Rectangle(hitboxX1, hitboxY1, Game.instance().input.getMouseX() - worldToScreenX(hitboxX1) + 1, Game.instance().input.getMouseY() - worldToScreenY(hitboxY1) + 1));
						hitboxClickCount = 0;
					}
				}
				
				if (Game.instance().input.mouseRight.isPressedFiltered())
				{
					if (hitboxClickCount == 1)
					{
						hitboxClickCount = 0;
					}
					else
					{
						// TODO remove hitbox
						// levelHitboxes.remove(getHitbox(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY())));
					}
				}
			}
		}
		
		// Resets the click count if the mode is exited
		if (currentBuildMode != 3 || !Game.instance().isBuildMode())
		{
			hitboxClickCount = 0;
		}
		
		for (PointLight light : lights)
		{
			light.tick();
		}
		
		// playerLight.setX(Game.instance().player.getX() + 8);
		// playerLight.setY(Game.instance().player.getY() + 8);
	}
	
	public PointLight getLight(int x, int y)
	{
		for (PointLight light : lights)
		{
			int width = light.getWidth();
			int height = light.getHeight();
			
			if (new Rectangle(light.getX() - light.getRadius(), light.getY() - light.getRadius(), width, height).contains(x, y))
			{
				return light;
			}
		}
		
		return null;
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
	
	public void addLight(PointLight light)
	{
		lights.add(light);
	}
}
