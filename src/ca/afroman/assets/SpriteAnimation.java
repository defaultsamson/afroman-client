package ca.afroman.assets;

import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.ModulusCounter;
import ca.afroman.resource.Vector2DInt;

public class SpriteAnimation extends DrawableAssetArray implements ITickable, ITextureDrawable
{
	protected int currentFrameIndex = 0;
	protected boolean pingPong;
	private boolean goingUp = true;
	protected ModulusCounter tickCounter;
	
	/**
	 * An animated sprite.
	 * 
	 * @param type the AssetType that corresponds with this
	 * @param pingPong whether this animation should cycle back and forth, or return back to the first frame after passing the final frame
	 * @param ticksPerFrame the number of ticks that must pass before the next frame of this should be shown
	 * @param frames the frames
	 */
	public SpriteAnimation(AssetType type, boolean pingPong, int ticksPerFrame, Texture... frames)
	{
		super(type, frames);
		
		this.pingPong = pingPong;
		tickCounter = new ModulusCounter(ticksPerFrame);
	}
	
	@Override
	public SpriteAnimation clone()
	{
		return new SpriteAnimation(getAssetType(), pingPong, tickCounter.getInterval(), (Texture[]) getDrawableAssets());
	}
	
	@Override
	public SpriteAnimation cloneWithAllSubAssets()
	{
		Texture[] newTextures = new Texture[size()];
		
		for (int i = 0; i < newTextures.length; i++)
		{
			newTextures[i] = ((Texture[]) getDrawableAssets())[i].clone();
		}
		
		return new SpriteAnimation(getAssetType(), pingPong, tickCounter.getInterval(), newTextures);
	}
	
	/**
	 * Flips this horizontally along the x axis.
	 */
	public SpriteAnimation flipX()
	{
		for (Asset as : getDrawableAssets())
		{
			if (as instanceof Texture)
			{
				Texture frame = (Texture) as;
				frame.flipX();
			}
		}
		
		return this;
	}
	
	/**
	 * Flips this vertically along the y avid.
	 */
	public SpriteAnimation flipY()
	{
		for (Asset as : getDrawableAssets())
		{
			if (as instanceof Texture)
			{
				Texture frame = (Texture) as;
				frame.flipY();
			}
		}
		
		return this;
	}
	
	@Override
	public Texture getDisplayedTexture()
	{
		return ((Texture[]) getDrawableAssets())[currentFrameIndex];
	}
	
	@Override
	public int getHeight()
	{
		return getDisplayedTexture().getHeight();
	}
	
	public ModulusCounter getTickCounter()
	{
		return tickCounter;
	}
	
	@Override
	public int getWidth()
	{
		return getDisplayedTexture().getWidth();
	}
	
	@Override
	public void render(Texture renderTo, Vector2DInt pos)
	{
		renderTo.draw(getDisplayedTexture(), pos);
	}
	
	/**
	 * Sets this to be on a certain frame.
	 * 
	 * @param frame the new frame
	 */
	public void setFrame(int frame)
	{
		currentFrameIndex = frame;
		tickCounter.reset();
	}
	
	@Override
	public void tick()
	{
		if (tickCounter.getInterval() != 0)
		{
			// If it's supposed to progress based on tpf
			if (tickCounter.isAtInterval())
			{
				if (goingUp)
				{
					currentFrameIndex++;
				}
				else
				{
					currentFrameIndex--;
				}
				
				// If it's going over the limit, loop back to frame 1, or ping pong
				if (currentFrameIndex > size() - 1)
				{
					if (pingPong)
					{
						// Makes animation play the other way.
						goingUp = !goingUp;
						// Puts back to the next frame
						currentFrameIndex -= 2;
					}
					else
					{
						currentFrameIndex = 0;
					}
				}
				
				if (currentFrameIndex < 0)
				{
					if (pingPong)
					{
						// Makes animation play the other way.
						goingUp = !goingUp;
						// Puts back to the next frame
						currentFrameIndex += 2;
					}
				}
			}
		}
	}
}
