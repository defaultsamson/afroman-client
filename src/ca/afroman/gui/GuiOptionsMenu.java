package ca.afroman.gui;

import ca.afroman.assets.AudioClip;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.LightMapState;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;

public class GuiOptionsMenu extends GuiMenuOutline
{
	private int tempScale;
	
	private GuiSlider musicVolume;
	private GuiSlider sfxVolume;
	
	private GuiSlider scale;
	private GuiTextButton applyScale;
	
	private GuiTextButton lighting;
	private GuiTextButton renderOOF;
	
	private GuiTextButton tsync;
	
	public GuiOptionsMenu(GuiScreen parent, boolean inGame)
	{
		super(parent, !inGame, false);
		
		int vSpacing = 22;
		
		int width = 102;
		int spacing = 3;
		musicVolume = new GuiSlider(this, 1, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 0) + 6, width, 0, 100, Options.instance().musicVolume, "Music");
		sfxVolume = new GuiSlider(this, 2, (ClientGame.WIDTH / 2) + spacing, 18 + (vSpacing * 0) + 6, width, 0, 100, Options.instance().sfxVolume, "SFX");
		musicVolume.setCanRightClick(true);
		sfxVolume.setCanRightClick(true);
		addButton(musicVolume);
		addButton(sfxVolume);
		
		tempScale = Options.instance().scale;
		scale = new GuiSlider(this, 3, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 1) + 6, width, 1, 8, tempScale, "Scale");
		applyScale = new GuiTextButton(this, 4, (ClientGame.WIDTH / 2) + spacing, 18 + (vSpacing * 1) + 6, width, blackFont, "Apply Scale");
		scale.setCanRightClick(true);
		applyScale.setCanRightClick(true);
		addButton(scale);
		addButton(applyScale);
		
		lighting = new GuiTextButton(this, 5, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 2) + 6, width, blackFont, "Lighting: ");
		renderOOF = new GuiTextButton(this, 6, (ClientGame.WIDTH / 2) + spacing, 18 + (vSpacing * 2) + 6, width, blackFont, "Draw OOF: ");
		lighting.setCanRightClick(true);
		renderOOF.setCanRightClick(true);
		addButton(lighting);
		addButton(renderOOF);
		
		tsync = new GuiTextButton(this, 7, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 3) + 6, width, blackFont, "T-Sync: ");
		tsync.setCanRightClick(true);
		addButton(tsync);
		
		addButton(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 18 + (vSpacing * 4) + 6, 72, blackFont, "Done"));
		
		updateButtons();
		
		if (!Options.instance().hasShownOptionsTip)
		{
			Options.instance().hasShownOptionsTip = true;
			new GuiClickNotification(this, -1, "You can right click options", "for more info on what they do");
		}
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15 - 6), "Settings");
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
	
	@Override
	public void tick()
	{
		super.tick();
		
		ClientGame.instance().input().escape.isReleasedFiltered();
	}
	
	private void updateButtons()
	{
		lighting.setText("Lighting: " + Options.instance().lighting);
		renderOOF.setText("Draw OOF: " + Options.instance().renderOffFocus);
		
		tsync.setText("T-Sync: " + Options.instance().getTsync());
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
