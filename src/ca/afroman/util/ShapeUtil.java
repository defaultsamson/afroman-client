package ca.afroman.util;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;

public class ShapeUtil
{
	public static boolean areColliding(int inX, int inY, int inWidth, int inHeight, int outX, int outY, int outWidth, int outHeight)
	{
		return inX < outX + outWidth && inX + inWidth > outX && inY < outY + outHeight && inY + inHeight > outY;
	}
	
	public static Rectangle2D.Double pointsToRectangle(final Vector2DDouble pos1, final Vector2DDouble pos2)
	{
		double x, y, width, height;
		
		// Finds the x, y, and height depending on which ones are greater than the other.
		// Have to do this because Java doesn't support rectangles with negative width or height
		if (pos1.getX() > pos2.getX())
		{
			width = pos1.getX() - pos2.getX();
			x = pos2.getX();
		}
		else
		{
			width = pos2.getX() - pos1.getX() + 1;
			x = pos2.getX() - width;
		}
		
		if (pos1.getY() > pos2.getY())
		{
			height = pos1.getY() - pos2.getY();
			y = pos2.getY();
		}
		else
		{
			height = pos2.getY() - pos1.getY() + 1;
			y = pos1.getY() - 1;
		}
		
		return new Rectangle2D.Double(x, y, width, height);
	}
	
	public static Rectangle pointsToRectangle(final Vector2DInt pos1, final Vector2DInt pos2)
	{
		int x, y, width, height;
		
		// Finds the x, y, and height depending on which ones are greater than the other.
		// Have to do this because Java doesn't support rectangles with negative width or height
		if (pos1.getX() > pos2.getX())
		{
			width = pos1.getX() - pos2.getX();
			x = pos2.getX();
		}
		else
		{
			width = pos2.getX() - pos1.getX() + 1;
			x = pos2.getX() - width;
		}
		
		if (pos1.getY() > pos2.getY())
		{
			height = pos1.getY() - pos2.getY();
			y = pos2.getY();
		}
		else
		{
			height = pos2.getY() - pos1.getY() + 1;
			y = pos1.getY() - 1;
		}
		
		return new Rectangle(x, y, width, height);
	}
}
