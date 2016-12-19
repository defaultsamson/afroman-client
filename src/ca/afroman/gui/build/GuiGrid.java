package ca.afroman.gui.build;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.level.api.Grid;

public class GuiGrid extends GuiScreen
{
	protected GuiTextButton gridButton;
	private Grid grid;
	
	public GuiGrid(Grid grid)
	{
		super(null);
		
		this.grid = grid;
		
		gridButton = new GuiTextButton(this, 500, 200 - 4 - 12, 3, 41 + 12, blackFont, "Grid " + grid.getSize());
		
		addButton(gridButton);
	}
	
	@Override
	public void pressAction(int buttonID, boolean isLeft)
	{
		// Rids of the click so that the Level doesn't get it
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			switch (buttonID)
			{
				case 500:
					grid.setGridSize(grid.getGridSize().getNext());
					break;
			}
			
			updateButtons();
		}
	}
	
	public void updateButtons()
	{
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			gridButton.setText("Grid " + grid.getSize());
		}
	}
}
