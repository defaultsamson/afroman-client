package ca.pixel.game.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ca.pixel.game.Game;
import ca.pixel.game.assets.Texture;

public class LightMap extends Texture
{
	public LightMap(int width, int height)
	{
		super(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
	}
	
	public void drawLight(int x, int y, int radius)
	{
		drawLight(x, y, radius, ColourUtil.TRANSPARENT);
	}
	
	public void drawLight(int x, int y, int radius, Color rgbColour)
	{
		int width = radius * 2;
		int height = width;
		
		if (x + width > 0 && y + height > 0 && x < getWidth() && y < getHeight())
		{
			BufferedImage lightTexture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
			// Polygon shape = new RegularPolygon(x, y, width, 10);
			Rectangle2D shape = new Rectangle2D.Float(0, 0, width, height);
			// Rectangle2D is more efficient than Ellipse2D
			
			Color[] gradientColours = new Color[] { rgbColour, ColourUtil.AMBIENT_COLOUR };
			float[] gradientFractions = new float[] { 0.0F, 1.0F };
			Paint paint = new RadialGradientPaint(new Point2D.Float(radius, radius), radius, gradientFractions, gradientColours);
			
			// Fills the circle with the gradient
			Graphics2D graphics = lightTexture.createGraphics();
			
			if (Game.instance().isHitboxDebugging())
			{
				graphics.drawRect((int) shape.getX(), (int) shape.getY(), (int) shape.getWidth() - 1, (int) shape.getHeight() - 1);;
			}
			
			graphics.setPaint(paint);
			graphics.fill(shape);
			
			// Loop over pixels within light radius
			for (int iy = 0; iy < height; iy++)
			{
				for (int ix = 0; ix < width; ix++)
				{
					int lightMapX = x + ix;
					int lightMapY = y + iy;
					
					// If it's off-screen, don't try to render it.
					if (lightMapX < 0 || lightMapY < 0 || lightMapX >= image.getWidth() || lightMapY >= image.getHeight()) continue;
					
					int lightPixel = lightTexture.getRGB(ix, iy);
					int lightmapPixel = image.getRGB(lightMapX, lightMapY);
					
					// Only set the pixel to it if the new colour doesn't go below the ambient colour.
					
					if (lightmapPixel == ColourUtil.BUFFER_WASTE || (lightmapPixel >> 24 & 0xFF) > (lightPixel >> 24 & 0xFF))
					{
						image.setRGB(lightMapX, lightMapY, lightPixel);
					}
				}
			}
			
			lightTexture.flush();
		}
	}
	
	public void clear()
	{
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				image.setRGB(x, y, ColourUtil.BUFFER_WASTE);
			}
		}
	}
	
	/**
	 * Fills all blank spots with ambient colour.
	 */
	public void patch()
	{
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				if (image.getRGB(x, y) == ColourUtil.BUFFER_WASTE) image.setRGB(x, y, ColourUtil.AMBIENT_COLOUR.getRGB());
			}
		}
	}
}
