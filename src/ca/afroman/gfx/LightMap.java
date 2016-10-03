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
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;

public class LightMap extends Texture
{
	public static boolean oldLightMixing = false;
	public static final Color DEFAULT_AMBIENT = new Color(0F, 0F, 0F, 0.64F);
	private static final Color CHEAP_AMBIENT = new Color(0F, 0F, 0F, 0.5F); // Must be this for the bit shift to work in the multiply
	private static Texture filter = Assets.getTexture(AssetType.FILTER);
	
	public static final Vector2DInt PATCH_POSITION = new Vector2DInt(0, 0);
	
	private static int multiplyPixels(int x, int y, Color ambientColour)
	{
		// TODO add back RGB for coloured lights?
		// int xb = (x) & 0xFF;
		// int yb = (y) & 0xFF;
		// int b = (xb * yb) / 255;
		//
		// int xg = (x >> 8) & 0xFF;
		// int yg = (y >> 8) & 0xFF;
		// int g = (xg * yg) / 255;
		//
		// int xr = (x >> 16) & 0xFF;
		// int yr = (y >> 16) & 0xFF;
		// int r = (xr * yr) / 255;
		
		int xa = (x >> 24) & 0xFF;
		int ya = (y >> 24) & 0xFF;
		int a;
		if (Options.instance().lighting == LightMapState.CHEAP)
		{
			a = (xa * ya) >> 7; // Math.min(255, xa + ya)
		}
		else
		{
			a = (xa * ya) / ambientColour.getAlpha(); // Math.min(255, xa + ya)
		}
		
		return (x & 0x00FFFFFF) | (a << 24); // (b) | (g << 8) | (r << 16) | (a << 24)
	}
	
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
	
	// public void putLight(ClientLightEntity light)
	// {
	// // Loop over pixels within light radius
	// for (int iy = 0; iy < light.getRadius() * 2; iy++)
	// {
	// for (int ix = 0; ix < light.getRadius() * 2; ix++)
	// {
	// // If it has a level, use the level's worldToScreen() coordinates, otherwise just draw it to the screen with its current coordinates
	// int lightMapX = (int) (light.getLevel() != null ? light.getLevel().worldToScreenX(light.getX() - light.getRadius()) : light.getX() - light.getRadius()) + ix;
	// int lightMapY = (int) (light.getLevel() != null ? light.getLevel().worldToScreenY(light.getY() - light.getRadius()) : light.getY() - light.getRadius()) + iy;
	//
	// // If it's off-screen, don't try to render it.
	// if (lightMapX < 0 || lightMapY < 0 || lightMapX >= getImage().getWidth() || lightMapY >= getImage().getHeight()) continue;
	//
	// int lightPixel = ((Texture) light.getAsset()).getImage().getRGB(ix, iy);
	// int lightmapPixel = getImage().getRGB(lightMapX, lightMapY);
	//
	// if (oldLightMixing)
	// {
	// // Only set the pixel to it if the new colour doesn't go below the ambient colour.
	// if (lightmapPixel == ColourUtil.BUFFER_WASTE || (lightmapPixel >> 24 & 0xFF) > (lightPixel >> 24 & 0xFF))
	// {
	// getImage().setRGB(lightMapX, lightMapY, lightPixel);
	// }
	// }
	// else // New and improved light mixing
	// {
	// if (lightmapPixel == ColourUtil.BUFFER_WASTE)
	// {
	// getImage().setRGB(lightMapX, lightMapY, lightPixel);
	// }
	// else // If it's not a BUFFER, multiply the current value together to get the new pixel
	// {
	// getImage().setRGB(lightMapX, lightMapY, multiplyPixels(lightPixel, lightmapPixel, getAmbientColour()));
	// }
	// }
	// }
	// }
	// }
	
	public void clear()
	{
		for (int y = 0; y < getImage().getHeight(); ++y)
		{
			for (int x = 0; x < getImage().getWidth(); ++x)
			{
				getImage().setRGB(x, y, ColourUtil.BUFFER_WASTE);
			}
		}
	}
	
	/**
	 * @deprecated Moving to new renderLight()
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 */
	@Deprecated
	public void drawLight(Vector2DInt pos, double radius)
	{
		drawLight(pos, radius, ColourUtil.TRANSPARENT);
	}
	
	/**
	 * @deprecated Moving to new renderLight()
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @param rgbColour
	 */
	@Deprecated
	public void drawLight(Vector2DInt pos, double radius, Color rgbColour)
	{
		int drawX = pos.getX();
		int drawY = pos.getY();
		int drawRadius = (int) radius;
		int drawWidth = (int) (radius * 2);
		int drawHeight = drawWidth;
		
		if (drawX + drawWidth > 0 && drawY + drawHeight > 0 && drawX < getWidth() && drawY < getHeight())
		{
			BufferedImage lightTexture = new BufferedImage(drawWidth, drawHeight, BufferedImage.TYPE_INT_ARGB);
			
			// Polygon shape = new RegularPolygon(x, y, width, 10);
			Rectangle2D shape = new Rectangle2D.Float(0, 0, drawWidth, drawHeight);
			// Rectangle2D is more efficient than Ellipse2D
			
			Color[] gradientColours = new Color[] { rgbColour, getAmbientColour() };
			float[] gradientFractions = new float[] { 0.0F, 1.0F };
			Paint paint = new RadialGradientPaint(new Point2D.Float(drawRadius, drawRadius), drawRadius, gradientFractions, gradientColours);
			
			// Fills the circle with the gradient
			Graphics2D graphics = lightTexture.createGraphics();
			
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
					if (lightMapX < 0 || lightMapY < 0 || lightMapX >= getImage().getWidth() || lightMapY >= getImage().getHeight()) continue;
					
					int lightPixel = lightTexture.getRGB(ix, iy);
					int lightmapPixel = getImage().getRGB(lightMapX, lightMapY);
					
					if (oldLightMixing)
					{
						// Only set the pixel to it if the new colour doesn't go below the ambient colour.
						if (lightmapPixel == ColourUtil.BUFFER_WASTE || (lightmapPixel >> 24 & 0xFF) > (lightPixel >> 24 & 0xFF))
						{
							getImage().setRGB(lightMapX, lightMapY, lightPixel);
						}
					}
					else // New and improved light mixing
					{
						if (lightmapPixel == ColourUtil.BUFFER_WASTE)
						{
							getImage().setRGB(lightMapX, lightMapY, lightPixel);
						}
						else // If it's not a BUFFER, multiply the current value together to get the new pixel
						{
							getImage().setRGB(lightMapX, lightMapY, multiplyPixels(lightPixel, lightmapPixel, getAmbientColour()));
						}
					}
				}
			}
			
			lightTexture.flush();
		}
	}
	
	public Color getAmbientColour()
	{
		return Options.instance().lighting == LightMapState.CHEAP ? CHEAP_AMBIENT : ambientColour;
	}
	
	/**
	 * Fills all blank spots with ambient colour, and applies the filter.
	 */
	public void patch()
	{
		for (int y = 0; y < getImage().getHeight(); ++y)
		{
			for (int x = 0; x < getImage().getWidth(); ++x)
			{
				if (getImage().getRGB(x, y) == ColourUtil.BUFFER_WASTE)
				{
					getImage().setRGB(x, y, getAmbientColour().getRGB());
				}
			}
		}
		
		if (Options.instance().lighting == LightMapState.ON) this.draw(filter, PATCH_POSITION);
	}
}
