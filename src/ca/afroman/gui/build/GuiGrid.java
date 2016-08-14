package ca.afroman.gui.build;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.GridSize;

public class GuiGrid extends GuiScreen
{
	protected GuiTextButton grid;
	
	public GuiGrid()
	{
		super(null);
		
		grid = new GuiTextButton(this, 500, 200 - 4 - 12, 3, 41 + 12, blackFont, "Grid " + ClientGame.instance().getCurrentLevel().grid.getSize());
		
		buttons.add(grid);
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		
	}
	
	@Override
	public void init()
	{
		
	}
	
	@Override
	public void keyTyped()
	{
		
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		// Rids of the click so that the Level doesn't get it
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			ClientLevel level = ClientGame.instance().getCurrentLevel();
			
			switch (buttonID)
			{
				case 500:
					level.grid = GridSize.getNext(level.grid);
					break;
			}
			
			updateButtons();
		}
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		
	}
	
	public void updateButtons()
	{
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			grid.setText("Grid " + ClientGame.instance().getCurrentLevel().grid.getSize());
		}
	}
}
