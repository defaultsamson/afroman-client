package ca.afroman.resource;

import ca.afroman.level.GridSize;

public class Vector2DDouble
{
	protected double x;
	protected double y;
	
	public Vector2DDouble(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2DDouble add(double xa, double ya)
	{
		x += xa;
		y += ya;
		
		return this;
	}
	
	public Vector2DDouble alignToGrid(GridSize grid)
	{
		int size = grid.getSize();
		
		if (size > 0)
		{
			// If the number is negative, must offset by the grid size because it will be
			// subtracting a negative modulus offset, therefore moving it one grid space
			// to the right. To counteract this, the grid space is subtracted from the ordinate
			int xOffset = x < 0 ? size : 0;
			int yOffset = y < 0 ? size : 0;
			
			x -= ((x % size) + xOffset);
			y -= ((y % size) + yOffset);
		}
		
		return this;
	}
	
	public Vector2DDouble alignToGridCenter(GridSize grid)
	{
		alignToGrid(grid);
		x += (grid.getSize() / 2);
		y += (grid.getSize() / 2);
		
		return this;
	}
	
	@Deprecated
	public Vector2DDouble alignToGridNearestCorner(GridSize grid)
	{
		// TODO
		// int size = grid.getSize();
		//
		// System.out.println("PreAlignment: (" + getX() + ", " + getY() + ")");
		//
		// Old version. Might work?
		// // if (size > 0)
		// // {
		// // System.out.println("PreAlignment: (" + getX() + ", " + getY() + ")");
		// //
		// // // First uses Math.round((x % size) / size) == 0 to determine if it should merge to the left side
		// // // Then it returns to proper amounts to offset by based on whether the ordinate is positive or negative
		// // int xOffset = Math.round((x % size) / size) == 0 ? (x < 0) ? 1 : 0 : (x < 0) ? size : -size;
		// // int yOffset = Math.round((y % size) / size) == 0 ? (y < 0) ? 1 : 0 : (y < 0) ? size : -size;
		// //
		// // x -= ((x % size) + xOffset);
		// // y -= ((y % size) + yOffset);
		// //
		// // System.out.println("Aligned : (" + getX() + ", " + getY() + ")");
		// // }
		//
		// Newly tested version, doesn't work
		// if (size > 0)
		// {
		// // int xOffset = x < 0 ? size : 0;
		// // int yOffset = y < 0 ? size : 0;
		// //
		// // x -= ((x % size) + xOffset);
		// // y -= ((y % size) + yOffset);
		//
		// if ((x % size) <= size / 2) // upper corner
		// {
		// x -= (x % size);
		// }
		// else
		// {
		// x += size - (x % size) - 1;
		// }
		//
		// if ((y % size) <= size / 2) // upper corner
		// {
		// y -= (y % size); // (y - (y % size));
		// }
		// else // lower corner
		// {
		// y += size - (y % size) - 1; // (y - (y % size));
		// }
		//
		// System.out.println("Aligned : (" + getX() + ", " + getY() + ")");
		// }
		//
		// System.out.println("Aligned : (" + getX() + ", " + getY() + ")");
		
		return this;
	}
	
	@Override
	public Vector2DDouble clone()
	{
		return new Vector2DDouble(x, y);
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public Vector2DDouble setPosition(double x, double y)
	{
		this.x = x;
		this.y = y;
		
		return this;
	}
	
	public Vector2DDouble setPosition(Vector2DDouble pos)
	{
		setPosition(pos.getX(), pos.getY());
		
		return this;
	}
	
	public Vector2DDouble setX(double x)
	{
		this.x = x;
		
		return this;
	}
	
	public Vector2DDouble setY(double y)
	{
		this.y = y;
		
		return this;
	}
	
	public Vector2DInt toVector2DInt()
	{
		return new Vector2DInt((int) x, (int) y);
	}
}
