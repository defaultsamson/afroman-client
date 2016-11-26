package ca.afroman.assets;

import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.ModulusCounter;
import ca.afroman.resource.Vector2DInt;

public class SpriteAnimation extends DrawableAssetArray implements ITickable, IRenderable
{
	/** Holds the textures for each frame. */
	protected int currentFrameIndex = 0;
	protected boolean pingPong;
	private boolean goingUp = true;
	protected ModulusCounter tickCounter;
	
	public SpriteAnimation(AssetType type, boolean pingPong, int ticksPerFrame, Texture... frames)
	{
		super(type, frames);
		
		this.pingPong = pingPong;
		tickCounter = new ModulusCounter(ticksPerFrame);
	}
	
	@Override
	public SpriteAnimation clone()
	{
		return new SpriteAnimation(getAssetType(), pingPong, tickCounter.getInterval(), (Texture[]) getAssets());
	}
	
	@Override
	public SpriteAnimation cloneWithAllSubAssets()
	{
		Texture[] newTextures = new Texture[frameCount()];
		
		for (int i = 0; i < newTextures.length; i++)
		{
			newTextures[i] = ((Texture[]) getAssets())[i].clone();
		}
		
		return new SpriteAnimation(getAssetType(), pingPong, tickCounter.getInterval(), newTextures);
	}
	
	/**
	 * Flips this horizontally.
	 */
	public SpriteAnimation flipX()
	{
		for (Asset as : getAssets())
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
	 * Flips this horizontally.
	 */
	public SpriteAnimation flipY()
	{
		for (Asset as : getAssets())
		{
			if (as instanceof Texture)
			{
				Texture frame = (Texture) as;
				frame.flipY();
			}
		}
		
		return this;
	}
	
	public int frameCount()
	{
		return ((Texture[]) getAssets()).length;
	}
	
	public Texture getCurrentFrame()
	{
		return ((Texture[]) getAssets())[currentFrameIndex];
	}
	
	@Override
	public int getHeight()
	{
		return getCurrentFrame().getHeight();
	}
	
	@Override
	public int getWidth()
	{
		return getCurrentFrame().getWidth();
	}
	
	@Override
	public void render(Texture renderTo, Vector2DInt pos)
	{
		renderTo.draw(getCurrentFrame(), pos);
	}
	
	public void setFrame(int frame)
	{
		currentFrameIndex = frame;
		tickCounter.reset();
	}
	
	/**
	 * Progresses this to the next frame.
	 */
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
				if (currentFrameIndex > frameCount() - 1)
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
