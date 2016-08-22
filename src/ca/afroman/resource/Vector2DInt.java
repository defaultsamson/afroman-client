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
		// If the grid is on, link to it
		if (grid.getSize() > 0)
		{
			// Offsets the clicked position to fall to the nearest grid position
			if (x < 0 && y < 0)
			{
				x = -(Math.abs(x) - (Math.abs(x) % grid.getSize())) - grid.getSize();
				y = -(Math.abs(y) - (Math.abs(y) % grid.getSize())) - grid.getSize();
			}
			else if (x < 0)
			{
				x = -(Math.abs(x) - (Math.abs(x) % grid.getSize())) - grid.getSize();
				y = (y - (y % grid.getSize()));
			}
			else if (y < 0)
			{
				x = (x - (x % grid.getSize()));
				y = -(Math.abs(y) - (Math.abs(y) % grid.getSize())) - grid.getSize();
			}
			else
			{
				x = (x - (x % grid.getSize()));
				y = (y - (y % grid.getSize()));
			}
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
