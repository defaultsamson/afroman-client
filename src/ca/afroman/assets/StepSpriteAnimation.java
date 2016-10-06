package ca.afroman.assets;

import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.Vector2DInt;

public class StepSpriteAnimation extends SpriteAnimation implements ITickable, IRenderable
{
	private int[] pauseFrames;
	
	private int lastFramePaused = -1;
	
	private boolean progress = true;
	
	public StepSpriteAnimation(int[] pauseFrames, AssetType type, boolean pingPong, int ticksPerFrame, Texture... frames)
	{
		super(type, pingPong, ticksPerFrame, frames);
		
		this.pauseFrames = pauseFrames;
	}
	
	@Override
	public Asset clone()
	{
		return new StepSpriteAnimation(pauseFrames, getAssetType(), pingPong, tickCounter.getInterval(), (Texture[]) getAssets());
	}
	
	@Override
	public AssetArray cloneWithAllSubAssets()
	{
		Texture[] newTextures = new Texture[frameCount()];
		
		for (int i = 0; i < newTextures.length; i++)
		{
			newTextures[i] = ((Texture[]) getAssets())[i].clone();
		}
		
		return new StepSpriteAnimation(pauseFrames, getAssetType(), pingPong, tickCounter.getInterval(), newTextures);
	}
	
	public void progress()
	{
		progress = true;
	}
	@Override
	public void render(Texture renderTo, Vector2DInt pos)
	{
		renderTo.draw(getCurrentFrame(), pos);
	}
	
	/**
	 * Progresses this to the next frame.
	 */
	@Override
	public void tick()
	{
		if (tickCounter.getInterval() != 0)
		{
			// If it's been told to progress the animation
			if (progress)
			{
				super.tick();
				
				// If it's left the frame that it was just paused on
				if (currentFrameIndex != lastFramePaused)
				{
					for (int frame : pauseFrames)
					{
						// If the current frame is a pause frame, then bloody pause
						if (frame == currentFrameIndex)
						{
							// Stop progressing and save the last frame it was paused on (this)
							lastFramePaused = currentFrameIndex;
							progress = false;
							tickCounter.setAtInterval();
							return;
						}
					}
					
					// Trick the program into thinking that it just paused on this frame
					// So that it will stop checking for it, and will check again for whatever frame it just left
					lastFramePaused = currentFrameIndex;
				}
			}
		}
	}
}
