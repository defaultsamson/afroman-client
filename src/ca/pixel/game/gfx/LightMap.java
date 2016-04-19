package ca.pixel.game.gfx;

public class LightMap extends Texture
{
	public static final int AMBIENT_COLOUR = 0x646464;
	private static final int BUFFER_WASTE = 0x0000FF;
	private static final int WHITE = 0xFFFFFF;
	private static final float WHITE_NORMAL_R = normaliseRGB((WHITE & 0xFF0000) >> 16);
	private static final float WHITE_NORMAL_G = normaliseRGB((WHITE & 0xFF00) >> 8);
	private static final float WHITE_NORMAL_B = normaliseRGB(WHITE & 0xFF);
	
	public LightMap(int width, int height)
	{
		super(getBlankSlate(width, height), width, height);
	}
	
	public void render(Texture toDrawTo, int x, int y)
	{
		// Loop over pixels within light radius
		for (int iy = 0; iy < height; iy++)
		{
			for (int ix = 0; ix < width; ix++)
			{
				int screenPixel = toDrawTo.pixels[ix + (iy * toDrawTo.width)];
				int lightmapPixel = pixels[ix + (iy * width)];
				
				float oldR = normaliseRGB((screenPixel & 0xff0000) >> 16);
				float oldG = normaliseRGB((screenPixel & 0xff00) >> 8);
				float oldB = normaliseRGB((screenPixel & 0xff));
				
				float newR = normaliseRGB((lightmapPixel & 0xff0000) >> 16);
				float newG = normaliseRGB((lightmapPixel & 0xff00) >> 8);
				float newB = normaliseRGB((lightmapPixel & 0xff));
				
				int r = normalToRGB(oldR * newR);
				int g = normalToRGB(oldG * newG);
				int b = normalToRGB(oldB * newB);
				
				toDrawTo.pixels[ix + (iy * toDrawTo.width)] = (0xff << 24) | (r << 16) | (g << 8) | (b);
			}
		}
	}
	
	public void drawLight(int x, int y, int radius)
	{
		drawLight(x, y, radius, 1.0F, 0xFFFFFF);
	}
	
	public void drawLight(int x, int y, int radius, float intensity)
	{
		drawLight(x, y, radius, intensity, 0xFFFFFF);
	}
	
	public void drawLight(int x, int y, int radius, float intensity, int colour)
	{
		// Loop over pixels within light radius
		for (int iy = y - radius * 2; iy < y + radius * 2; iy++)
		{
			for (int ix = x - radius * 2; ix < x + radius * 2; ix++)
			{
				// If out of bounds, continue
				if (ix < 0 || iy < 0 || ix >= this.width || iy >= this.height) continue;
				
				// Finds the distance
				float dist = (float) Math.sqrt(((ix - x) * (ix - x)) + ((iy - y) * (iy - y)));
				
				int current = pixels[ix + (iy * width)];
				
				/*
				 * The old colour to overlay this on (Essentially the brightest colour)
				 * int old = WHITE;
				 * float oldR = normaliseRGB((old & 0xFF0000) >> 16);
				 * float oldG = normaliseRGB((old & 0xFF00) >> 8);
				 * float oldB = normaliseRGB(old & 0xFF);
				 */
				
				// The function that dictates the intensity of the rays as the light gets farther and farther away
				float amplitude = radius / (dist * dist);
				
				// A 0.0 to 1.0 value for each rgb colour for multiplying
				float lightR = normaliseRGB(((colour & 0xFF0000) >> 16) * amplitude);
				float lightG = normaliseRGB(((colour & 0xFF00) >> 8) * amplitude);
				float lightB = normaliseRGB((colour & 0xFF) * amplitude);
				
				// A 0 to 255 value for the rgb colours
				int newR = normalToRGB((WHITE_NORMAL_R * lightR) * intensity);
				int newG = normalToRGB((WHITE_NORMAL_G * lightG) * intensity);
				int newB = normalToRGB((WHITE_NORMAL_B * lightB) * intensity);
				
				// Gets the new colour as one RBG integer
				int newColour = (newR << 16) | (newG << 8) | newB;
				
				// Only set the pixel to it if the new colour doesn't go below the ambient colour.
				if (newColour > AMBIENT_COLOUR && newColour > current) 
				{
					pixels[ix + (iy * this.width)] = newColour;
				}
			}
		}
	}
	
	private static float normaliseRGB(float rgbValue)
	{
		// Picks a value that's less than or equal to 1.0 and 0.0
		return (float) Math.max(Math.min((rgbValue / 255), 1.0), 0.0);
	}
	
	private static int normalToRGB(float rgbValue)
	{
		return Math.round(rgbValue * 255);
	}
	
	public void clear()
	{
		for (int i = 0; i < pixels.length; i++)
		{
			pixels[i] = BUFFER_WASTE;
		}
	}
	
	/**
	 * Fills all blank spots with ambient colour.
	 */
	public void patch()
	{
		for (int i = 0; i < pixels.length; i++)
		{
			if (pixels[i] == BUFFER_WASTE) pixels[i] = AMBIENT_COLOUR;
		}
	}
	
	private static int[] getBlankSlate(int width, int height)
	{
		int[] toReturn = new int[width * height];
		
		for (int i = 0; i < toReturn.length; i++)
		{
			toReturn[i] = 0xFFFFFFFF;
		}
		
		return toReturn;
	}
}
