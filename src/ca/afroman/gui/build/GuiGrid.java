package ca.afroman.gui.build;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.level.ClientLevel;

public class GuiGrid extends GuiScreen
{
	protected GuiTextButton grid;
	
	public GuiGrid()
	{
		super(null);
		
		grid = new GuiTextButton(this, 500, 200 - 4 - 12, 3, 41 + 12, blackFont, "Grid " + ClientGame.instance().getCurrentLevel().grid.getSize());
		
		addButton(grid);
	}
	
	@Override
	public void pressAction(int buttonID, boolean isLeft)
	{
		// Rids of the click so that the Level doesn't get it
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			ClientLevel level = ClientGame.instance().getCurrentLevel();
			
			switch (buttonID)
			{
				case 500:
					level.grid = level.grid.getNext();
					break;
			}
			
			updateButtons();
		}
	}
	
	public void updateButtons()
	{
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			grid.setText("Grid " + ClientGame.instance().getCurrentLevel().grid.getSize());
		}
	}
}
