package ca.pixel.game.world.tiles;

import java.awt.Rectangle;

import ca.pixel.game.assets.Texture;
import ca.pixel.game.world.Level;
import ca.pixel.game.world.LevelObject;

public class Tile extends LevelObject
{
	// public static final HashMap<Material, Tile> tiles = new HashMap<Material, Tile>();
	// public static final Tile VOID = new TileBasic(Material.VOID, false, false);
	// public static final Tile STONE = new TileBasic(Material.STONE, false, false);
	// public static final Tile GRASS = new TileBasic(Material.GRASS, false, false);
	// public static final Tile WALL = new TileBasic(Material.WALL, true, false);
	
	protected Texture texture;
	protected Material material;
	protected boolean isEmitter;
	
	public Tile(Level level, int x, int y, Texture texture, Material material, boolean isEmitter, boolean isSolid)
	{
		this(level, x, y, texture, material, isEmitter, (isSolid ? new Rectangle(0, 0, 16, 16) : null));
	}
	
	public Tile(Level level, int x, int y, Texture texture, Material material, boolean isEmitter, Rectangle... hitboxes)
	{
		super(level, x, y, hitboxes);
		this.material = material;
		this.isEmitter = isEmitter;
		this.texture = texture;
		
		// tiles.put(material, this);
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public boolean isEmitter()
	{
		return isEmitter;
	}
	
	@Override
	public void render(Texture renderTo)
	{
		renderTo.draw(texture, x - level.getCameraXOffset(), y - level.getCameraYOffset());
		
		super.render(renderTo);
	}
	
	@Override
	public void tick()
	{
		
	}
	
	public Texture getTexture()
	{
		return texture;
	}
}
