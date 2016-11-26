package ca.afroman.gui.build;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiIconButton;
import ca.afroman.gui.GuiMenuOutline;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.resource.Vector2DInt;

public class GuiLevelSelect extends GuiMenuOutline
{
	private GuiIconButton prev;
	private GuiIconButton next;
	private GuiTextButton done;
	
	public GuiLevelSelect(GuiScreen parent, boolean isInLevel)
	{
		super(parent, !isInLevel, false);
		
		int yStart = 20;
		int ySpacing = 22;
		
		int width = 102;
		int spacing = 3;
		
		prev = new GuiIconButton(this, 1001, (ClientGame.WIDTH / 2) - (72 / 2) - 16 - 4, 18 + (ySpacing * 4) + 6, 16, Assets.getTexture(AssetType.ICON_NEXT).clone().flipX());
		next = new GuiIconButton(this, 1002, (ClientGame.WIDTH / 2) + (72 / 2) + 4, 18 + (ySpacing * 4) + 6, 16, Assets.getTexture(AssetType.ICON_NEXT).clone());
		done = new GuiTextButton(this, 1000, (ClientGame.WIDTH / 2) - (72 / 2), 18 + (ySpacing * 4) + 6, 72, blackFont, "Quit");
		
		addButton(prev);
		addButton(next);
		addButton(done);
		
		// Draws the level buttons
		int counter = 0;
		int row = 0;
		for (LevelType level : LevelType.values())
		{
			boolean isEvenNum = (counter & 1) == 0;
			
			int buttonX = (ClientGame.WIDTH / 2) + (isEvenNum ? -(width + spacing) : spacing);
			int buttonY = yStart + (ySpacing * row);
			
			if (!isEvenNum) row++;
			
			// Add each player in the last as a button. Only lets the host of the server edit
			GuiTextButton button = new GuiTextButton(this, counter, buttonX, buttonY, width, blackFont, level.getName());
			this.addButton(button);
			
			counter++;
		}
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15 - 6), "Level Selection");
	}
	
	@Override
	public void pressAction(int buttonID, boolean isLeft)
	{
		
	}
	
	@Override
	public void releaseAction(int buttonID, boolean isLeft)
	{
		switch (buttonID)
		{
			default:
				LevelType type = LevelType.fromOrdinal(buttonID);
				
				Level level = ClientGame.instance().getLevel(type);
				if (level != null)
				{
					ClientGame.instance().setIsBuildMode(true);
					ClientGame.instance().setCurrentScreen(null);
					ClientGame.instance().setCurrentLevel(level);
					level.loadBuildMode(level.getBuildMode());
				}
				break;
			case 1000: // Back
				ClientGame.instance().setIsInGame(false);
				break;
			case 1001: // Prev
				break;
			case 1002: // Next
				break;
		}
	}
}
