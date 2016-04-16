package ca.pixel.game.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextureSheet
{
	private String path;
	private int width;
	private int height;
	
	private int[] pixels;
	
	public TextureSheet(String path)
	{
		BufferedImage image = null;
		
		this.path = path;
		
		try
		{
			image = ImageIO.read(TextureSheet.class.getResourceAsStream(this.path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (image == null)
		{
			return;
		}
		
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		
		// Colour format
		// 0xAARRGGBB
		//
		// To isolate each colour channel
		//
		// int alpha = (pixels[i] >>> 24) & 0xFF;
		// int red   = (pixels[i] >>> 16) & 0xFF;
		// int green = (pixels[i] >>>  8) & 0xFF;
		// int blue  = (pixels[i] >>>  0) & 0xFF;
	}
	
	/**
	 * @return the width of this in pixels.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * @return the height of this in pixels.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * @return the path of this as a resource within the runtime environment (inside the jar).
	 */
	public String getPath()
	{
		return path;
	}
	
	/**
	 * @return the RGB integer pixel data.
	 */
	public int[] getPixels()
	{
		return pixels;
	}
	
	/**
	 * Gets a sub-section of this.
	 * 
	 * @param x the x ordinate to start selecting from
	 * @param y the y ordinate to start selecting from
	 * @param width the width of the selection
	 * @param height the height of the selection
	 * @return the sub-section of this with the given parameters.
	 */
	public Texture getSubTexture(int x, int y, int width, int height)
	{
		return new Texture(getSubPixels(x, y, width, height), width, height);
	}
	
	/**
	 * Gets a sub-section of this.
	 * 
	 * @param x the x ordinate to start selecting from
	 * @param y the y ordinate to start selecting from
	 * @param width the width of the selection
	 * @param height the height of the selection
	 * @return the sub-section of this with the given parameters.
	 */
	public int[] getSubPixels(int x, int y, int width, int height)
	{
		return PixelUtil.getSubPixels(pixels, this.width, x, y, width, height);
	}
}
