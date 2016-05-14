package ca.afroman.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Texture;
import ca.afroman.entity.api.ClientAssetEntity;
import ca.afroman.gfx.ColourUtil;
import ca.afroman.gfx.LightMap;
import ca.afroman.level.ClientLevel;

public class ClientLightEntity extends ClientAssetEntity
{
	private double radius;
	
	public ClientLightEntity(int id, ClientLevel level, double x, double y, double radius)
	{
		super(id, level, AssetType.INVALID, x, y, radius, radius);
		
		radius = width / 2;
		
		asset = getLightTexture(radius, (level != null ? getLevel().getLightMap().getAmbientColour() : LightMap.DEFAULT_AMBIENT));
	}
	
	@Override
	public void render(Texture renderTo)
	{
		if (renderTo instanceof LightMap)
		{
			((LightMap) renderTo).putLight(this);
		}
		else
		{
			asset.render(renderTo, getLevel().worldToScreenX(x) - (int) radius, getLevel().worldToScreenY(y) - (int) radius);
		}
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public static Texture getLightTexture(double radius, Color ambientColour)
	{
		return getLightTexture(radius, ambientColour, ColourUtil.TRANSPARENT);
	}
	
	public static Texture getLightTexture(double radius, Color ambientColour, Color lightColour)
	{
		int drawRadius = (int) radius;
		int drawWidth = (int) (radius * 2);
		int drawHeight = drawWidth;
		
		BufferedImage lightTexture = new BufferedImage(drawWidth, drawHeight, BufferedImage.TYPE_INT_ARGB);
		
		// Polygon shape = new RegularPolygon(x, y, width, 10);
		Rectangle2D shape = new Rectangle2D.Float(0, 0, drawWidth, drawHeight);
		// Rectangle2D is more efficient than Ellipse2D
		
		Color[] gradientColours = new Color[] { lightColour, ambientColour };
		float[] gradientFractions = new float[] { 0.0F, 1.0F };
		Paint paint = new RadialGradientPaint(new Point2D.Float(drawRadius, drawRadius), drawRadius, gradientFractions, gradientColours);
		
		// Fills the circle with the gradient
		Graphics2D graphics = lightTexture.createGraphics();
		
		graphics.setPaint(paint);
		graphics.fill(shape);
		
		return new Texture(AssetType.INVALID, lightTexture);
	}
}
