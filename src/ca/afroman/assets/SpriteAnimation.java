package ca.afroman.assets;

import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.ModulusCounter;
import ca.afroman.resource.Vector2DInt;

public class SpriteAnimation extends AssetArray implements ITickable, IRenderable
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
	public Asset clone()
	{
		return new SpriteAnimation(getAssetType(), pingPong, tickCounter.getInterval(), (Texture[]) getAssets());
	}
	
	@Override
	public AssetArray cloneWithAllSubAssets()
	{
		Texture[] newTextures = new Texture[frameCount()];
		
		for (int i = 0; i < newTextures.length; i++)
		{
			newTextures[i] = ((Texture[]) getAssets())[i].clone();
		}
		
		return new SpriteAnimation(getAssetType(), pingPong, tickCounter.getInterval(), newTextures);
	}
	
	public int frameCount()
	{
		return ((Texture[]) getAssets()).length;
	}
	
	public Texture getCurrentFrame()
	{
		return ((Texture[]) getAssets())[currentFrameIndex];
	}
	
	public double getHeight()
	{
		return getCurrentFrame().getHeight();
	}
	
	public double getWidth()
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
