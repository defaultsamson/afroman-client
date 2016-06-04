package ca.afroman.assets;

import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;

public class SpriteAnimation extends AssetArray implements ITickable, IRenderable
{
	/** Holds the textures for each frame. */
	private int currentFrameIndex = 0;
	private boolean pingPong;
	private boolean goingUp = true;
	private int ticksPerFrame;
	private int tickCounter = 0;
	
	public SpriteAnimation(AssetType type, int ticksPerFrame, Texture... frames)
	{
		this(type, false, ticksPerFrame, frames);
	}
	
	public SpriteAnimation(AssetType type, boolean pingPong, int ticksPerFrame, Texture... frames)
	{
		super(type, frames);
		
		this.pingPong = pingPong;
		this.ticksPerFrame = ticksPerFrame;
	}
	
	/**
	 * Progresses this to the next frame.
	 */
	@Override
	public void tick()
	{
		if (ticksPerFrame != 0)
		{
			tickCounter++;
			
			// If it's supposed to progress based on tpf
			if (tickCounter >= ticksPerFrame)
			{
				tickCounter = 0;
				
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
	
	public Texture getCurrentFrame()
	{
		return ((Texture[]) getAssets())[currentFrameIndex];
	}
	
	public int frameCount()
	{
		return ((Texture[]) getAssets()).length;
	}
	
	public void setFrame(int frame)
	{
		currentFrameIndex = frame;
	}
	
	@Override
	public void render(Texture renderTo, int x, int y)
	{
		renderTo.draw(getCurrentFrame(), x, y);
	}
	
	@Override
	public Asset clone()
	{
		return new SpriteAnimation(getAssetType(), pingPong, ticksPerFrame, (Texture[]) getAssets());
	}
	
	@Override
	public AssetArray cloneWithAllSubAssets()
	{
		Texture[] newTextures = new Texture[frameCount()];
		
		for (int i = 0; i < newTextures.length; i++)
		{
			newTextures[i] = ((Texture[]) getAssets())[i].clone();
		}
		
		return new SpriteAnimation(getAssetType(), pingPong, ticksPerFrame, newTextures);
	}
	
	public double getWidth()
	{
		return getCurrentFrame().getWidth();
	}
	
	public double getHeight()
	{
		return getCurrentFrame().getHeight();
	}
}
