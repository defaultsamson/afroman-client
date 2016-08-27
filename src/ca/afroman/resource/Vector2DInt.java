package ca.afroman.resource;

import ca.afroman.level.GridSize;

public class Vector2DInt
{
	private int x;
	private int y;
	
	public Vector2DInt(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2DInt add(int xa, int ya)
	{
		x += xa;
		y += ya;
		
		return this;
	}
	
	public Vector2DInt alignToGrid(GridSize grid)
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
	
	public Vector2DInt alignToGridCenter(GridSize grid)
	{
		alignToGrid(grid);
		x += (grid.getSize() / 2);
		y += (grid.getSize() / 2);
		
		return this;
	}
	
	@Override
	public Vector2DInt clone()
	{
		return new Vector2DInt(x, y);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public Vector2DInt setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		
		return this;
	}
	
	public Vector2DInt setPosition(Vector2DInt pos)
	{
		setPosition(pos.getX(), pos.getY());
		
		return this;
	}
	
	public Vector2DInt setX(int x)
	{
		this.x = x;
		
		return this;
	}
	
	public Vector2DInt setY(int y)
	{
		this.y = y;
		
		return this;
	}
	
	public Vector2DDouble toVector2DDouble()
	{
		return new Vector2DDouble(x, y);
	}
}
