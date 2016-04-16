package ca.pixel.game.gfx;

public class Texture
{
	private int[] pixels;
	private int width;
	private int height;
	
	public Texture(int[] pixels, int width, int height)
	{
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}
	
	public void draw(Texture toDraw, int x, int y)
	{
		PixelUtil.drawPixelsOnPixels(toDraw.pixels, toDraw.width, x, y, toDraw.width, toDraw.height, pixels, width, height);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
