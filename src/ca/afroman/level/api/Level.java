package ca.afroman.level.api;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.ITextureDrawable;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.Tile;
import ca.afroman.entity.api.DrawableEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.YComparator;
import ca.afroman.events.Event;
import ca.afroman.events.HitboxToggle;
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
import ca.afroman.util.CastUtil;
import ca.afroman.util.ColourUtil;
import ca.afroman.util.ListUtil;
import ca.afroman.util.ShapeUtil;

public class Level extends ServerClientObject implements ITickable
{
	private static final int DEFAULT_TILE_LAYERS = 7;
	public static final int DEFAULT_DYNAMIC_TILE_LAYER_INDEX = 3;
	
	// Build mode
	private static final int MAX_TOOLTIP_TIME = (60 * 3); // Time in ticks
	private int timeOnTool = 0;
	
	private Grid grid = new Grid();
	private BuildMode buildMode = BuildMode.TILE;
	
	// Tiles
	private DrawableAsset cursorAsset = null;
	private boolean showLayer[];
	private int editingLayer = 0;
	
	// PointLights
	private int currentPointLightRadius = 10;
	private PointLight lightCursor = null;
	
	// FlickeringLights
	private int currentFlickerLightFlicker = 4;
	private double currentFlickerLightRadius = 4;
	private double lastFlickerLightRadius = currentFlickerLightRadius;
	private FlickeringLight flickerCursor = null;
	
	// HitBoxes
	private Vector2DDouble hitbox1 = new Vector2DDouble(0, 0);
	private Vector2DDouble hitbox2 = new Vector2DDouble(0, 0);
	private int hitboxClickCount = 0;
	
	// Used for doing cleanup and setup of build modes
	private LevelType type;
	
	// Universal to both client and server
	private ArrayList<Entity> entities;
	private ArrayList<Hitbox> hitboxes;
	private ArrayList<Event> events;
	private ArrayList<PlayerEntity> players;
	
	// Only for client
	private ArrayList<ArrayList<Tile>> tiles;
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
			tiles = new ArrayList<ArrayList<Tile>>(tileLayers);
			for (int i = 0; i < tileLayers; i++)
			{
				tiles.add(new ArrayList<Tile>());
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
		}
	}
	
	public void chainEvents(Entity triggerer, int inTrigger)
	{
		// TODO detect infinite loops?
		for (Event event : getEvents())
		{
			for (int eventTrigger : event.getInTriggers())
			{
				// If the event has the specified eventID
				if (eventTrigger == inTrigger)
				{
					// Trigger it
					event.trigger(triggerer);
					break;
				}
			}
		}
	}
	
	public void cleanupBuildMode(BuildMode mode)
	{
		// Cleanup
		switch (mode)
		{
			case TILE:
				ClientGame.instance().setCurrentScreen(null);
				break;
			case LIGHT:
				ClientGame.instance().setCurrentScreen(null);
				lightCursor.removeFromLevel();
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
	
	public void copyToClipboard()
	{
		ArrayList<String> lines = new ArrayList<String>();
		
		final String csPrefix = "		";
		final String cPrefix = "			";
		final String isServerSideText = "isServerSide";
		final String lineBreaker = "//////////// HIGHLIGHT ME ////////////";
		
		lines.add(csPrefix + lineBreaker);
		lines.add(csPrefix);
		
		// Shared for both client/server
		
		lines.add(csPrefix + "// Hitboxes");
		
		// Format looks like this
		// new Hitbox(false, 0, 0, 20, 20).addToLevel(this);
		for (Hitbox box : getHitboxes())
		{
			if (!box.isMicroManaged())
			{
				StringBuilder sb = new StringBuilder();
				sb.append(csPrefix);
				sb.append("new Hitbox(");
				sb.append(isServerSideText);
				sb.append(", false, ");
				sb.append(CastUtil.normalizeDouble(box.getX()));
				sb.append(", ");
				sb.append(CastUtil.normalizeDouble(box.getY()));
				sb.append(", ");
				sb.append(CastUtil.normalizeDouble(box.getWidth()));
				sb.append(", ");
				sb.append(CastUtil.normalizeDouble(box.getHeight()));
				sb.append(").addToLevel(this);");
				
				lines.add(sb.toString());
			}
		}
		
		// client only
		lines.add(csPrefix);
		lines.add(csPrefix + "if (!" + isServerSideText + ")");
		lines.add(csPrefix + "{");
		
		lines.add(cPrefix + "// Tiles");
		
		// Format looks like this
		// new Tile(isServerSide, 3, Assets.getTexture(AssetType.CAT).clone(), new Vector2DDouble(20, 30)).addToLevel(this);
		for (int i = 0; i < getTileLayers().size(); i++)
		{
			ArrayList<Tile> layer = getTileLayers().get(i);
			
			boolean wroteSomething = false;
			for (Tile tile : layer)
			{
				if (!tile.isMicroManaged())
				{
					StringBuilder sb = new StringBuilder();
					sb.append(cPrefix);
					sb.append("new Tile(");
					sb.append(i);
					sb.append(", false, new Vector2DDouble(");
					sb.append(CastUtil.normalizeDouble(tile.getPosition().getX()));
					sb.append(", ");
					sb.append(CastUtil.normalizeDouble(tile.getPosition().getY()));
					sb.append("), Assets.getDrawableAsset(AssetType.");
					sb.append(tile.getDrawableAsset().getAssetType().name());
					sb.append(").clone()).addToLevel(this);");
					
					lines.add(sb.toString());
					
					wroteSomething = true;
				}
			}
			
			// Adds a space inbetween layers if something was written, and it's not the last one (because it adds a space on the next layer anyways)
			if (wroteSomething) lines.add(cPrefix);
		}
		
		// lines.add(cPrefix);
		lines.add(cPrefix + "// Lights");
		
		// Lights
		for (PointLight light : getPointLights())
		{
			if (!light.isMicroManaged())
			{
				if (light instanceof FlickeringLight)
				{
					// FlickeringLight
					// Format looks like this
					// new FlickeringLight(false, new Vector2DDouble(40.0, 24.0), 18.0, 20.0, 10).addToLevel(this);
					FlickeringLight flight = (FlickeringLight) light;
					
					StringBuilder sb = new StringBuilder();
					sb.append(cPrefix);
					sb.append("new FlickeringLight(false, new Vector2DDouble(");
					sb.append(CastUtil.normalizeDouble(light.getPosition().getX()));
					sb.append(", ");
					sb.append(CastUtil.normalizeDouble(light.getPosition().getY()));
					sb.append("), ");
					sb.append(CastUtil.normalizeDouble(light.getRadius()));
					sb.append(", ");
					sb.append(CastUtil.normalizeDouble(flight.getRadius2()));
					sb.append(", ");
					sb.append(flight.getTicksPerFrame());
					sb.append(").addToLevel(this);");
					
					lines.add(sb.toString());
				}
				else
				{
					// PointLight
					// Format looks like this
					// new PointLight(isServerSide, false, new Vector2DDouble(10.0, -20.0), 20).addToLevel(this);
					
					StringBuilder sb = new StringBuilder();
					sb.append(cPrefix);
					sb.append("new PointLight(false, new Vector2DDouble(");
					sb.append(CastUtil.normalizeDouble(light.getPosition().getX()));
					sb.append(", ");
					sb.append(CastUtil.normalizeDouble(light.getPosition().getY()));
					sb.append("), ");
					sb.append(CastUtil.normalizeDouble(light.getRadius()));
					sb.append(").addToLevel(this);");
					
					lines.add(sb.toString());
				}
			}
		}
		
		lines.add(csPrefix + "}");
		
		lines.add(csPrefix);
		lines.add(csPrefix + lineBreaker);
		
		ClientGame.instance().input().setClipboard(lines);
	}
	
	public BuildMode getBuildMode()
	{
		return buildMode;
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
	
	public int getDynamicLayer()
	{
		return dynamicLayer;
	}
	
	public int getEditingLayer()
	{
		return editingLayer;
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
	 * @return the level events.
	 */
	public ArrayList<Event> getEvents()
	{
		return events;
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
	 * @return the level hitboxes.
	 */
	public ArrayList<Hitbox> getHitboxes()
	{
		return hitboxes;
	}
	
	public LevelType getLevelType()
	{
		return type;
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
	 * <i>Accessible from server/client.</i>
	 * 
	 * @return the level players.
	 */
	public ArrayList<PlayerEntity> getPlayers()
	{
		return players;
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
			if (!light.isMicroManaged()) ;
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
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the level lights.
	 */
	public ArrayList<PointLight> getPointLights()
	{
		return lights;
	}
	
	/**
	 * Gets a tile at the given coordinates.
	 * 
	 * @param pos the in-level coordinates
	 * @return the tile. <b>null</b> if there are no tiles at that given location.
	 */
	public Entity getTile(int layer, Vector2DDouble pos)
	{
		ArrayList<Tile> tiles = getTiles(layer);
		
		for (int i = tiles.size() - 1; i >= 0; i--)
		{
			Entity tile = tiles.get(i);
			
			if (!tile.isMicroManaged())
			{
				if (tile instanceof DrawableEntity)
				{
					DrawableAsset asset = ((DrawableEntity) tile).getDrawableAsset();
					
					Vector2DDouble textureP = tile.getPosition();
					Vector2DDouble mouseRelativeToTexture = pos.clone().add(-textureP.getX(), -textureP.getY());
					
					boolean isClickWithinTexture = new Rectangle(0, 0, asset.getWidth(), asset.getHeight()).contains(mouseRelativeToTexture.getX(), mouseRelativeToTexture.getY());
					
					if (isClickWithinTexture)
					{
						// If the asset has an accessible Texture, refine the click search pixel by pixel
						if (asset instanceof ITextureDrawable)
						{
							Texture texture = ((ITextureDrawable) asset).getDisplayedTexture();
							
							int colour = texture.getImage().getRGB((int) mouseRelativeToTexture.getX(), (int) mouseRelativeToTexture.getY());
							boolean isClicked = !ColourUtil.isTransparent(colour);
							
							if (isClicked)
							{
								return tile;
							}
						}
						else // If not, then just accept it
						{
							return tile;
						}
					}
				}
				else if (tile.hasHitbox())
				{
					for (Hitbox h : tile.getHitbox())
					{
						if (h.contains(pos.getX(), pos.getY())) return tile;
					}
				}
				else
				{
					Hitbox surrounding = new Hitbox(isServerSide(), true, tile.getPosition().getX(), tile.getPosition().getY(), 16, 16);
					
					if (surrounding.contains(pos.getX(), pos.getY()))
					{
						return tile;
					}
				}
			}
		}
		return null;
		
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the layers of tiles.
	 */
	public ArrayList<ArrayList<Tile>> getTileLayers()
	{
		return tiles;
	}
	
	/**
	 * <i>Accessible from client only.</i>
	 * 
	 * @return the tiles on the specified layer.
	 */
	public ArrayList<Tile> getTiles(int layer)
	{
		return tiles.get(layer);
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
					new Hitbox(isServerSide(), false, box.getX(), box.getY(), box.getWidth(), box.getHeight()).addToLevel(this);
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
					FlickeringLight light = new FlickeringLight(false, flickerCursor.getPosition().clone(), flickerCursor.getRadius(), flickerCursor.getRadius2(), flickerCursor.getTicksPerFrame());
					light.addToLevel(this);
					
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
	
	public boolean isCurrentlevel()
	{
		return ClientGame.instance().getCurrentLevel() == this;
	}
	
	public boolean isShowingLayer(int layer)
	{
		if (layer >= 0 && layer < showLayer.length)
		{
			return showLayer[layer];
		}
		else
		{
			return false;
		}
	}
	
	public void loadBuildMode(BuildMode mode)
	{
		timeOnTool = 0;
		
		// Load new build mode
		switch (mode)
		{
			case TILE:
				if (cursorAsset == null)
				{
					cursorAsset = (DrawableAsset) Assets.getAsset(AssetType.fromOrdinal(0).getNextBuildModeAsset()).clone();
				}
				ClientGame.instance().setCurrentScreen(new GuiTileEditor(grid));
				break;
			case LIGHT:
				if (lightCursor == null)
				{
					lightCursor = new PointLight(true, new Vector2DDouble(0, 0), currentPointLightRadius);
				}
				lightCursor.addToLevel(this);
				ClientGame.instance().setCurrentScreen(new GuiGrid(grid));
				break;
			case HITBOX:
				ClientGame.instance().setCurrentScreen(new GuiGrid(grid));
				break;
			case FLICKERING_LIGHT:
				if (flickerCursor == null)
				{
					flickerCursor = new FlickeringLight(true, new Vector2DDouble(0, 0), currentFlickerLightRadius, currentFlickerLightFlicker, 10);
				}
				ClientGame.instance().setCurrentScreen(new GuiFlickeringLightEditor(grid, flickerCursor));
				break;
		}
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
				
				// Draws the tile on the cursor below the lighting
				if (ClientGame.instance().isBuildMode() && buildMode == BuildMode.TILE)
				{
					cursorAsset.render(renderTo, worldToScreen(screenToWorld(ClientGame.instance().input().getMousePos().clone()).alignToGrid(grid.getGridSize())));
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
		if (ClientGame.instance().isHitboxDebugging() || ClientGame.instance().isBuildMode())
		{
			for (Hitbox box : this.getHitboxes())
			{
				Vector2DInt pos = worldToScreen(new Vector2DDouble(box.getX(), box.getY()));
				renderTo.drawFillRect(new Color(1F, 1F, 1F, 1F), new Color(1F, 1F, 1F, 0.3F), pos, (int) box.getWidth(), (int) box.getHeight());
			}
			
			for (Entity entity : this.getEntities())
			{
				if (entity.hasHitbox())
				{
					for (Hitbox box : entity.getHitbox())
					{
						Vector2DInt pos = worldToScreen(new Vector2DDouble(box.getX(), box.getY()));
						renderTo.drawFillRect(new Color(1F, 1F, 1F, 1F), new Color(1F, 1F, 1F, 0.3F), pos, (int) box.getWidth(), (int) box.getHeight());
					}
				}
			}
			
			for (Entity entity : this.getPlayers())
			{
				if (entity.hasHitbox())
				{
					for (Hitbox box : entity.getHitbox())
					{
						Vector2DInt pos = worldToScreen(new Vector2DDouble(box.getX(), box.getY()));
						renderTo.drawFillRect(new Color(1F, 1F, 1F, 1F), new Color(1F, 1F, 1F, 0.3F), pos, (int) box.getWidth(), (int) box.getHeight());
					}
				}
			}
			
			for (Event e : this.getEvents())
			{
				if (e.hasHitbox())
				{
					for (Hitbox box : e.getHitbox())
					{
						Vector2DInt pos = worldToScreen(new Vector2DDouble(box.getX(), box.getY()));
						
						Color c1; // Outline colour
						Color c2; // Fill colour
						
						if (e instanceof HitboxTrigger)
						{
							c1 = new Color(0.75F, 0.3F, 1F, 1F);
							c2 = new Color(0.75F, 0.3F, 1F, 0.3F);
						}
						else if (e instanceof HitboxToggle)
						{
							c1 = new Color(1F, 0.3F, 0.3F, 1F);
							c2 = new Color(1F, 0.3F, 0.3F, 0.3F);
						}
						else
						{
							c1 = new Color(1F, 1F, 1F, 1F);
							c2 = new Color(1F, 1F, 1F, 0.3F);
						}
						
						renderTo.drawFillRect(c1, c2, pos, (int) box.getWidth(), (int) box.getHeight());
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
			
			// Draws whatever it is for the specific build mode, whether it be the asset on the cursor, or the hitbox being created
			if (buildMode == BuildMode.HITBOX && hitboxClickCount == 1)
			{
				Rectangle box = ShapeUtil.pointsToRectangle(worldToScreen(hitbox1), worldToScreen(hitbox2));
				
				renderTo.drawFillRect(new Color(1F, 1F, 1F, 1F), new Color(1F, 1F, 1F, 0.3F), new Vector2DInt((int) box.getX(), (int) box.getY()), (int) box.getWidth(), (int) box.getHeight());
			}
			else if (buildMode == BuildMode.FLICKERING_LIGHT && hitboxClickCount == 1)
			{
				Vector2DInt pos1 = worldToScreen(hitbox1);
				Vector2DInt pos2 = worldToScreen(hitbox2.clone().alignToGridCenter(grid.getGridSize()));
				
				// The change in x and y from first point to the cursor's point
				int dx = pos2.getX() - pos1.getX();
				int dy = pos2.getY() - pos1.getY();
				
				// Finds the amplitude to modify dx and dy by to achieve the x and y amplitudes for the smaller light flicker line
				double amp = currentFlickerLightFlicker / currentFlickerLightRadius;
				
				// Finds the the x and y amplitudes for the smaller light flicker line
				int cx = (int) (amp * dx);
				int cy = (int) (amp * dy);
				
				// Finds the x and y ordinates of the intermediate point based on the new cx and cy amplitudes
				int x = pos2.getX() - cx;
				int y = pos2.getY() - cy;
				
				Vector2DInt intermediatePoint = new Vector2DInt(x, y);
				
				Paint oldPaint = renderTo.getGraphics().getPaint();
				
				renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
				renderTo.getGraphics().drawLine(pos1.getX(), pos1.getY(), intermediatePoint.getX(), intermediatePoint.getY());
				
				renderTo.getGraphics().setPaint(new Color(0.1F, 0.1F, 1F, 1F));
				renderTo.getGraphics().drawLine(intermediatePoint.getX(), intermediatePoint.getY(), pos2.getX(), pos2.getY());
				
				renderTo.getGraphics().setPaint(oldPaint);
			}
			
			if (timeOnTool < MAX_TOOLTIP_TIME)
			{
				String text1 = "";
				String text2 = "";
				String text3 = "";
				String text4 = "";
				
				int lines = 0;
				
				switch (buildMode)
				{
					case TILE:
						lines = 3;
						text2 = "Tiles";
						text3 = "Scroll to switch texture";
						break;
					case LIGHT:
						lines = 3;
						text2 = "Lights";
						text3 = "Scroll to change size";
						break;
					case HITBOX:
						lines = 3;
						text2 = "Hitboxes";
						text3 = "Click to place both corners";
						text4 = "Right click to cancel corner";
						break;
					case FLICKERING_LIGHT:
						lines = 3;
						text2 = "Flickering Lights";
						text3 = "Click to place center and edge";
						text4 = "Scroll to adjust flicker";
						break;
				}
				
				if (lines == 4) Assets.getFont(AssetType.FONT_NOBLE).renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, ClientGame.HEIGHT - 46), text1);
				if (lines >= 3) Assets.getFont(lines == 3 ? AssetType.FONT_NOBLE : AssetType.FONT_BLACK).renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, ClientGame.HEIGHT - 36), text2);
				if (lines >= 2) Assets.getFont(lines == 2 ? AssetType.FONT_NOBLE : AssetType.FONT_BLACK).renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, ClientGame.HEIGHT - 26), text3);
				if (lines >= 1) Assets.getFont(lines == 1 ? AssetType.FONT_NOBLE : AssetType.FONT_BLACK).renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, ClientGame.HEIGHT - 16), text4);
			}
		}
		
		// Draws the building hitbox, cursor asset, the grid, and the tooltips
		if (ClientGame.instance().isBuildMode())
		{
			
		}
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
	 * Sets the camera to center itself on the given point.
	 * 
	 * @param point the point in level coordinates
	 */
	public void setCameraCenterInWorld(Vector2DDouble point)
	{
		camOffset.setVector(point.getX() - ClientGame.WIDTH / 2, point.getY() - ClientGame.HEIGHT / 2);
	}
	
	public void setEditingLayer(int layer)
	{
		if (layer >= 0 && layer < showLayer.length)
		{
			editingLayer = layer;
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
		else if (isCurrentlevel())
		{
			for (ArrayList<Tile> tileList : tiles)
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
			
			boolean isBuildMode = ClientGame.instance().isBuildMode();
			
			if (isBuildMode)
			{
				if (timeOnTool <= MAX_TOOLTIP_TIME) timeOnTool++;
				
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
							Tile tileToAdd = new Tile(editingLayer, false, screenToWorld(ClientGame.instance().input().getMousePos()).alignToGrid(grid.getGridSize()), cursorAsset.clone());
							tileToAdd.addToLevel(this);
						}
						
						if (ClientGame.instance().input().mouseRight.isPressedFiltered())
						{
							Entity tile = getTile(editingLayer, screenToWorld(ClientGame.instance().input().getMousePos()));
							
							if (tile != null)
							{
								tile.removeFromLevel();
							}
						}
						
						if (ClientGame.instance().input().mouseWheelDown.isPressedFiltered())
						{
							cursorAsset.dispose();
							cursorAsset = (DrawableAsset) Assets.getAsset(cursorAsset.getAssetType().getLastBuildModeAsset()).clone();
						}
						
						if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
						{
							cursorAsset.dispose();
							cursorAsset = (DrawableAsset) Assets.getAsset(cursorAsset.getAssetType().getNextBuildModeAsset()).clone();
						}
						
						if (cursorAsset instanceof ITickable) ((ITickable) cursorAsset).tick();
						break;
					case LIGHT:
						if (ClientGame.instance().input().mouseLeft.isPressedFiltered())
						{
							PointLight light = new PointLight(false, lightCursor.getPosition().clone(), lightCursor.getRadius());
							light.addToLevel(this);
						}
						
						lightCursor.setPosition(screenToWorld(ClientGame.instance().input().getMousePos()).alignToGridCenter(grid.getGridSize()));
						
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
							currentPointLightRadius -= speed;
							if (currentPointLightRadius < 2) currentPointLightRadius = 2;
						}
						
						if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
						{
							currentPointLightRadius += speed;
						}
						
						lightCursor.setRadius(currentPointLightRadius);
						break;
					case FLICKERING_LIGHT:
						if (hitboxClickCount == 1)
						{
							flickerCursor.setPosition(hitbox1.alignToGridCenter(grid.getGridSize()));
							boolean flickerLightChange = false;
							
							hitbox2.alignToGridCenter(grid.getGridSize());
							
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
									hitbox1.setVector(screenToWorld(ClientGame.instance().input().getMousePos())).add(1, 1);
									
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
					hitbox2.setVector(screenToWorld(ClientGame.instance().input().getMousePos())); // .add(1, 1)
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
	
	public void toggleTileLayerShow(int layer)
	{
		if (layer >= 0 && layer < showLayer.length)
		{
			showLayer[layer] = !showLayer[layer];
		}
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
}
