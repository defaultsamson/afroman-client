package ca.afroman.gui;

import ca.afroman.assets.AudioClip;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;

public class GuiOptionsMenu extends GuiMenuOutline
{
	private int tempScale;
	
	public GuiOptionsMenu(GuiScreen parent, boolean inGame)
	{
		super(parent, !inGame, false);
		
		addButton(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 2) + 6, 72, blackFont, "Done"));
		
		int width = 102;
		int spacing = 3;
		addButton(new GuiSlider(this, 1, (ClientGame.WIDTH / 2) - width - spacing, 18 + (24 * 0) + 6, width, 0, 100, Options.instance().musicVolume, "Music"));
		addButton(new GuiSlider(this, 2, (ClientGame.WIDTH / 2) + spacing, 18 + (24 * 0) + 6, width, 0, 100, Options.instance().sfxVolume, "SFX"));
		
		tempScale = Options.instance().scale;
		addButton(new GuiSlider(this, 3, (ClientGame.WIDTH / 2) - width - spacing, 18 + (24 * 1) + 6, width, 1, 8, tempScale, "Scale"));
		addButton(new GuiTextButton(this, 4, (ClientGame.WIDTH / 2) + spacing, 18 + (24 * 1) + 6, width, blackFont, "Apply Scale"));
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
			case 0:
				Options.instance().save();
				goToParentScreen();
				break;
			case 4:
				Options.instance().scale = tempScale;
				ClientGame.instance().resizeGame(ClientGame.WIDTH * Options.instance().scale, ClientGame.HEIGHT * Options.instance().scale, true);
				break;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		ClientGame.instance().input().escape.isReleasedFiltered();
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
