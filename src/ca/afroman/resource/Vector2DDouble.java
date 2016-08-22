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
	
	public Vector2DDouble alignToGridCenter(GridSize grid)
	{
		alignToGrid(grid);
		x += (grid.getSize() / 2);
		y += (grid.getSize() / 2);
		
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
