package ca.pixel.game.gfx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture
{
	/** The Hexadecimal colour code to replace with alpha. */
	private static final String ALPHA_COLOUR_HEX1 = "0xFF00FF"; // Light purple
	private static final String ALPHA_COLOUR_HEX2 = "0x7F007F"; // Dark purple
	/** The RGB integer to replace with alpha. */
	public static final int ALPHA_COLOUR1 = new Color(Integer.valueOf(ALPHA_COLOUR_HEX1.substring(2, 4), 16), Integer.valueOf(ALPHA_COLOUR_HEX1.substring(4, 6), 16), Integer.valueOf(ALPHA_COLOUR_HEX1.substring(6, 8), 16)).getRGB();
	public static final int ALPHA_COLOUR2 = new Color(Integer.valueOf(ALPHA_COLOUR_HEX2.substring(2, 4), 16), Integer.valueOf(ALPHA_COLOUR_HEX2.substring(4, 6), 16), Integer.valueOf(ALPHA_COLOUR_HEX2.substring(6, 8), 16)).getRGB();
	
	private int[] pixels;
	private int width;
	private int height;
	
	public Texture(int[] pixels, int width, int height)
	{
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}
	
	public static Texture fromResource(String path)
	{
		BufferedImage image = null;
		
		try
		{
			image = ImageIO.read(Texture.class.getResourceAsStream(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		// Colour format
		// 0xAARRGGBB
		//
		// To isolate each colour channel
		//
		// int alpha = (pixels[i] >>> 24) & 0xFF;
		// int red = (pixels[i] >>> 16) & 0xFF;
		// int green = (pixels[i] >>> 8) & 0xFF;
		// int blue = (pixels[i] >>> 0) & 0xFF;
		
		return new Texture(image.getRGB(0, 0, width, height, null, 0, width), width, height);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	@Override
	public Texture clone()
	{
		return new Texture(pixels.clone(), width, height);
	}
	
	/**
	 * Flips this horizontally.
	 */
	public void flipX()
	{
		// Flips the sprite horizontally.
		int[] pixels = new int[this.pixels.length];
		int row, column;
		for (int i = 0; i < this.pixels.length; i++)
		{
			row = (i / width);
			column = (i % width);
			pixels[row * width + column] = this.pixels[(row + 1) * width - column - 1];
		}
		
		this.pixels = pixels;
	}
	
	/**
	 * Flips this vertically.
	 */
	public void flipY()
	{
		for (int i = 0; i < height / 2; ++i)
		{
			int k = height - 1 - i;
			for (int j = 0; j < width; ++j)
			{
				int temp = pixels[i * width + j];
				pixels[i * width + j] = pixels[k * width + j];
				pixels[k * width + j] = temp;
			}
		}
	}
	
	/**
	 * Rotates this 90 degrees clockwise.
	 */
	public void rotateCW()
	{
		// Rotates the sprite 90 degrees clockwise.
		int[] pixels = new int[this.pixels.length];
		int row, column;
		for (int i = 0; i < this.pixels.length; i++)
		{
			row = (i / width);
			column = (i % width);
			pixels[row * width + column] = this.pixels[(height - 1) * width - (((row * width + column) % height) * width) + (row * width + column) / height];
		}
		int w = width;
		width = height;
		height = w;
		this.pixels = pixels;
	}
	
	/**
	 * Rotates this 90 degrees counter-clockwise.
	 */
	public void rotateCCW()
	{
		
		// Rotates the sprite 90 degrees counter-clockwise.
		int[] pixels = new int[this.pixels.length];
		int row, column;
		for (int i = 0; i < this.pixels.length; i++)
		{
			row = (i / width);
			column = (i % width);
			pixels[row * width + column] = this.pixels[(width - 1) + (((row * width + column) % height) * width) - (row * width + column) / height];
		}
		int w = width;
		width = height;
		height = w;
		this.pixels = pixels;
	}
	
	/**
	 * Rotates this 180 degrees.
	 */
	public void rotate180()
	{
		// Rotates the sprite 180 degrees.
		int[] pixels = new int[this.pixels.length];
		for (int i = 0; i < this.pixels.length; i++)
		{
			pixels[i] = this.pixels[pixels.length - i - 1];
		}
		this.pixels = pixels;
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
	 * Gets the pixel data of a sub-image of an image's pixels data.
	 * 
	 * @param x the x ordinate to get from in <b>inPixel</b>
	 * @param y the y ordinate to get from in <b>inPixel</b>
	 * @param width the width of the area of <b>inPixel</b>'s image to get
	 * @param height the height of the area of <b>inPixel</b>'s image to get
	 */
	public int[] getSubPixels(int x, int y, int width, int height)
	{
		// If selection is in the area
		if (!(x < 0 || x + width > this.width || y < 0 || y + height > this.height))
		{
			int[] output = new int[width * height];
			
			// Draws all the lines of pixels with their x offsets,
			// an incremental y offset being applied for each line down
			for (int iy = 0; iy < height; iy++)
			{
				for (int ix = 0; ix < width; ix++)
				{
					output[ix + (iy * width)] = pixels[x + ix + ((y + iy) * this.width)];
				}
			}
			
			return output;
		}
		return null;
	}
	
	/**
	 * Superimposes an image's pixels over this one, listening for alpha on the ALPHA_COLOUR channel.
	 * 
	 * @param toDraw the image to draw's RGB pixel data
	 * @param width the width of <b>toDraw</b>'s image
	 * @param x the x ordinate to draw <b>toDraw</b> on <b>this</b>
	 * @param y the y ordinate to draw <b>toDraw</b> on <b>this</b>
	 */
	public void draw(Texture toDraw, int x, int y)
	{
		// If not being drawn on the screen at all
		if (!(x + toDraw.width < 0 || x > this.width || y + toDraw.height < 0 || y > this.height))
		{
			int inX = 0;
			int inY = 0;
			int drawWidth = toDraw.width;
			int drawHeight = toDraw.height;
			
			// Fix the parameters if an image is trying to be draw off an edge of the screen
			
			// Corrects being off the left edge.
			if (x < 0)
			{
				// Draws to x = 0, but starts drawing farther in from the source image, and gives a smaller width
				// The end result is it appears off-screen to the left
				inX = -x;
				drawWidth -= inX;
				x = 0;
			}
			
			// Corrects being off the top edge.
			if (y < 0)
			{
				// Draws to y = 0, but starts drawing farther in from the source image, and gives a smaller height
				// The end result is it appears off-screen to the top
				inY = -y;
				drawHeight -= inY;
				y = 0;
			}
			
			// Corrects being off the right edge.
			if (x + drawWidth > this.width)
			{
				// Makes width smaller to go out exactly to the right edge of the screen.
				// End result appears to be going off-screen to the right.
				drawWidth = this.width - x;
			}
			
			// Corrects being off the bottom edge.
			if (y + drawHeight > this.height)
			{
				// Makes height smaller to go out exactly to the bottom edge of the screen.
				// End result appears to be going off-screen to the bottom.
				drawHeight = this.height - y;
			}
			
			// Draws all the lines of pixels with their x offsets,
			// an incremental y offset being applied for each line down
			for (int iy = 0; iy < drawHeight; iy++)
			{
				for (int ix = 0; ix < drawWidth; ix++)
				{
					// Gets the pixel's RGB int to transfer
					int inPixel = toDraw.pixels[inX + ix + ((inY + iy) * toDraw.width)];
					
					// If the RGB int is the default for alpha, don't draw it, if it isn't, then draw it
					if (inPixel != ALPHA_COLOUR1 && inPixel != ALPHA_COLOUR2) pixels[x + ix + ((y + iy) * width)] = inPixel;
				}
				
				// Original method for drawing from one pixel array to another.
				// The down side is this method will not super-impose alpha.
				// System.arraycopy(pixels, inX + ((inY + i) * this.width), toDrawTo, x + ((y + i) * width), width);
			}
		}
	}
}
