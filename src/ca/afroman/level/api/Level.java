package ca.afroman.level.api;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.DrawableEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.YComparator;
import ca.afroman.events.Event;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.TriggerType;
import ca.afroman.game.Role;
import ca.afroman.gui.build.GuiFlickeringLightEditor;
import ca.afroman.gui.build.GuiGrid;
import ca.afroman.gui.build.GuiTileEditor;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.light.PointLight;
import ca.afroman.log.ALogType;
import ca.afroman.option.Options;
import ca.afroman.packet.PacketActivateTrigger;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.server.ServerGame;
import ca.afroman.util.ListUtil;
import ca.afroman.util.ShapeUtil;

public class Level extends ServerClientObject implements ITickable
{
	// Build mode
	public GridSize grid = GridSize.MEDIUM;
	public BuildMode buildMode = BuildMode.TILE;
	
	// Tiles
	private DrawableAsset cursorAsset = null;
	private boolean showLayer[];
	private int editingLayer = 0;
	
	// PointLights
	private int currentBuildLightRadius = 10;
	
	// FlickeringLights
	private int currentFlickerLightFlicker = 4;
	private double currentFlickerLightRadius = 4;
	private double lastFlickerLightRadius = currentFlickerLightRadius;
	public FlickeringLight flickerCursor = null;
	
	// HitBoxes
	private Vector2DDouble hitbox1 = new Vector2DDouble(0, 0);
	private Vector2DDouble hitbox2 = new Vector2DDouble(0, 0);
	private int hitboxClickCount = 0;
	
	// Used for doing cleanup and setup of build modes
	private boolean lastIsBuildMode = false;
	
	private static final int DEFAULT_TILE_LAYERS = 7;
	private static final int DEFAULT_DYNAMIC_TILE_LAYER_INDEX = 3;
	
	private LevelType type;
	
	// Universal to both client and server
	private ArrayList<Entity> entities;
	private ArrayList<Hitbox> hitboxes;
	private ArrayList<Event> events;
	private ArrayList<PlayerEntity> players;
	
	// Only for client
	private ArrayList<ArrayList<Entity>> tiles;
	private int dynamicLayer;
	private ArrayList<PointLight> lights;
	private LightMap lightmap;
	private Vector2DDouble camOffset;
	
	public Level(boolean isServerSide, LevelType type)
	{
		this(isServerSide, type, DEFAULT_TILE_LAYERS, DEFAULT_DYNAMIC_TILE_LAYER_INDEX);
	}
	
	public Level(boolean isServerSide, LevelType type, int tileLayers, int dynamicTileLayer)
	{
		super(isServerSide);
		
		this.type = type;
		
		entities = new ArrayList<Entity>();
		hitboxes = new ArrayList<Hitbox>();
		events = new ArrayList<Event>();
		players = new ArrayList<PlayerEntity>();
		
		// Initialise client-only variables
		if (!isServerSide)
		{
			tiles = new ArrayList<ArrayList<Entity>>(tileLayers);
			for (int i = 0; i < tileLayers; i++)
			{
				tiles.add(new ArrayList<Entity>());
			}
			
			showLayer = new boolean[] { true, true, true, true, true, true, true };
			dynamicLayer = dynamicTileLayer;
			
			lights = new ArrayList<PointLight>();
			lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT);
			camOffset = new Vector2DDouble(0, 0);
		}
		else
		{
			tiles = null;
			
			showLayer = null;
			dynamicLayer = 0;
			
			lights = null;
			lightmap = null;
			camOffset = null;
		}
	}
	
	public void chainEvents(Entity triggerer, int inTrigger)
	{
		
	}
	
	public void render(Texture renderTo)
	{
		// Renders Tiles
		for (int i = 0; i < tiles.size(); i++)
		{
			// If layer is supposed to be drawn
			if (showLayer[i])
			{
				// If the layer is the designated dynamic layer
				if (i == dynamicLayer)
				{
					ArrayList<DrawableEntity> entities = new ArrayList<DrawableEntity>();
					
					for (Entity entity : this.getEntities())
					{
						if (entity instanceof DrawableEntity)
						{
							entities.add((DrawableEntity) entity);
						}
					}
					for (PlayerEntity player : this.getPlayers())
					{
						entities.add(player);
					}
					
					for (Entity tile : tiles.get(i))
					{
						if (tile instanceof DrawableEntity)
						{
							entities.add((DrawableEntity) tile);
						}
					}
					
					ListUtil.sort(entities, new YComparator());
					
					for (Entity entity : entities)
					{
						if (entity instanceof DrawableEntity) ((DrawableEntity) entity).render(renderTo);
					}
				}
				// Otherwise, just draw the layer normally
				else
				{
					for (Entity tile : tiles.get(i))
					{
						// If it has a texture, render it
						if (tile instanceof DrawableEntity)
						{
							((DrawableEntity) tile).render(renderTo);
						}
					}
				}
			}
		}
		
		// https://www.youtube.com/watch?v=6qIFmeRcY3c
		if (Options.instance().isLightingOn())
		{
			lightmap.clear();
			for (PointLight light : lights)
			{
				light.renderCentered(lightmap);
			}
			lightmap.patch();
			lightmap.render(renderTo, LightMap.PATCH_POSITION);
		}
		
		// Draws out the hitboxes
		if (ClientGame.instance().isHitboxDebugging())
		{
			for (Hitbox box : this.getHitboxes())
			{
				Vector2DInt pos = worldToScreen(new Vector2DDouble(box.getX(), box.getY()));
				renderTo.drawFillRect(new Color(1F, 1F, 1F, 1F), new Color(1F, 1F, 1F, 0.3F), pos, (int) box.getWidth(), (int) box.getHeight());
			}
			
			for (Entity entity : this.getEntities())
			{
				for (Hitbox box : entity.hitboxInLevel())
				{
					Vector2DInt pos = worldToScreen(new Vector2DDouble(box.getX(), box.getY()));
					renderTo.drawFillRect(new Color(1F, 1F, 1F, 1F), new Color(1F, 1F, 1F, 0.3F), pos, (int) box.getWidth(), (int) box.getHeight());
				}
			}
			
			for (Entity entity : this.getPlayers())
			{
				for (Hitbox box : entity.hitboxInLevel())
				{
					Vector2DInt pos = worldToScreen(new Vector2DDouble(box.getX(), box.getY()));
					renderTo.drawFillRect(new Color(1F, 1F, 1F, 1F), new Color(1F, 1F, 1F, 0.3F), pos, (int) box.getWidth(), (int) box.getHeight());
				}
			}
		}
		
		if (ClientGame.instance().getCurrentScreen() instanceof GuiGrid)
		{
			// Draws the grid
			if (grid.getSize() > 0)
			{
				// The amount of extra lines to draw off the bottom and right sides of the screen to prevent any drawing loss
				int bleed = 2;
				int xOffset = (int) camOffset.getX() % grid.getSize(); // Gets the grid offsets so the grid draws to the screen with the world position in mind
				int yOffset = (int) camOffset.getY() % grid.getSize();
				
				Paint oldPaint = renderTo.getGraphics().getPaint();
				
				renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 0.1F));
				
				// Vertical lines
				for (int i = 0; i < Math.ceil(ClientGame.WIDTH / (double) grid.getSize()) + bleed; i++)
				{
					int x = (i * grid.getSize()) - xOffset;
					renderTo.getGraphics().drawLine(x, 0, x, ClientGame.HEIGHT);
				}
				
				// Horizontal lines
				for (int i = 0; i < Math.ceil(ClientGame.HEIGHT / (double) grid.getSize()) + bleed; i++)
				{
					int y = (i * grid.getSize()) - yOffset;
					renderTo.getGraphics().drawLine(0, y, ClientGame.WIDTH, y);
				}
				
				renderTo.getGraphics().setPaint(oldPaint);
			}
		}
	}
	
	@Override
	public void tick()
	{
		for (Entity entity : entities)
		{
			entity.tick();
		}
		
		for (PlayerEntity player : players)
		{
			player.tick();
		}
		
		for (Event event : events)
		{
			event.tick();
		}
		
		if (isServerSide())
		{
			
		}
		else
		{
			for (ArrayList<Entity> tileList : tiles)
			{
				for (Entity tile : tileList)
				{
					tile.tick();
				}
			}
			
			for (PointLight light : lights)
			{
				light.tick();
			}
			
			boolean newIsBuildMode = ClientGame.instance().isBuildMode();
			
			// Cleanup the selected build mode if build mode has been exited,
			// load if it has just been entered
			if (newIsBuildMode && !lastIsBuildMode)
			{
				loadBuildMode(buildMode);
			}
			else if (!newIsBuildMode && lastIsBuildMode)
			{
				cleanupBuildMode(buildMode);
			}
			
			lastIsBuildMode = newIsBuildMode;
			
			if (newIsBuildMode)
			{
				BuildMode lastBuildMode = buildMode;
				
				if (ClientGame.instance().input().e.isPressedFiltered())
				{
					buildMode = buildMode.getNext();
				}
				
				if (ClientGame.instance().input().q.isPressedFiltered())
				{
					buildMode = buildMode.getLast();
				}
				
				// If the build mode has changed, update the build modes
				if (lastBuildMode != buildMode)
				{
					cleanupBuildMode(lastBuildMode);
					loadBuildMode(buildMode);
				}
				
				boolean isShifting = ClientGame.instance().input().shift.isPressed();
				int speed = (isShifting ? 5 : 1);
				
				// Only permit movement if the user is outside of a GUI...
				boolean permitMovement = ClientGame.instance().getCurrentScreen() == null;
				
				// UNLESS it's an instance of the tile editor, or etc...
				if (ClientGame.instance().getCurrentScreen() instanceof GuiTileEditor) permitMovement = true;
				if (ClientGame.instance().getCurrentScreen() instanceof GuiGrid) permitMovement = true;
				
				if (permitMovement)
				{
					if (ClientGame.instance().input().up.isPressed())
					{
						camOffset.add(0, -speed);
					}
					if (ClientGame.instance().input().down.isPressed())
					{
						camOffset.add(0, speed);
					}
					if (ClientGame.instance().input().left.isPressed())
					{
						camOffset.add(-speed, 0);
					}
					if (ClientGame.instance().input().right.isPressed())
					{
						camOffset.add(speed, 0);
					}
				}
				
				switch (buildMode)
				{
					case TILE:
						if (ClientGame.instance().input().mouseLeft.isPressedFiltered())
						{
							DrawableEntity tileToAdd = new DrawableEntity(false, -1, cursorAsset.clone(), screenToWorld(ClientGame.instance().input().getMousePos()).alignToGrid(grid));
							tileToAdd.addTileToLevel(this, editingLayer);
						}
						
						if (ClientGame.instance().input().mouseRight.isPressedFiltered())
						{
							Entity tile = getTile(editingLayer, screenToWorld(ClientGame.instance().input().getMousePos()));
							
							if (tile != null)
							{
								tile.removeTileFromLevel();
							}
						}
						
						if (ClientGame.instance().input().mouseWheelDown.isPressedFiltered())
						{
							cursorAsset.dispose();
							cursorAsset = (DrawableAsset) Assets.getAsset(cursorAsset.getAssetType().getLastDrawableAsset()).clone();
						}
						
						if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
						{
							cursorAsset.dispose();
							cursorAsset = (DrawableAsset) Assets.getAsset(cursorAsset.getAssetType().getNextDrawableAsset()).clone();
						}
						
						if (cursorAsset instanceof ITickable) ((ITickable) cursorAsset).tick();
						break;
					case LIGHT:
						if (ClientGame.instance().input().mouseLeft.isPressedFiltered())
						{
							PointLight light = new PointLight(false, -1, screenToWorld(ClientGame.instance().input().getMousePos()).alignToGridCenter(grid), currentBuildLightRadius);
							lights.add(light);
						}
						
						if (ClientGame.instance().input().mouseRight.isPressedFiltered())
						{
							PointLight light = getPointLight(screenToWorld(ClientGame.instance().input().getMousePos()));
							
							if (light != null)
							{
								light.removeFromLevel();
							}
						}
						
						if (ClientGame.instance().input().mouseWheelDown.isPressedFiltered())
						{
							currentBuildLightRadius -= speed;
							if (currentBuildLightRadius < 2) currentBuildLightRadius = 2;
						}
						
						if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
						{
							currentBuildLightRadius += speed;
						}
						break;
					case FLICKERING_LIGHT:
						if (hitboxClickCount == 1)
						{
							flickerCursor.setPosition(hitbox1);
							boolean flickerLightChange = false;
							
							// The change in x and y from first point to the cursor's point
							double dx = hitbox2.getX() - hitbox1.getX();
							double dy = hitbox2.getY() - hitbox1.getY();
							
							// Finds the length of the line (using pothagorean theorem) based on the given change in x and y
							currentFlickerLightRadius = Math.sqrt((dx * dx) + (dy * dy));
							
							if (currentFlickerLightRadius != lastFlickerLightRadius)
							{
								flickerCursor.setRadius(Math.max(currentFlickerLightRadius, 1));
								lastFlickerLightRadius = currentFlickerLightRadius;
								
								flickerLightChange = true;
							}
							
							if (ClientGame.instance().input().mouseWheelDown.isPressedFiltered())
							{
								currentFlickerLightFlicker -= speed;
								if (currentFlickerLightFlicker < 1) currentFlickerLightFlicker = 1;
								flickerLightChange = true;
							}
							
							if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
							{
								currentFlickerLightFlicker += speed;
								flickerLightChange = true;
							}
							
							if (flickerLightChange)
							{
								flickerCursor.setRadius2(Math.max(currentFlickerLightRadius - currentFlickerLightFlicker, 1));
							}
						}
					case HITBOX:
						// case TRIGGER:
						// case HITBOX_TOGGLE:
						// case TP_TRIGGER:
						if (ClientGame.instance().getCurrentScreen() instanceof GuiGrid || ClientGame.instance().getCurrentScreen() == null)
						{
							if (ClientGame.instance().input().mouseLeft.isPressedFiltered())
							{
								if (hitboxClickCount == 0)
								{
									hitboxClickCount = 1;
									hitbox1.setPosition(screenToWorld(ClientGame.instance().input().getMousePos())).add(1, 1);
									
									if (buildMode == BuildMode.FLICKERING_LIGHT)
									{
										flickerCursor.addToLevel(this);
									}
								}
								else if (hitboxClickCount == 1)
								{
									hitboxClickCount = 0;
									hitboxBehaviour(buildMode, true);
								}
							}
							
							if (ClientGame.instance().input().mouseRight.isPressedFiltered())
							{
								if (hitboxClickCount == 1)
								{
									hitboxClickCount = 0;
									
									if (buildMode == BuildMode.FLICKERING_LIGHT)
									{
										flickerCursor.setPosition(new Vector2DDouble(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2));
										flickerCursor.removeFromLevel();
									}
								}
								else
								{
									hitboxBehaviour(buildMode, false);
								}
							}
						}
						break;
				}
				
				// Sets up the hitbox when it's been clicked
				if (hitboxClickCount > 0)
				{
					hitbox2.setPosition(screenToWorld(ClientGame.instance().input().getMousePos())); // .add(1, 1)
				}
			}
			else if (ClientGame.instance().getRole() == Role.SPECTATOR)
			{
				ClientGame game = ClientGame.instance();
				PlayerEntity pe = game.getPlayer(game.getSpectatingRole());
				if (pe != null)
				{
					setCameraCenterInWorld(pe.getPosition().clone().add(8, 8));
					
					if (game.input().right.isPressedFiltered())
					{
						Role role = game.getSpectatingRole();
						while ((role = role.getNext()) == Role.SPECTATOR);
						
						game.setSpectatingRole(role);
					}
					if (game.input().left.isPressedFiltered())
					{
						Role role = game.getSpectatingRole();
						while ((role = role.getLast()) == Role.SPECTATOR);
						
						game.setSpectatingRole(role);
					}
				}
				else
				{
					game.logger().log(ALogType.WARNING, "PlayerEntity for role " + game.getSpectatingRole() + " is null");
				}
			}
		}
	}
	
	// Deals with generic hitbox behaviour for build modes that use it
	private void hitboxBehaviour(BuildMode mode, boolean leftClick)
	{
		switch (mode)
		{
			default:
				break;
			case HITBOX:
				if (leftClick)
				{
					Rectangle2D box = ShapeUtil.pointsToRectangle(hitbox1, hitbox2);
					hitboxes.add(new Hitbox(box.getX(), box.getY(), box.getWidth(), box.getHeight()));
				}
				else
				{
					Hitbox box = getHitbox(screenToWorld(ClientGame.instance().input().getMousePos()));
					
					if (box != null)
					{
						box.removeFromLevel();
					}
				}
				break;
			case FLICKERING_LIGHT:
				if (leftClick)
				{
					FlickeringLight light = new FlickeringLight(false, PointLight.getIDCounter().getNext(), flickerCursor.getPosition(), flickerCursor.getRadius(), flickerCursor.getRadius2(), flickerCursor.getTicksPerFrame());
					lights.add(light);
					
					flickerCursor.setPosition(new Vector2DDouble(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2));
					flickerCursor.removeFromLevel();
				}
				else
				{
					FlickeringLight light = getFlickeringLight(screenToWorld(ClientGame.instance().input().getMousePos()));
					
					if (light != null)
					{
						light.removeFromLevel();
					}
				}
				break;
		}
		
	}
	
	private void loadBuildMode(BuildMode mode)
	{
		// Load new build mode
		switch (mode)
		{
			case TILE:
				if (cursorAsset == null)
				{
					cursorAsset = (DrawableAsset) Assets.getAsset(AssetType.fromOrdinal(0).getNextRenderable()).clone();
				}
				ClientGame.instance().setCurrentScreen(new GuiTileEditor());
				break;
			case LIGHT:
				ClientGame.instance().setCurrentScreen(new GuiGrid());
				break;
			case HITBOX:
				ClientGame.instance().setCurrentScreen(new GuiGrid());
				break;
			case FLICKERING_LIGHT:
				if (flickerCursor == null) flickerCursor = new FlickeringLight(false, -1, new Vector2DDouble(0, 0), currentFlickerLightRadius, currentFlickerLightFlicker, 10);
				ClientGame.instance().setCurrentScreen(new GuiFlickeringLightEditor());
				break;
		}
	}
	
	private void cleanupBuildMode(BuildMode mode)
	{
		// Cleanup
		switch (mode)
		{
			case TILE:
				ClientGame.instance().setCurrentScreen(null);
				break;
			case LIGHT:
				ClientGame.instance().setCurrentScreen(null);
				break;
			case HITBOX:
				ClientGame.instance().setCurrentScreen(null);
				hitboxClickCount = 0;
				break;
			case FLICKERING_LIGHT:
				ClientGame.instance().setCurrentScreen(null);
				hitboxClickCount = 0;
				flickerCursor.removeFromLevel();
				break;
		}
	}
	
	/**
	 * <i>Accessible from server/client.</i>
	 * 
	 * @return the level entities.
	 */
	public ArrayList<Entity> getEntities()
	{
		return entities;
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the layers of tiles.
	 */
	public ArrayList<ArrayList<Entity>> getTileLayers()
	{
		return tiles;
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the tiles on the specified layer.
	 */
	public ArrayList<Entity> getTiles(int layer)
	{
		return tiles.get(layer);
	}
	
	/**
	 * Gets a tile at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the tile. <b>null</b> if there are no tiles at that given location.
	 */
	public Entity getTile(int layer, Vector2DDouble pos)
	{
		ArrayList<Entity> tiles = getTiles(layer);
		
		for (int i = tiles.size() - 1; i >= 0; i--)
		{
			Entity tile = tiles.get(i);
			
			// TODO generate removable hitboxes for tiles based on asset
			Hitbox surrounding = new Hitbox(tile.getPosition().getX(), tile.getPosition().getY(), 16, 16);
			
			if (surrounding.contains(pos.getX(), pos.getY()))
			{
				return tile;
			}
		}
		return null;
		
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the level lights.
	 */
	public ArrayList<PointLight> getPointLights()
	{
		return lights;
	}
	
	/**
	 * Gets a PointLight at the given coordinates.
	 * 
	 * @param pos the position
	 * @return the entity. <b>null</b> if there are no PointLights at that given location.
	 */
	public PointLight getPointLight(Vector2DDouble pos)
	{
		for (int i = getPointLights().size() - 1; i >= 0; i--)
		{
			PointLight light = getPointLights().get(i);
			
			// If the light is not unnamed
			if (light.getID() != -1) ;
			{
				double radius = light.getRadius();
				
				double xa = (pos.getX() - light.getPosition().getX());
				double ya = (pos.getY() - light.getPosition().getY());
				
				// If the light contains the point
				// Old method, creates a square hitbox to check for collision
				// if (light.getID() != -1 && new Hitbox(light.getX() - radius, light.getY() - radius, (radius * 2) - 1, (radius * 2) - 1).contains(x, y))
				if (xa * xa + ya * ya < radius * radius) // (x - center_x)^2 + (y - center_y)^2 < radius^2
				{
					return light;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a PointLight at the given coordinates.
	 * 
	 * @param pos the position
	 * @return the entity. <b>null</b> if there are no PointLights at that given location.
	 */
	public FlickeringLight getFlickeringLight(Vector2DDouble pos)
	{
		for (int i = getPointLights().size() - 1; i >= 0; i--)
		{
			PointLight light = getPointLights().get(i);
			
			// If the light is not unnamed
			if (light instanceof FlickeringLight && light.getID() != -1) ;
			{
				double radius = light.getRadius();
				
				double xa = (pos.getX() - light.getPosition().getX());
				double ya = (pos.getY() - light.getPosition().getY());
				
				// If the light contains the point
				// Old method, creates a square hitbox to check for collision
				// if (light.getID() != -1 && new Hitbox(light.getX() - radius, light.getY() - radius, (radius * 2) - 1, (radius * 2) - 1).contains(x, y))
				if (xa * xa + ya * ya < radius * radius) // (x - center_x)^2 + (y - center_y)^2 < radius^2
				{
					return (FlickeringLight) light;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * <i>Accessible from server/client.</i>
	 * 
	 * @return the level hitboxes.
	 */
	public ArrayList<Hitbox> getHitboxes()
	{
		return hitboxes;
	}
	
	/**
	 * Gets a hitbox at the given coordinates.
	 *
	 * @param pos the position
	 * @return the entity. <b>null</b> if there are no hitboxes at that given location.
	 */
	public Hitbox getHitbox(Vector2DDouble pos)
	{
		for (int i = getHitboxes().size() - 1; i >= 0; i--)
		{
			Hitbox hitbox = getHitboxes().get(i);
			
			if (!hitbox.isMicroManaged() && hitbox.contains(pos.getX(), pos.getY()))
			{
				return hitbox;
			}
		}
		
		return null;
	}
	
	/**
	 * <i>Accessible from server/client.</i>
	 * 
	 * @return the level events.
	 */
	public ArrayList<Event> getEvents()
	{
		return events;
	}
	
	public Event getEvent(int id)
	{
		for (Event e : events)
		{
			if (id == e.getID()) return e;
		}
		
		return null;
	}
	
	/**
	 * <i>Accessible from server/client.</i>
	 * 
	 * @return the level players.
	 */
	public ArrayList<PlayerEntity> getPlayers()
	{
		return players;
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the level lightmap.
	 */
	public LightMap getLightmap()
	{
		return lightmap;
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the level's camera offset.
	 */
	public Vector2DDouble getCameraOffset()
	{
		return camOffset;
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @param point the point on the screen to translate to level coordinates
	 * @return the in-level coordinate equivalent of <b>point</b>.
	 */
	public Vector2DDouble screenToWorld(Vector2DInt point)
	{
		return new Vector2DDouble(point.getX() + camOffset.getX(), point.getY() + camOffset.getY());
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @param point the point in the level to translate to screen coordinates
	 * @return the on-screen coordinate equivalent of <b>point</b>.
	 */
	public Vector2DInt worldToScreen(Vector2DDouble point)
	{
		return new Vector2DInt((int) point.getX() - (int) camOffset.getX(), (int) point.getY() - (int) camOffset.getY());
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * Sets the camera to center itself on the given point.
	 * 
	 * @param point the point in level coordinates
	 */
	public void setCameraCenterInWorld(Vector2DDouble point)
	{
		camOffset.setPosition(point.getX() - ClientGame.WIDTH / 2, point.getY() - ClientGame.HEIGHT / 2);
	}
	
	/**
	 * Attempts to allow the player to interact with the environment.
	 * 
	 * @param entity the player that attempted interaction
	 */
	public void tryInteract(PlayerEntity entity)
	{
		for (Event e : getEvents())
		{
			if (e instanceof HitboxTrigger)
			{
				HitboxTrigger t = (HitboxTrigger) e;
				
				if (((HitboxTrigger) e).getTriggerTypes().contains(TriggerType.PLAYER_INTERACT))
				{
					if (entity.isColliding(t.getHitbox()))
					{
						t.trigger(entity);
						ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(t.getID(), getLevelType(), entity.getRole()));
					}
				}
			}
		}
	}
	
	public void toggleTileLayerShow(int layer)
	{
		if (layer >= 0 && layer < showLayer.length)
		{
			showLayer[layer] = !showLayer[layer];
		}
	}
	
	public void setEditingLayer(int layer)
	{
		if (layer >= 0 && layer < showLayer.length)
		{
			editingLayer = layer;
		}
	}
	
	public int getEditingLayer()
	{
		return editingLayer;
	}
	
	public boolean isShowingLayer(int layer)
	{
		return layer == editingLayer;
	}
	
	public LevelType getLevelType()
	{
		return type;
	}
}
