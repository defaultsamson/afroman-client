package ca.afroman.gui;

import ca.afroman.assets.AudioClip;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;

public class GuiOptionsMenu extends GuiMenuOutline
{
	private int tempScale;
	
	private GuiTextButton lighting;
	private GuiTextButton renderOOF;
	private GuiTextButton tsync;
	
	public GuiOptionsMenu(GuiScreen parent, boolean inGame)
	{
		super(parent, !inGame, false);
		
		int vSpacing = 22;
		
		int width = 102;
		int spacing = 3;
		addButton(new GuiSlider(this, 1, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 0) + 6, width, 0, 100, Options.instance().musicVolume, "Music"));
		addButton(new GuiSlider(this, 2, (ClientGame.WIDTH / 2) + spacing, 18 + (vSpacing * 0) + 6, width, 0, 100, Options.instance().sfxVolume, "SFX"));
		
		tempScale = Options.instance().scale;
		addButton(new GuiSlider(this, 3, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 1) + 6, width, 1, 8, tempScale, "Scale"));
		addButton(new GuiTextButton(this, 4, (ClientGame.WIDTH / 2) + spacing, 18 + (vSpacing * 1) + 6, width, blackFont, "Apply Scale"));
		
		lighting = new GuiTextButton(this, 5, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 2) + 6, width, blackFont, "Lighting: ");
		renderOOF = new GuiTextButton(this, 6, (ClientGame.WIDTH / 2) + spacing, 18 + (vSpacing * 2) + 6, width, blackFont, "Draw OOF: ");
		
		addButton(lighting);
		addButton(renderOOF);
		
		tsync = new GuiTextButton(this, 7, (ClientGame.WIDTH / 2) - width - spacing, 18 + (vSpacing * 3) + 6, width, blackFont, "T-Sync: ");
		
		addButton(tsync);
		
		addButton(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 18 + (vSpacing * 4) + 6, 72, blackFont, "Done"));
		
		updateButtons();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15 - 6), "Settings");
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 0: // Done
				Options.instance().save();
				goToParentScreen();
				break;
			case 4: // Scale apply
				Options.instance().scale = tempScale;
				ClientGame.instance().resizeGame(ClientGame.WIDTH * Options.instance().scale, ClientGame.HEIGHT * Options.instance().scale, true);
				break;
			case 5: // Lighting
				Options.instance().lighting = Options.instance().lighting.getNext();
				updateButtons();
				break;
			case 6: // Render out of focus
				Options.instance().renderOffFocus = !Options.instance().renderOffFocus;
				updateButtons();
				break;
			case 7: // Tick sync
				Options.instance().setTsync(!Options.instance().getTsync());
				updateButtons();
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
