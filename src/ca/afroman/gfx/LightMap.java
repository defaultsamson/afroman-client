package ca.afroman.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;

public class LightMap extends Texture
{
	private Color ambientColour;
	
	public LightMap(int width, int height)
	{
		this(width, height, ColourUtil.AMBIENT_COLOUR);
	}
	
	public LightMap(int width, int height, Color ambientColour)
	{
		super(AssetType.INVALID, new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
		
		this.ambientColour = ambientColour;
	}
	
	public void drawLight(double x, double y, double radius)
	{
		drawLight(x, y, radius, ColourUtil.TRANSPARENT);
	}
	
	public void drawLight(double x, double y, double radius, Color rgbColour)
	{
		int drawX = (int) x;
		int drawY = (int) y;
		int drawRadius = (int) radius;
		int drawWidth = (int) (radius * 2);
		int drawHeight = drawWidth;
		
		if (x + drawWidth > 0 && y + drawHeight > 0 && x < getWidth() && y < getHeight())
		{
			BufferedImage lightTexture = new BufferedImage(drawWidth, drawHeight, BufferedImage.TYPE_INT_ARGB);
			
			// Polygon shape = new RegularPolygon(x, y, width, 10);
			Rectangle2D shape = new Rectangle2D.Float(0, 0, drawWidth, drawHeight);
			// Rectangle2D is more efficient than Ellipse2D
			
			Color[] gradientColours = new Color[] { rgbColour, ambientColour };
			float[] gradientFractions = new float[] { 0.0F, 1.0F };
			Paint paint = new RadialGradientPaint(new Point2D.Float(drawRadius, drawRadius), drawRadius, gradientFractions, gradientColours);
			
			// Fills the circle with the gradient
			Graphics2D graphics = lightTexture.createGraphics();
			
			if (ClientGame.instance().isHitboxDebugging())
			{
				graphics.drawRect((int) shape.getX(), (int) shape.getY(), (int) shape.getWidth() - 1, (int) shape.getHeight() - 1);;
			}
			
			graphics.setPaint(paint);
			graphics.fill(shape);
			
			// Loop over pixels within light radius
			for (int iy = 0; iy < drawHeight; iy++)
			{
				for (int ix = 0; ix < drawWidth; ix++)
				{
					int lightMapX = drawX + ix;
					int lightMapY = drawY + iy;
					
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
	 * Fills all blank spots with ambient colour, and applies the filter.
	 */
	public void patch()
	{
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				if (image.getRGB(x, y) == ColourUtil.BUFFER_WASTE) image.setRGB(x, y, ambientColour.getRGB());
			}
		}
		
		this.draw(Assets.getTexture(AssetType.FILTER), 0, 0);
	}
}
