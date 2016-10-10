package ca.afroman.gui;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.LightMap;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;

public class GuiOptionsMenu extends GuiScreen
{
	private boolean inGame;
	private LightMap lightmap;
	
	public GuiOptionsMenu(GuiScreen parent, boolean inGame)
	{
		super(parent);
		
		this.inGame = inGame;
		lightmap = !inGame ? new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT) : null;
		
		addButton(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 2) + 6, 72, blackFont, "Done"));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		if (Options.instance().isLightingOn() && !inGame)
		{
			lightmap.clear();
			lightmap.patch();
			
			renderTo.draw(lightmap, LightMap.PATCH_POSITION);
		}
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15 - 6), "Settings");
	}
	
	@Override
	public void keyTyped()
	{
		
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 0:
				goToParentScreen();
				break;
		}
	}
}
