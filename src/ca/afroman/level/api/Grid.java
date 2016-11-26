package ca.afroman.level.api;

public class Grid
{
	private GridSize grid;
	
	public Grid()
	{
		grid = GridSize.MEDIUM;
	}
	
	public GridSize getGridSize()
	{
		return grid;
	}
	
	public int getSize()
	{
		return grid.getSize();
	}
	
	public void setGridSize(GridSize grid)
	{
		this.grid = grid;
	}
}
