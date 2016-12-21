package ca.afroman.gui;

import java.awt.event.KeyEvent;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.AudioClip;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.light.LightMapState;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;

public class GuiOptionsMenu extends GuiMenuOutline
{
	private static final int MIN_PAGE = 0;
	private static final int MAX_PAGE = 1;
	
	private static int currentPage = 0;
	
	private GuiIconButton prev;
	private GuiIconButton next;
	private GuiTextButton done;
	
	// Page 1
	private int tempScale;
	
	private GuiSlider musicVolume;
	private GuiSlider sfxVolume;
	
	private GuiSlider scale;
	private GuiTextButton applyScale;
	
	private GuiTextButton lighting;
	private GuiTextButton renderOOF;
	
	private GuiTextButton tsync;
	
	// Page 2
	private GuiTextButton up;
	private GuiTextButton down;
	private GuiTextButton left;
	private GuiTextButton right;
	private GuiTextButton interact;
	private GuiTextButton nextItem;
	private GuiTextButton prevItem;
	private GuiTextButton dropItem;
	private GuiTextButton useItem;
	
	private int settingFor = -1;
	
	public GuiOptionsMenu(GuiScreen parent, boolean inGame)
	{
		super(parent, !inGame, false);
		
		int vSpacing = 22;
		
		int width = 102;
		int spacing = 3;
		
		int vStart = 18 + 6;
		
		// Page 1
		prev = new GuiIconButton(this, 201, (ClientGame.WIDTH / 2) - (72 / 2) - 16 - 4, vStart + (vSpacing * 4), 16, Assets.getTexture(AssetType.ICON_NEXT).clone().flipX());
		next = new GuiIconButton(this, 202, (ClientGame.WIDTH / 2) + (72 / 2) + 4, vStart + (vSpacing * 4), 16, Assets.getTexture(AssetType.ICON_NEXT).clone());
		done = new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), vStart + (vSpacing * 4), 72, blackFont, "Done");
		
		musicVolume = new GuiSlider(this, 1, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 0), width, 0, 100, Options.instance().musicVolume, "Music");
		sfxVolume = new GuiSlider(this, 2, (ClientGame.WIDTH / 2) + spacing, vStart + (vSpacing * 0), width, 0, 100, Options.instance().sfxVolume, "SFX");
		musicVolume.setCanRightClick(true);
		sfxVolume.setCanRightClick(true);
		
		tempScale = Options.instance().scale;
		scale = new GuiSlider(this, 3, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 1), width, 1, 8, tempScale, "Scale");
		applyScale = new GuiTextButton(this, 4, (ClientGame.WIDTH / 2) + spacing, vStart + (vSpacing * 1), width, blackFont, "Apply Scale");
		scale.setCanRightClick(true);
		applyScale.setCanRightClick(true);
		
		lighting = new GuiTextButton(this, 5, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 2), width, blackFont, "");
		renderOOF = new GuiTextButton(this, 6, (ClientGame.WIDTH / 2) + spacing, vStart + (vSpacing * 2), width, blackFont, "");
		lighting.setCanRightClick(true);
		renderOOF.setCanRightClick(true);
		
		tsync = new GuiTextButton(this, 7, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 3), width, blackFont, "");
		tsync.setCanRightClick(true);
		
		// Page 2
		vSpacing = 18;
		vStart = 18 + 2;
		
		up = new GuiTextButton(this, 20, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 0), width, blackFont, "");
		up.setCanRightClick(true);
		down = new GuiTextButton(this, 21, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 1), width, blackFont, "");
		down.setCanRightClick(true);
		left = new GuiTextButton(this, 22, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 2), width, blackFont, "");
		left.setCanRightClick(true);
		right = new GuiTextButton(this, 23, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 3), width, blackFont, "");
		right.setCanRightClick(true);
		interact = new GuiTextButton(this, 24, (ClientGame.WIDTH / 2) - width - spacing, vStart + (vSpacing * 4), width, blackFont, "");
		interact.setCanRightClick(true);
		
		nextItem = new GuiTextButton(this, 25, (ClientGame.WIDTH / 2) + spacing, vStart + (vSpacing * 0), width, blackFont, "");
		nextItem.setCanRightClick(true);
		prevItem = new GuiTextButton(this, 26, (ClientGame.WIDTH / 2) + spacing, vStart + (vSpacing * 1), width, blackFont, "");
		prevItem.setCanRightClick(true);
		dropItem = new GuiTextButton(this, 27, (ClientGame.WIDTH / 2) + spacing, vStart + (vSpacing * 2), width, blackFont, "");
		dropItem.setCanRightClick(true);
		useItem = new GuiTextButton(this, 28, (ClientGame.WIDTH / 2) + spacing, vStart + (vSpacing * 3), width, blackFont, "");
		useItem.setCanRightClick(true);
		
		addButtons(currentPage);
	}
	
	private void addButtons(int page)
	{
		clearButtons();
		
		currentPage = page;
		
		switch (page)
		{
			case 0:
				addButton(musicVolume);
				addButton(sfxVolume);
				
				addButton(scale);
				addButton(applyScale);
				
				addButton(lighting);
				addButton(renderOOF);
				
				addButton(tsync);
				break;
			case 1:
				addButton(up);
				addButton(down);
				addButton(left);
				addButton(right);
				addButton(interact);
				
				addButton(nextItem);
				addButton(prevItem);
				addButton(dropItem);
				addButton(useItem);
				
				break;
		}
		
		addButton(done);
		addButton(prev);
		addButton(next);
		
		switch (page)
		{
			case 0:
				if (!Options.instance().hasShownOptionsTip)
				{
					Options.instance().hasShownOptionsTip = true;
					new GuiClickNotification(this, -1, "You can right click options", "for more info on what they do");
				}
				break;
			case 1:
				if (!Options.instance().hasShownControlsTip)
				{
					Options.instance().hasShownControlsTip = true;
					new GuiClickNotification(this, -1, "You can right click controls", "to reset them to their defaults");
				}
				break;
		}
		
		updateControlButtons(true);
		updateButtons();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		String title;
		switch (currentPage)
		{
			default:
				title = "Settings";
				break;
			case 1:
				title = "Controls";
				break;
		}
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15 - 6), title);
	}
	
	private void pendKeySet(int buttonID)
	{
		if (settingFor == -1)
		{
			settingFor = buttonID;
			ClientGame.instance().input().getKeyPress(this);
			for (GuiButton b : buttons)
			{
				b.setEnabled(b.getID() == buttonID);
			}
			updateControlButtons(false);
		}
	}
	
	@Override
	public void pressAction(int buttonID, boolean isLeft)
	{
		if (isLeft)
		{
			switch (buttonID)
			{
				case 20: // Up
				case 21: // Down
				case 22: // Left
				case 23: // Right
				case 24: // Interact
				case 25: // Next item
				case 26: // Last item
				case 27: // Drop item
				case 28: // Use item
					pendKeySet(buttonID);
			}
		}
		else
		{
			settingFor = buttonID;
			
			switch (buttonID)
			{
				case 20: // Up
					setReadKey(Options.DEFAULT_INPUT_UP);
					break;
				case 21: // Down
					setReadKey(Options.DEFAULT_INPUT_DOWN);
					break;
				case 22: // Left
					setReadKey(Options.DEFAULT_INPUT_LEFT);
					break;
				case 23: // Right
					setReadKey(Options.DEFAULT_INPUT_RIGHT);
					break;
				case 24: // Interact
					setReadKey(Options.DEFAULT_INPUT_INTERACT);
					break;
				case 25: // Next item
					setReadKey(Options.DEFAULT_INPUT_NEXT_ITEM);
					break;
				case 26: // Previous item
					setReadKey(Options.DEFAULT_INPUT_PREV_ITEM);
					break;
				case 27: // Drop item
					setReadKey(Options.DEFAULT_INPUT_DROP_ITEM);
					break;
				case 28: // Use item
					setReadKey(Options.DEFAULT_INPUT_USE_ITEM);
					break;
			}
		}
	}
	
	@Override
	public void releaseAction(int buttonID, boolean isLeft)
	{
		switch (buttonID)
		{
			case 0: // Done
				Options.instance().save();
				goToParentScreen();
				break;
			case 201: // Previous
				addButtons(currentPage - 1);
				break;
			case 202: // Next
				addButtons(currentPage + 1);
				break;
			case 1:
				if (!isLeft)
				{
					new GuiClickNotification(this, -1, "Changes the output volume", "of all the music.");
				}
				break;
			case 2:
				if (!isLeft)
				{
					new GuiClickNotification(this, -1, "Changes the output volume", "of all the sound effects.");
				}
				break;
			case 3:
				if (!isLeft)
				{
					new GuiClickNotification(this, -1, "Changes the initial scale", "of the game window.");
				}
				break;
			case 4: // Scale apply
				if (isLeft)
				{
					Options.instance().scale = tempScale;
					ClientGame.instance().resizeGame(ClientGame.WIDTH * Options.instance().scale, ClientGame.HEIGHT * Options.instance().scale, true);
				}
				else
				{
					new GuiClickNotification(this, -1, "Resizes the game screen to the", "size of the selected scale.");
				}
				break;
			case 5: // Lighting
				if (isLeft)
				{
					Options.instance().lighting = Options.instance().lighting.getNext();
					updateButtons();
				}
				else
				{
					new GuiClickNotification(this, -1, "Sets the lighting scheme.", LightMapState.CHEAP + " is for slow computers.");
				}
				break;
			case 6: // Render out of focus
				if (isLeft)
				{
					Options.instance().renderOffFocus = !Options.instance().renderOffFocus;
					updateButtons();
				}
				else
				{
					new GuiClickNotification(this, -1, "Renders the game while the", "window is out of focus.");
				}
				break;
			case 7: // Tick sync
				if (isLeft)
				{
					Options.instance().setTsync(!Options.instance().getTsync());
					updateButtons();
				}
				else
				{
					new GuiClickNotification(this, -1, "Tick-Sync. Syncs the framerate", "of the game with the tick rate.");
				}
				break;
		}
	}
	
	/**
	 * Sets the key read to the one found by the input device.
	 * 
	 * @param readKey
	 */
	public void setReadKey(int readKey)
	{
		if (ClientGame.instance().input().escape.getKeyEvent() != readKey)
		{
			switch (settingFor)
			{
				case 20: // Up
					ClientGame.instance().input().up.setKeyEvents(readKey);
					Options.instance().inputUp = readKey;
					break;
				case 21: // Down
					ClientGame.instance().input().down.setKeyEvents(readKey);
					Options.instance().inputDown = readKey;
					break;
				case 22: // Left
					ClientGame.instance().input().left.setKeyEvents(readKey);
					Options.instance().inputLeft = readKey;
					break;
				case 23: // Right
					ClientGame.instance().input().right.setKeyEvents(readKey);
					Options.instance().inputRight = readKey;
					break;
				case 24: // Interact
					ClientGame.instance().input().interact.setKeyEvents(readKey);
					Options.instance().inputInteract = readKey;
					break;
				case 25: // Next item
					ClientGame.instance().input().prevItem.setKeyEvents(readKey);
					Options.instance().inputPrevItem = readKey;
					break;
				case 26: // Last item
					ClientGame.instance().input().nextItem.setKeyEvents(readKey);
					Options.instance().inputNextItem = readKey;
					break;
				case 27: // Drop item
					ClientGame.instance().input().dropItem.setKeyEvents(readKey);
					Options.instance().inputDropItem = readKey;
					break;
				case 28: // Use item
					ClientGame.instance().input().useItem.setKeyEvents(readKey);
					Options.instance().inputUseItem = readKey;
					break;
			}
			
			settingFor = -1;
		}
		else
		{
			settingFor = -2;
		}
		
		for (GuiButton b : buttons)
		{
			b.setEnabled(true);
		}
		
		updateControlButtons(true);
		updateButtons();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (settingFor == -2)
		{
			if (ClientGame.instance().input().escape.isPressed())
			{
				ClientGame.instance().input().escape.isReleasedFiltered();
			}
			else if (ClientGame.instance().input().escape.isReleasedFiltered())
			{
				settingFor = -1;
			}
		}
		else if (ClientGame.instance().input().escape.isReleasedFiltered())
		{
			goToParentScreen();
		}
	}
	
	private void updateButtons()
	{
		switch (currentPage)
		{
			case 0:
				lighting.setText("Lighting: " + Options.instance().lighting);
				renderOOF.setText("Draw OOF: " + Options.instance().renderOffFocus);
				
				tsync.setText("T-Sync: " + Options.instance().getTsync());
				break;
			case 1:
				up.setText("Up: " + KeyEvent.getKeyText(ClientGame.instance().input().up.getKeyEvent()));
				down.setText("Down: " + KeyEvent.getKeyText(ClientGame.instance().input().down.getKeyEvent()));
				left.setText("Left: " + KeyEvent.getKeyText(ClientGame.instance().input().left.getKeyEvent()));
				right.setText("Right: " + KeyEvent.getKeyText(ClientGame.instance().input().right.getKeyEvent()));
				
				interact.setText("Interact: " + KeyEvent.getKeyText(ClientGame.instance().input().interact.getKeyEvent()));
				nextItem.setText("Next Item: " + KeyEvent.getKeyText(ClientGame.instance().input().prevItem.getKeyEvent()));
				prevItem.setText("Prev Item: " + KeyEvent.getKeyText(ClientGame.instance().input().nextItem.getKeyEvent()));
				dropItem.setText("Drop Item: " + KeyEvent.getKeyText(ClientGame.instance().input().dropItem.getKeyEvent()));
				
				useItem.setText("Use Item: " + KeyEvent.getKeyText(ClientGame.instance().input().useItem.getKeyEvent()));
				
				break;
		}
	}
	
	private void updateControlButtons(boolean enable)
	{
		if (enable)
		{
			done.setEnabled(true);
			next.setEnabled(currentPage < MAX_PAGE);
			prev.setEnabled(currentPage > MIN_PAGE);
		}
		else
		{
			done.setEnabled(false);
			next.setEnabled(false);
			prev.setEnabled(false);
		}
	}
	
	@Override
	public void updateValue(int sliderID, int newValue)
	{
		switch (sliderID)
		{
			case 1:
				Options.instance().musicVolume = newValue;
				AudioClip.updateVolumesFromOptions();
				break;
			case 2:
				Options.instance().sfxVolume = newValue;
				AudioClip.updateVolumesFromOptions();
				break;
			case 3:
				tempScale = newValue;
				break;
		}
	}
}
