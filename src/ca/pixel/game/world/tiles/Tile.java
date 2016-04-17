package ca.pixel.game.world.tiles;

import java.util.HashMap;

import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.Level;
import ca.pixel.game.world.Material;

public abstract class Tile
{
	public static final HashMap<Material, Tile> tiles = new HashMap<Material, Tile>();
	public static final Tile VOID = new TileBasic(Material.VOID, false, false);
	public static final Tile STONE = new TileBasic(Material.STONE, true, false);
	public static final Tile GRASS = new TileBasic(Material.GRASS, false, false);
	
	
	protected Material material;
	protected boolean solid;
	protected boolean emitter;
	
	public Tile(Material material, boolean isSolid, boolean isEmitter)
	{
		this.material = material;
		solid = isSolid;
		emitter = isEmitter;
		
		tiles.put(material, this);
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
	
	public abstract void render(Texture renderTo, Level level, int x, int y);
}
