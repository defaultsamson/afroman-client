package ca.afroman.gui.build;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.GridSize;
import ca.afroman.resource.Vector2DInt;

public class GuiTileEditor extends GuiGrid
{
	private GuiTextButton layer0edit;
	private GuiTextButton layer1edit;
	private GuiTextButton layer2edit;
	private GuiTextButton layer3edit;
	private GuiTextButton layer4edit;
	private GuiTextButton layer5edit;
	
	private GuiTextButton layer0show;
	private GuiTextButton layer1show;
	private GuiTextButton layer2show;
	private GuiTextButton layer3show;
	private GuiTextButton layer4show;
	private GuiTextButton layer5show;
	
	public GuiTileEditor()
	{
		super();
		
		layer0show = new GuiTextButton(this, 00, 5, 18 + (18 * 0), 32, blackFont, "L1 X");
		layer1show = new GuiTextButton(this, 10, 5, 18 + (18 * 1), 32, blackFont, "L2 X");
		layer2show = new GuiTextButton(this, 20, 5, 18 + (18 * 2), 32, blackFont, "L3 X");
		layer3show = new GuiTextButton(this, 30, 5, 24 + (18 * 3), 32, blackFont, "L4 X");
		layer4show = new GuiTextButton(this, 40, 5, 24 + (18 * 4), 32, blackFont, "L5 X");
		layer5show = new GuiTextButton(this, 50, 5, 24 + (18 * 5), 32, blackFont, "L6 X");
		
		buttons.add(layer0show);
		buttons.add(layer1show);
		buttons.add(layer2show);
		buttons.add(layer3show);
		buttons.add(layer4show);
		buttons.add(layer5show);
		
		layer0edit = new GuiTextButton(this, 01, 39, 18 + (18 * 0), 13, blackFont, "E");
		layer1edit = new GuiTextButton(this, 11, 39, 18 + (18 * 1), 13, blackFont, "E");
		layer2edit = new GuiTextButton(this, 21, 39, 18 + (18 * 2), 13, blackFont, "E");
		layer3edit = new GuiTextButton(this, 31, 39, 24 + (18 * 3), 13, blackFont, "E");
		layer4edit = new GuiTextButton(this, 41, 39, 24 + (18 * 4), 13, blackFont, "E");
		layer5edit = new GuiTextButton(this, 51, 39, 24 + (18 * 5), 13, blackFont, "E");
		
		buttons.add(layer0edit);
		buttons.add(layer1edit);
		buttons.add(layer2edit);
		buttons.add(layer3edit);
		buttons.add(layer4edit);
		buttons.add(layer5edit);
		
		updateButtons();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(28, 6), "Layers");
	}
	
	@Override
	public void init()
	{
		
	}
	
	@Override
	public void keyTyped()
	{
		super.keyTyped();
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		super.pressAction(buttonID);
		
		// Rids of the click so that the Level doesn't get it
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			ClientLevel level = ClientGame.instance().getCurrentLevel();
			
			switch (buttonID)
			{
				case 00:
					level.showLayer0 = !level.showLayer0;
					break;
				case 01:
					level.editLayer = 0;
					break;
				case 10:
					level.showLayer1 = !level.showLayer1;
					break;
				case 11:
					level.editLayer = 1;
					break;
				case 20:
					level.showLayer2 = !level.showLayer2;
					break;
				case 21:
					level.editLayer = 2;
					break;
				case 30:
					level.showLayer3 = !level.showLayer3;
					break;
				case 31:
					level.editLayer = 3;
					break;
				case 40:
					level.showLayer4 = !level.showLayer4;
					break;
				case 41:
					level.editLayer = 4;
					break;
				case 50:
					level.showLayer5 = !level.showLayer5;
					break;
				case 51:
					level.editLayer = 5;
					break;
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
		super.releaseAction(buttonID);
	}
	
	@Override
	public void updateButtons()
	{
		super.updateButtons();
		
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			ClientLevel level = ClientGame.instance().getCurrentLevel();
			
			layer0show.setText("L1 " + (level.showLayer0 ? "O" : "X"));
			layer1show.setText("L2 " + (level.showLayer1 ? "O" : "X"));
			layer2show.setText("L3 " + (level.showLayer2 ? "O" : "X"));
			layer3show.setText("L4 " + (level.showLayer3 ? "O" : "X"));
			layer4show.setText("L5 " + (level.showLayer4 ? "O" : "X"));
			layer5show.setText("L6 " + (level.showLayer5 ? "O" : "X"));
			
			// Sets everything to false before setting one of them to true
			layer0edit.setEnabled(true);
			layer1edit.setEnabled(true);
			layer2edit.setEnabled(true);
			layer3edit.setEnabled(true);
			layer4edit.setEnabled(true);
			layer5edit.setEnabled(true);
			
			switch (level.editLayer)
			{
				case 0:
					layer0edit.setEnabled(false);
					break;
				case 1:
					layer1edit.setEnabled(false);
					break;
				case 2:
					layer2edit.setEnabled(false);
					break;
				case 3:
					layer3edit.setEnabled(false);
					break;
				case 4:
					layer4edit.setEnabled(false);
					break;
				case 5:
					layer5edit.setEnabled(false);
					break;
			}
		}
	}
}
