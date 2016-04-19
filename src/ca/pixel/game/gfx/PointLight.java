package ca.pixel.game.gfx;

public class PointLight
{
	protected int x, y, radius;
	
	public PointLight(int x, int y, int radius)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	/*
	public void render2(Texture renderTo)
	{
		for (int i = 0; i < length; i++)
		{
			srcPx = src[srcPos];
			destPx = dest[destPos];
			if (alpha == 255)
			{
				srcR = (srcPx & RED_MASK) >>> 16;
				srcG = (srcPx & GREEN_MASK) >>> 8;
				srcB = srcPx & BLUE_MASK;
			}
			else
			{
				srcR = mult((srcPx & RED_MASK) >>> 16, alpha);
				srcG = mult((srcPx & GREEN_MASK) >>> 8, alpha);
				srcB = mult(srcPx & BLUE_MASK, alpha);
			}
			destR = (destPx & RED_MASK) >>> 16;
			destG = (destPx & GREEN_MASK) >>> 8;
			destB = (destPx & BLUE_MASK);
			
			dest[destPos] = min(srcR + destR, 0xFF) << 16 | min(srcG + destG, 0xFF) << 8 | min(srcB + destB, 0xFF);
			
			srcPos++;
			destPos++;
			
		}
	}
	*/
	
	public void render(int[] lightMapPixels, int width, int height)
	{
		// Color and intensity of light
		int col = 0xFFFFFF;
		float insensity = 1.0F;
		
		// Loop over pixels within light radius
		for (int iy = 0; iy <= radius * 2; iy++)
		{
			for (int ix = radius * 2; ix <= radius * 2; ix++)
			{
				// If out of bounds, continue
				if (ix < 0 || iy < 0 || ix >= width || iy >= height) continue;
				
				// Finds the distance
				double dist = Math.sqrt(((ix - x) * (ix - x)) + ((iy - y) * (iy - y)));
				int old = lightMapPixels[ix + (iy * width)];
				
				float oldR = normaliseRGB((old & 0xFF0000) >> 16);
				float oldG = normaliseRGB((old & 0xFF00) >> 8);
				float oldB = normaliseRGB(old & 0xFF);
				
				float lightR = normaliseRGB((int) (((col & 0xFF0000) >> 16) * radius / (dist * dist)));
				float lightG = normaliseRGB((int) (((col & 0xFF00) >> 8) * radius / (dist * dist)));
				float lightB = normaliseRGB((int) ((col & 0xFF) * radius / (dist * dist)));
				
				int newR = normalToRGB((oldR * lightR) * insensity);
				int newG = normalToRGB((oldG * lightG) * insensity);
				int newB = normalToRGB((oldB * lightB) * insensity);
				
				lightMapPixels[ix + (iy * width)] = (newR << 16) | (newG << 8) | newB;
			}
		}
	}
	
	private static float normaliseRGB(float rgbValue)
	{
		return (rgbValue / 255);
	}
	
	private static int normalToRGB(float rgbValue)
	{
		return Math.round(rgbValue);
	}
}
