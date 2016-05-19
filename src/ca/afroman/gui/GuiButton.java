package ca.afroman.gui;

import java.awt.Rectangle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.input.InputType;

public class GuiButton extends InputType
{
	protected GuiScreen screen;
	
	protected Rectangle hitbox;
	
	private int id;
	private boolean canHold = false;
	private boolean isEnabled = true;
	private int state = 0;
	private Texture[][] textures;
	
	public GuiButton(GuiScreen screen, int id, int x, int y, int width)
	{
		this(screen, Assets.getTexture(AssetType.BUTTON_NORMAL), Assets.getTexture(AssetType.BUTTON_HOVER), Assets.getTexture(AssetType.BUTTON_PRESSED), id, x, y, width);
	}
	
	public GuiButton(GuiScreen screen, Texture normal, Texture hover, Texture pressed, int id, int x, int y, int width)
	{
		if (width < 3) width = 3;
		
		hitbox = new Rectangle(x, y, width, normal.getHeight());
		
		Texture[] temp = new Texture[3];
		temp[0] = normal;
		temp[1] = hover;
		temp[2] = pressed;
		
		textures = new Texture[3][3];
		
		// Goes through all the textures
		// Sets textures[i][0] to left side
		// Sets textures[i][1] to middle
		// Sets textures[i][2] to right side
		for (int i = 0; i < 3; i++)
		{
			for (int i2 = 0; i2 < 3; i2++)
			{
				textures[i][i2] = temp[i].getSubTexture(temp[i].assetType(), i2, 0, 1, normal.getHeight());
			}
		}
		
		this.screen = screen;
		this.id = id;
	}
	
	public void tick()
	{
		if (isEnabled)
		{
			int mouseX = ClientGame.instance().input().getMouseX();
			int mouseY = ClientGame.instance().input().getMouseY();
			
			if (hitbox.contains(mouseX, mouseY))
			{
				if (ClientGame.instance().input().mouseLeft.isPressed())
				{
					state = 2; // Down
					
					this.setPressed(true);
				}
				else
				{
					state = 1; // Hovering
					
					this.setPressed(false);
				}
			}
			else
			{
				state = 0; // Not on the button at all
				
				this.setPressed(false);
			}
			
			if (this.canHold() && this.isPressed())
			{
				onPressed();
			}
			else if (this.isPressedFiltered())
			{
				onPressed();
			}
			else if (this.isReleasedFiltered() && ClientGame.instance().input().mouseLeft.isReleased())
			{
				onRelease();
			}
		}
	}
	
	public boolean canHold()
	{
		return canHold;
	}
	
	public void setEnabled()
	{
		setEnabled(true);
	}
	
	public void setEnabled(boolean enabled)
	{
		isEnabled = enabled;
	}
	
	public boolean isEnabled()
	{
		return isEnabled;
	}
	
	public void setCanHold(boolean canHold)
	{
		this.canHold = canHold;
	}
	
	protected void onPressed()
	{
		if (isEnabled && screen != null) screen.pressAction(id);
	}
	
	protected void onRelease()
	{
		if (isEnabled && screen != null) screen.releaseAction(id);
	}
	
	/**
	 * @return the texture set to be using based on the state.
	 */
	private Texture[] getTexture()
	{
		return (isEnabled ? textures[state] : textures[2]);
	}
	
	public void render(Texture drawTo)
	{
		// Draws the left pixels
		drawTo.draw(getTexture()[0], hitbox.x, hitbox.y);
		
		// Draws the center pixels
		for (int i = 1; i < hitbox.getWidth() - 1; i++)
		{
			drawTo.draw(getTexture()[1], hitbox.x + i, hitbox.y);
		}
		
		// Draws the right pixels
		drawTo.draw(getTexture()[2], hitbox.x + hitbox.width - 1, hitbox.y);
	}
}
