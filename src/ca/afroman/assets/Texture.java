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
	
	public static Texture fromResource(AssetType type, String path)
	{
		return fromResource(type, path, 0);
	}
	
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
		
		// Filters all the purple colours to transparent
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				int argb = image.getRGB(x, y);
				if (argb == ColourUtil.ALPHA_COLOUR1 || argb == ColourUtil.ALPHA_COLOUR2)
				{
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
		
		return new Texture(type, image, yComparatorOffset);
	}
	
	private BufferedImage image;
	private Graphics2D graphics;
	private int yComparatorOffset;
	
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
	public void draw(Texture toDraw, Vector2DInt pos)
	{
		int x = pos.getX();
		int y = pos.getY();
		
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
	 * Flips this horizontally.
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
	 * Flips this vertically.
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
	
	public int getYComparatorOffset()
	{
		return yComparatorOffset;
	}
	
	@Override
	public void render(Texture renderTo, Vector2DInt pos)
	{
		renderTo.draw(this, pos);
	}
	
	public Texture rotate(double degrees)
	{
		AffineTransform at = AffineTransform.getTranslateInstance(this.getWidth() / 2, this.getHeight() / 2);
		at.rotate(Math.toRadians(degrees));
		at.translate(-this.getWidth() / 2, -this.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		
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
	 * Converts the greyscale to alpha mask.
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
	 * Creates an array of textures from a this.
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
