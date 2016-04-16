package ca.pixel.game.gfx;

import java.awt.Color;

public class PixelUtil
{
	/** The Hexadecimal colour code to replace with alpha. */
	private static final String ALPHA_COLOUR_HEX = "0xFF00FF";
	/** The RGB integer to replace with alpha. */
	public static final int ALPHA_COLOUR = new Color(Integer.valueOf(ALPHA_COLOUR_HEX.substring(2, 4), 16), Integer.valueOf(ALPHA_COLOUR_HEX.substring(4, 6), 16), Integer.valueOf(ALPHA_COLOUR_HEX.substring(6, 8), 16)).getRGB(); // 0xFF00FF, -65281
	
	/**
	 * Superimposes an image's pixels from one array to another, listening for alpha on the ALPHA_COLOUR channel.
	 * 
	 * @param inPixels the image's RGB pixel data to draw
	 * @param imageWidth the total width of the entirety of <b>inPixel</b>'s image
	 * @param x the x ordinate to draw <b>inPixel</b> on <b>toDrawTo</b>
	 * @param y the y ordinate to draw <b>inPixel</b> on <b>toDrawTo</b>
	 * @param width the width of <b>inPixel</b>'s image to draw
	 * @param height the height of <b>inPixel</b>'s image
	 * @param toDrawTo the other image's RGB pixel data to draw to
	 * @param width2 the width of <b>toDrawTo</b>'s image
	 * @param height2 the height of <b>toDrawTo</b>'s image
	 */
	public static void drawPixelsOnPixels(int[] inPixels, int imageWidth, int x, int y, int width, int height, int[] toDrawTo, int width2, int height2)
	{
		// If not being drawn on the screen at all
		if (!(x + width < 0 || x > width2 || y + height < 0 || y > height2))
		{
			int inX = 0;
			int inY = 0;
			
			// Fix the parameters if an image is trying to be draw off an edge of the screen
			
			// Corrects being off the left edge.
			if (x < 0)
			{
				// Draws to x = 0, but starts drawing farther in from the source image, and gives a smaller width
				// The end result is it appears off-screen to the left
				inX = -x;
				width -= inX;
				x = 0;
			}
			
			// Corrects being off the top edge.
			if (y < 0)
			{
				// Draws to y = 0, but starts drawing farther in from the source image, and gives a smaller height
				// The end result is it appears off-screen to the top
				inY = -y;
				height -= inY;
				y = 0;
			}
			
			// Corrects being off the right edge.
			if (x + width > width2)
			{
				// Makes width smaller to go out exactly to the right edge of the screen.
				// End result appears to be going off-screen to the right.
				width = width2 - x;
			}
			
			// Corrects being off the bottom edge.
			if (y + height > height2)
			{
				// Makes height smaller to go out exactly to the bottom edge of the screen.
				// End result appears to be going off-screen to the bottom.
				height = height2 - y;
			}
			
			// Draws all the lines of pixels with their x offsets,
			// an incremental y offset being applied for each line down
			for (int iy = 0; iy < height; iy++)
			{
				for (int ix = 0; ix < width; ix++)
				{
					// Gets the pixel's RGB int to transfer
					int inPixel = inPixels[inX + ix + ((inY + iy) * imageWidth)];
					
					// If the RGB int is the default for alpha, don't draw it, if it isn't, then draw it
					if (inPixel != ALPHA_COLOUR) toDrawTo[x + ix + ((y + iy) * width2)] = inPixel;
				}
				
				// Original method for drawing from one pixel array to another.
				// The down side is this method will not super-impose alpha.
				// System.arraycopy(inPixels, inX + ((inY + i) * imageWidth), toDrawTo, x + ((y + i) * width2), width);
			}
		}
	}
	
	/**
	 * Gets the pixel data of a sub-image of an image's pixels data.
	 * <p>
	 * <b>WARNING: </b> It is assumed that none of the coordinates go out of bounds, so be careful.
	 * 
	 * @param inPixels the image's RGB pixel data to get from
	 * @param imageWidth the total width of the entirety of <b>inPixel</b>'s image
	 * @param x the x ordinate to get from in <b>inPixel</b>
	 * @param y the y ordinate to get from in <b>inPixel</b>
	 * @param width the width of the area of <b>inPixel</b>'s image to get
	 * @param height the height of the area of <b>inPixel</b>'s image to get
	 */
	public static int[] getSubPixels(int[] inPixels, int imageWidth, int x, int y, int width, int height)
	{
		int[] output = new int[width*height];
		
		// Draws all the lines of pixels with their x offsets,
		// an incremental y offset being applied for each line down
		for (int iy = 0; iy < height; iy++)
		{
			System.arraycopy(inPixels, x + ((y + iy) * imageWidth), output, x + ((y + iy) * width), width);
		}
		
		return output;
	}
}
