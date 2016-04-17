package ca.pixel.game.gfx;

public class Screen
{
	public static final int MAP_WIDTH = 64;
	public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;
	
	public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH];
	
	public int width;
	public int height;
	
	public Screen(int width, int height)
	{
		
	}
}
