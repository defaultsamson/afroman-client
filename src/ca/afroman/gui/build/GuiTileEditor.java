package ca.afroman.gui.build;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DInt;

public class GuiTileEditor extends GuiGrid
{
	private GuiTextButton layer0edit;
	private GuiTextButton layer1edit;
	private GuiTextButton layer2edit;
	private GuiTextButton layer3edit;
	private GuiTextButton layer4edit;
	private GuiTextButton layer5edit;
	private GuiTextButton layer6edit;
	
	private GuiTextButton layer0show;
	private GuiTextButton layer1show;
	private GuiTextButton layer2show;
	private GuiTextButton layer3show;
	private GuiTextButton layer4show;
	private GuiTextButton layer5show;
	private GuiTextButton layer6show;
	
	private GuiTextButton tileMenu;
	
	public GuiTileEditor()
	{
		super();
		
		layer0show = new GuiTextButton(this, 00, 5, 26 + (18 * 5), 32, blackFont, "L1 X");
		layer1show = new GuiTextButton(this, 10, 5, 26 + (18 * 4), 32, blackFont, "L2 X");
		layer2show = new GuiTextButton(this, 20, 5, 26 + (18 * 3), 32, blackFont, "L3 X");
		layer3show = new GuiTextButton(this, 30, 5, 3 + (18 * 2), 32, blackFont, "L4 X");
		layer4show = new GuiTextButton(this, 40, 5, 3 + (18 * 1), 32, blackFont, "L5 X");
		layer5show = new GuiTextButton(this, 50, 5, 3 + (18 * 0), 32, blackFont, "L6 X");
		layer6show = new GuiTextButton(this, 60, 5, 5 + (18 * 3), 32, blackFont, "LM X");
		
		addButton(layer0show);
		addButton(layer1show);
		addButton(layer2show);
		addButton(layer3show);
		addButton(layer4show);
		addButton(layer5show);
		addButton(layer6show);
		
		layer0edit = new GuiTextButton(this, 01, 39, 26 + (18 * 5), 13, blackFont, "E");
		layer1edit = new GuiTextButton(this, 11, 39, 26 + (18 * 4), 13, blackFont, "E");
		layer2edit = new GuiTextButton(this, 21, 39, 26 + (18 * 3), 13, blackFont, "E");
		layer3edit = new GuiTextButton(this, 31, 39, 3 + (18 * 2), 13, blackFont, "E");
		layer4edit = new GuiTextButton(this, 41, 39, 3 + (18 * 1), 13, blackFont, "E");
		layer5edit = new GuiTextButton(this, 51, 39, 3 + (18 * 0), 13, blackFont, "E");
		layer6edit = new GuiTextButton(this, 61, 39, 5 + (18 * 3), 13, blackFont, "E");
		
		addButton(layer0edit);
		addButton(layer1edit);
		addButton(layer2edit);
		addButton(layer3edit);
		addButton(layer4edit);
		addButton(layer5edit);
		addButton(layer6edit);
		
		tileMenu = new GuiTextButton(this, 3, 200 - 4 - 12 - (13 * 6) - 5, 3, 13 * 6, blackFont, "Tile List");
		
		addButton(tileMenu);
		
		updateButtons();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(28, 6), "Layers");
	}
	
	@Override
	public void pressAction(int buttonID, boolean isLeft)
	{
		super.pressAction(buttonID, isLeft);
		
		// Rids of the click so that the Level doesn't get it
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			Level level = ClientGame.instance().getCurrentLevel();
			
			switch (buttonID)
			{
				case 00:
					level.toggleTileLayerShow(0);
					break;
				case 01:
					level.setEditingLayer(0);
					break;
				case 10:
					level.toggleTileLayerShow(1);
					break;
				case 11:
					level.setEditingLayer(1);
					break;
				case 20:
					level.toggleTileLayerShow(2);
					break;
				case 21:
					level.setEditingLayer(2);
					break;
				case 30:
					level.toggleTileLayerShow(4);
					break;
				case 31:
					level.setEditingLayer(4);
					break;
				case 40:
					level.toggleTileLayerShow(5);
					break;
				case 41:
					level.setEditingLayer(5);
					break;
				case 50:
					level.toggleTileLayerShow(6);
					break;
				case 51:
					level.setEditingLayer(6);
					break;
				
				// Dynamic Layer is by default layer 3
				case 60:
					level.toggleTileLayerShow(3);
					break;
				case 61:
					level.setEditingLayer(3);
					break;
			}
			
			updateButtons();
		}
	}
	
	@Override
	public void updateButtons()
	{
		super.updateButtons();
		
		if (ClientGame.instance().getCurrentLevel() != null)
		{
			Level level = ClientGame.instance().getCurrentLevel();
			
			layer0show.setText("L1 " + (level.isShowingLayer(0) ? "O" : "X"));
			layer1show.setText("L2 " + (level.isShowingLayer(1) ? "O" : "X"));
			layer2show.setText("L3 " + (level.isShowingLayer(2) ? "O" : "X"));
			layer3show.setText("L4 " + (level.isShowingLayer(4) ? "O" : "X"));
			layer4show.setText("L5 " + (level.isShowingLayer(5) ? "O" : "X"));
			layer5show.setText("L6 " + (level.isShowingLayer(6) ? "O" : "X"));
			layer6show.setText("LM " + (level.isShowingLayer(3) ? "O" : "X"));
			
			// Sets everything to true before setting one of them to false
			layer0edit.setEnabled(true);
			layer1edit.setEnabled(true);
			layer2edit.setEnabled(true);
			layer3edit.setEnabled(true);
			layer4edit.setEnabled(true);
			layer5edit.setEnabled(true);
			layer6edit.setEnabled(true);
			
			switch (level.getEditingLayer())
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
				case 6:
					layer6edit.setEnabled(false);
					break;
			}
		}
	}
}
