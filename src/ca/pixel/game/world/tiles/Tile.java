package ca.pixel.game.world.tiles;

import java.awt.Rectangle;

import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.Level;
import ca.pixel.game.world.LevelObject;

public class Tile extends LevelObject
{
	// public static final HashMap<Material, Tile> tiles = new HashMap<Material, Tile>();
	// public static final Tile VOID = new TileBasic(Material.VOID, false, false);
	// public static final Tile STONE = new TileBasic(Material.STONE, false, false);
	// public static final Tile GRASS = new TileBasic(Material.GRASS, false, false);
	// public static final Tile WALL = new TileBasic(Material.WALL, true, false);
	
	protected Material material;
	protected boolean solid;
	protected boolean emitter;
	
	public Tile(Level level, int x, int y, Material material, boolean isSolid, boolean isEmitter)
	{
		super(level, x, y, (isSolid ? new Rectangle(0, 0, 16, 16) : null));
		this.material = material;
		solid = isSolid;
		emitter = isEmitter;
		
		// tiles.put(material, this);
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public boolean isSolid()
	{
		return solid;
	}
	
	public boolean isEmitter()
	{
		return emitter;
	}
	
	@Override
	public void render(Texture renderTo)
	{
		renderTo.draw(material.getTexture(), x - level.getCameraXOffset(), y - level.getCameraYOffset());
		
		super.render(renderTo);
	}
	
	@Override
	public void tick()
	{
		
	}
}
