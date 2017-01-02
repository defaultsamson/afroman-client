package ca.afroman.assets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.util.ColourUtil;
import ca.afroman.util.ShapeUtil;

public class Texture extends DrawableAsset implements ITextureDrawable
{
	public static final String TEXTURE_PATH = "/texture/";
	
	/**
	 * Creates a Texture object from the resources within the running jar.
	 * 
	 * @param type the AssetType to assign to the Texture
	 * @param path the path of the audio resource
	 * @return a Texture from the running jar's resources.
	 */
	public static Texture fromResource(AssetType type, String path)
	{
		return fromResource(type, path, 0);
	}
	
	/**
	 * Creates a Texture object from the resources within the running jar.
	 * 
	 * @param type the AssetType to assign to the Texture
	 * @param path the path of the audio resource
	 * @param yComparatorOffset the offset to use in the YComparator
	 * @return a Texture from the running jar's resources.
	 */
	public static Texture fromResource(AssetType type, String path, int yComparatorOffset)
	{
		// Loads the image
		BufferedImage inImage = null;
		try
		{
			inImage = ImageIO.read(Texture.class.getResourceAsStream(TEXTURE_PATH + path));
		}
		catch (IOException e)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Could not read texture from class resource stream", e);
		}
		
		if (inImage == null) return null;
		
		// Puts the loaded image into a BufferedImage capable of transparency
		BufferedImage image = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		image.createGraphics().drawImage(inImage, 0, 0, null);
		
		Texture text = new Texture(type, image, yComparatorOffset);
		text.replaceColour(ColourUtil.ALPHA_COLOUR1, 0x00000000);
		text.replaceColour(ColourUtil.ALPHA_COLOUR2, 0x00000000);
		
		// Colour format
		// 0xAARRGGBB
		//
		// To isolate each colour channel
		//
		// int alpha = (pixels[i] >>> 24) & 0xFF;
		// int red = (pixels[i] >>> 16) & 0xFF;
		// int green = (pixels[i] >>> 8) & 0xFF;
		// int blue = (pixels[i] >>> 0) & 0xFF;
		
		return text;
	}
	
	private BufferedImage image;
	private Graphics2D graphics;
	private int yComparatorOffset;
	
	/**
	 * 
	 * @param type the AssetType to assign to the Texture
	 * @param image the BufferedImage to use internally
	 * @param yComparatorOffset the offset to use in the YComparator
	 */
	public Texture(AssetType type, BufferedImage image, int yComparatorOffset)
	{
		super(type, image.getWidth(), image.getHeight());
		
		this.image = image;
		this.graphics = image.createGraphics();
		this.yComparatorOffset = yComparatorOffset;
	}
	
	@Override
	public Texture clone()
	{
		return clone(getAssetType());
	}
	
	/**
	 * Clones this, but uses the provided AssetType instead of the current one of this.
	 * 
	 * @param newAssetType the new AssetType to use
	 * @return the cloned Texture.
	 */
	public Texture clone(AssetType newAssetType)
	{
		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
		return new Texture(newAssetType, new BufferedImage(cm, raster, isAlphaPremultiplied, null), yComparatorOffset);
	}
	
	@Override
	public void dispose()
	{
		graphics.dispose();
		image.flush();
	}
	
	/**
	 * Superimposes a Texture over this one.
	 * 
	 * @param toDraw the image to draw
	 * @param pos the position to draw <b>toDraw</b> on <b>this</b>
	 */
	public void draw(Texture toDraw, int x, int y)
	{
		// Only draw if what's trying to be drawn is within the bounds of this
		if (ShapeUtil.areColliding(x, y, toDraw.getWidth(), toDraw.getHeight(), 0, 0, getWidth(), getHeight())) // (x < this.getWidth() && y < this.getHeight() && x + toDraw.getWidth() > 0 && y + toDraw.getHeight() > 0)
		{
			graphics.drawImage(toDraw.image, x, y, null);
		}
	}
	
	/**
	 * Superimposes a Texture over this one.
	 * 
	 * @param toDraw the image to draw
	 * @param pos the position to draw <b>toDraw</b> on <b>this</b>
	 */
	public void draw(Texture toDraw, Vector2DInt pos)
	{
		draw(toDraw, pos.getX(), pos.getY());
	}
	
	/**
	 * Superimposes a Texture over this one.
	 * 
	 * @param toDraw the image to draw
	 * @param pos the position to draw <b>toDraw</b> on <b>this</b>
	 */
	public void drawFillRect(Color outlineColour, Color fillColour, final Vector2DInt pos, final int width, final int height)
	{
		// Only draw if what's trying to be drawn is within the bounds of this
		if (ShapeUtil.areColliding(pos.getX(), pos.getY(), width, height, 0, 0, getWidth(), getHeight()))
		{
			Paint oldPaint = getGraphics().getPaint();
			
			getGraphics().setPaint(fillColour);
			getGraphics().fillRect(pos.getX() + 1, pos.getY() + 1, width - 2, height - 2);
			
			getGraphics().setPaint(outlineColour);
			getGraphics().drawRect(pos.getX(), pos.getY(), width - 1, height - 1);
			
			getGraphics().setPaint(oldPaint);
		}
	}
	
	/**
	 * Superimposes a Texture over this one.
	 * 
	 * @param toDraw the image to draw
	 * @param pos the position to draw <b>toDraw</b> on <b>this</b>
	 */
	public void drawRect(Color colour, final Vector2DInt pos, final int width, final int height)
	{
		// Only draw if what's trying to be drawn is within the bounds of this
		if (ShapeUtil.areColliding(pos.getX(), pos.getY(), width, height, 0, 0, getWidth(), getHeight()))
		{
			Paint oldPaint = getGraphics().getPaint();
			
			getGraphics().setPaint(colour);
			getGraphics().drawRect(pos.getX(), pos.getY(), width - 1, height - 1);
			
			getGraphics().setPaint(oldPaint);
		}
	}
	
	/**
	 * Flips this horizontally along the x axis.
	 */
	public Texture flipX()
	{
		AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
		at.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		
		return this;
	}
	
	/**
	 * Flips this vertically along the y axis.
	 */
	public Texture flipY()
	{
		AffineTransform at = AffineTransform.getScaleInstance(1, -1);
		at.translate(0, -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		
		return this;
	}
	
	@Override
	public Texture getDisplayedTexture()
	{
		return this;
	}
	
	/**
	 * @return the Graphics2D object of the internal BufferedImage in this.
	 */
	public Graphics2D getGraphics()
	{
		return graphics;
	}
	
	/**
	 * @return the raw BufferedImage in this.
	 */
	public BufferedImage getImage()
	{
		return image;
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
	 * Gets a sub-section of this.
	 * 
	 * @param x the x ordinate to start selecting from
	 * @param y the y ordinate to start selecting from
	 * @param width the width of the selection
	 * @param height the height of the selection
	 * @return the sub-section of this with the given parameters.
	 */
	public Texture getSubTexture(AssetType newType, int x, int y, int width, int height)
	{
		return getSubTexture(newType, x, y, width, height, 0);
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
	public Texture getSubTexture(AssetType newType, int x, int y, int width, int height, int yComparatorOffset)
	{
		return new Texture(newType, getSubImage(x, y, width, height), yComparatorOffset);
	}
	
	/**
	 * Gets the offset (in pixels) to use for when Entities are being
	 * compared via the YComparator class. This is useful for Textures
	 * where the designed bottom of the drawn object is not the bottom
	 * of the image.
	 * 
	 * @return the offset
	 */
	public int getYComparatorOffset()
	{
		return yComparatorOffset;
	}
	
	@Override
	public void render(Texture renderTo, int x, int y)
	{
		renderTo.draw(this, x, y);
	}
	
	@Override
	public Texture replaceColour(int from, int to)
	{
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				if (image.getRGB(x, y) == from)
				{
					image.setRGB(x, y, to);
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Rotates this in degrees.
	 * 
	 * @param degrees
	 * @return
	 */
	public Texture rotate(double degrees)
	{
		// TODO this will give errors for anything that's not rotated by 90 degrees
		AffineTransform at = AffineTransform.getTranslateInstance(this.getHeight() / 2, this.getWidth() / 2);
		
		at.rotate(Math.toRadians(degrees));
		at.translate(-this.getWidth() / 2, -this.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		
		width = image.getWidth();
		height = image.getWidth();
		
		return this;
	}
	
	/**
	 * Scales this texture.
	 * 
	 * @param xScale the horizontal scaling amplitude
	 * @param yScale the vertical scaling amplitude
	 */
	public void scale(double xScale, double yScale)
	{
		AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
	}
	
	/**
	 * Converts the greyscale (sampled from the red channel) to alpha mask.
	 */
	public void setFromGreyscaleToAlphaMask()
	{
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int origRGB = this.image.getRGB(x, y);
				int origColor = origRGB & 0x00FFFFFF; // mask away any alpha present
				
				int newRGB = (origColor & 0x00FF0000) << 8; // shift red into alpha bits
				
				this.image.setRGB(x, y, newRGB);
			}
		}
	}
	
	/**
	 * Creates an array of textures from this, with the same AssetType.
	 * <p>
	 * <b>WARNING: </b> This constructor assumes that the width and height of each texture
	 * is the same. If not all the textures have the same dimensions, this will break.
	 * 
	 * @param xColumns how many textures there are in the horizontal plane
	 * @param yRows how many textures there are in the vertical plane
	 */
	public Texture[] toTextureArray(int xColumns, int yRows)
	{
		int subTextWidth = this.getWidth() / xColumns;
		int subTextHeight = this.getHeight() / yRows;
		
		Texture[] textures = new Texture[xColumns * yRows];
		
		for (int y = 0; y < yRows; y++)
		{
			for (int x = 0; x < xColumns; x++)
			{
				textures[(y * xColumns) + x] = this.getSubTexture(getAssetType(), x * subTextWidth, y * subTextHeight, subTextWidth, subTextHeight);
			}
		}
		
		return textures;
	}
}
