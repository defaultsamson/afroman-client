package ca.pixel.game.assets;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

import ca.pixel.game.gfx.ColourUtil;

public class Texture extends Asset
{
	protected BufferedImage image;
	protected Graphics2D graphics;
	
	public Texture(BufferedImage image)
	{
		this.image = image;
		this.graphics = image.createGraphics();
	}
	
	public static Texture fromResource(String path)
	{
		// Loads the image
		BufferedImage inImage = null;
		try
		{
			inImage = ImageIO.read(Texture.class.getResourceAsStream(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Puts the loaded image into a BufferedImage capable of transparency
		BufferedImage image = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		image.createGraphics().drawImage(inImage, 0, 0, null);
		
		// Filters all the purple colours to transparent
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				int argb = image.getRGB(x, y);
				if (argb == ColourUtil.ALPHA_COLOUR1 || argb == ColourUtil.ALPHA_COLOUR2)
				{
					// System.out.println("Editing");
					image.setRGB(x, y, 0x00000000);
				}
			}
		}
		
		// Colour format
		// 0xAARRGGBB
		//
		// To isolate each colour channel
		//
		// int alpha = (pixels[i] >>> 24) & 0xFF;
		// int red = (pixels[i] >>> 16) & 0xFF;
		// int green = (pixels[i] >>> 8) & 0xFF;
		// int blue = (pixels[i] >>> 0) & 0xFF;
		
		return new Texture(image);
	}
	
	public int getWidth()
	{
		return image.getWidth();
	}
	
	public int getHeight()
	{
		return image.getHeight();
	}
	
	@Override
	public Texture clone()
	{
		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
		return new Texture(new BufferedImage(cm, raster, isAlphaPremultiplied, null));
	}
	
	public Graphics2D getGraphics()
	{
		return graphics;
	}
	
	/**
	 * Flips this horizontally.
	 */
	public void flipX()
	{
		AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
		at.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
	}
	
	/**
	 * Flips this vertically.
	 */
	public void flipY()
	{
		AffineTransform at = AffineTransform.getScaleInstance(1, -1);
		at.translate(0, -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
	}
	
	public void rotate(double degrees)
	{
		AffineTransform at = AffineTransform.getTranslateInstance(this.getWidth() / 2, this.getHeight() / 2);
		at.rotate(Math.toRadians(degrees));
		at.translate(-this.getWidth() / 2, -this.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
	}
	
	public BufferedImage getImage()
	{
		return image;
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
		return new Texture(getSubImage(x, y, width, height));
	}
	
	/**
	 * Gets the pixel data of a sub-image of an image's pixels data.
	 * 
	 * @param x the x ordinate to get from in <b>inPixel</b>
	 * @param y the y ordinate to get from in <b>inPixel</b>
	 * @param width the width of the area of <b>inPixel</b>'s image to get
	 * @param height the height of the area of <b>inPixel</b>'s image to get
	 */
	public BufferedImage getSubImage(int x, int y, int width, int height)
	{
		return image.getSubimage(x, y, width, height);
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
		// Only draw if what's trying to be drawn is within the bounds of this
		if (x < this.getWidth() && y < this.getHeight() && x + toDraw.getWidth() > 0 && y + toDraw.getHeight() > 0)
		{
			graphics.drawImage(toDraw.image, x, y, null);
		}
	}
}
