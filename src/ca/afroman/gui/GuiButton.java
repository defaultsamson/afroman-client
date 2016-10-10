package ca.afroman.gui;

import java.awt.Rectangle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.AudioClip;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.input.InputType;
import ca.afroman.resource.Vector2DInt;

public class GuiButton extends InputType
{
	protected GuiScreen screen;
	
	protected Rectangle hitbox;
	
	private boolean makeSound = true;
	private AudioClip pushSound;
	private AudioClip releaseSound;
	private InputType onHover;
	
	private int id;
	private boolean canHold = false;
	private boolean isEnabled = true;
	private ButtonState state = ButtonState.NONE;
	private Texture[][] textures;
	
	public GuiButton(GuiScreen screen, int id, int x, int y, int width)
	{
		this(screen, Assets.getTexture(AssetType.BUTTON_NORMAL), Assets.getTexture(AssetType.BUTTON_HOVER), Assets.getTexture(AssetType.BUTTON_PRESSED), id, x, y, width);
	}
	
	public GuiButton(GuiScreen screen, Texture normal, Texture hover, Texture pressed, int id, int x, int y, int width)
	{
		if (width < 3) width = 3;
		
		hitbox = new Rectangle(x, y, width, (int) normal.getHeight());
		
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
				textures[i][i2] = temp[i].getSubTexture(temp[i].getAssetType(), i2, 0, 1, (int) normal.getHeight());
			}
		}
		
		this.screen = screen;
		this.id = id;
		this.pushSound = Assets.getAudioClip(AssetType.AUDIO_BUTTON_PUSH);
		this.releaseSound = Assets.getAudioClip(AssetType.AUDIO_BUTTON_RELEASE);
		this.onHover = new InputType();
	}
	
	public boolean canHold()
	{
		return canHold;
	}
	
	public int getID()
	{
		return id;
	}
	
	/**
	 * @return the texture set to be using based on the state.
	 */
	protected Texture[] getTexture()
	{
		return (isEnabled ? textures[state.ordinal()] : textures[2]);
	}
	
	public boolean isEnabled()
	{
		return isEnabled;
	}
	
	public boolean isHovering()
	{
		return state == ButtonState.HOVERING;
	}
	
	public boolean isIdle()
	{
		return state == ButtonState.NONE;
	}
	
	@Override
	public boolean isPressed()
	{
		return state == ButtonState.PRESSED;
	}
	
	public void onHover()
	{
		
	}
	
	protected void onPress()
	{
		if (isEnabled && screen != null) screen.pressAction(id);
	}
	
	protected void onRelease()
	{
		if (isEnabled && screen != null) screen.releaseAction(id);
	}
	
	public void render(Texture drawTo)
	{
		// Draws the left pixels
		drawTo.draw(getTexture()[0], new Vector2DInt(hitbox.x, hitbox.y));
		
		// Draws the center pixels
		for (int i = 1; i < hitbox.getWidth() - 1; i++)
		{
			drawTo.draw(getTexture()[1], new Vector2DInt(hitbox.x + i, hitbox.y));
		}
		
		// Draws the right pixels
		drawTo.draw(getTexture()[2], new Vector2DInt(hitbox.x + hitbox.width - 1, hitbox.y));
	}
	
	public void setCanHold(boolean canHold)
	{
		this.canHold = canHold;
	}
	
	public void setEnabled()
	{
		setEnabled(true);
	}
	
	public void setEnabled(boolean enabled)
	{
		isEnabled = enabled;
	}
	
	public void setMakeSound(boolean isNoisy)
	{
		makeSound = isNoisy;
	}
	
	public void tick()
	{
		if (isEnabled)
		{
			Vector2DInt mouse = ClientGame.instance().input().getMousePos();
			
			// Only listen for action on this button if the screen isn't already listening to another button overtop this one
			if (hitbox.contains(mouse.getX(), mouse.getY()) && screen.getListeningButton() == null)
			{
				screen.setListeningButton(this);
				
				if (ClientGame.instance().input().mouseLeft.isPressed())
				{
					state = ButtonState.PRESSED; // Down
					
					this.setPressed(true);
				}
				else
				{
					state = ButtonState.HOVERING; // Hovering
					onHover.setPressed(true);
					
					this.setPressed(false);
				}
			}
			else
			{
				state = ButtonState.NONE; // Not on the button at all
				onHover.setPressed(false);
				
				this.setPressed(false);
			}
			
			if (onHover.isPressedFiltered())
			{
				onHover();
			}
			if (this.canHold() && this.isPressed())
			{
				onPress();
			}
			else if (this.isPressedFiltered())
			{
				onPress();
				if (makeSound) pushSound.start();
			}
			else if (this.isReleasedFiltered())
			{
				if (makeSound) releaseSound.start();
				
				// Only invoke the onRelease() method if the mouse was released to cause this being released, and it wasn't just dragged off of this button
				if (ClientGame.instance().input().mouseLeft.isReleased())
				{
					onRelease();
				}
			}
		}
	}
}
