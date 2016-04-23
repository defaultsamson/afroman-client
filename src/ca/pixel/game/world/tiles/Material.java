package ca.pixel.game.world.tiles;

import ca.pixel.game.assets.Assets;
import ca.pixel.game.gfx.Texture;

public enum Material
{
	VOID(null), STONE(Assets.dirt), GRASS(Assets.grass.getTexture(0)), WALL(Assets.wall);
	
	Material(Texture texture)
	{
		this.texture = texture;
	}
	
	private Texture texture;
	
	public Texture getTexture()
	{
		return texture;
	}
}
