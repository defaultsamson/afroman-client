package ca.afroman.assets;

import ca.afroman.interfaces.ITickable;

public class StepSpriteAnimation extends SpriteAnimation implements ITickable
{
	private int[] pauseFrames;
	private int lastFramePaused = -1;
	private boolean progress;
	
	/**
	 * An animated sprite with set frames to pause on.
	 * 
	 * @param pauseFrames an array listing the frame numbers that this should pause at
	 * @param type the AssetType that corresponds with this
	 * @param pingPong whether this animation should cycle back and forth, or return back to the first frame after passing the final frame
	 * @param ticksPerFrame the number of ticks that must pass before the next frame of this should be shown
	 * @param frames the frames
	 */
	public StepSpriteAnimation(int[] pauseFrames, AssetType type, boolean pingPong, int ticksPerFrame, Texture... frames)
	{
		super(type, pingPong, ticksPerFrame, frames);
		
		this.pauseFrames = pauseFrames;
		setFrame(0);
	}
	
	@Override
	public StepSpriteAnimation clone()
	{
		return new StepSpriteAnimation(pauseFrames, getAssetType(), pingPong, tickCounter.getInterval(), (Texture[]) getDrawableAssets());
	}
	
	@Override
	public StepSpriteAnimation cloneWithAllSubAssets()
	{
		Texture[] newTextures = new Texture[size()];
		
		for (int i = 0; i < newTextures.length; i++)
		{
			newTextures[i] = ((Texture[]) getDrawableAssets())[i].clone();
		}
		
		return new StepSpriteAnimation(pauseFrames, getAssetType(), pingPong, tickCounter.getInterval(), newTextures);
	}
	
	/**
	 * Progresses this, such that it continues until the next pause frame.
	 */
	public void progress()
	{
		progress = true;
	}
	
	@Override
	public void setFrame(int frame)
	{
		super.setFrame(frame);
		
		progress = true;
		
		// If the frame being set to is a pause frame, then treat it as though it is
		if (pauseFrames.length > 0)
		{
			for (int f : pauseFrames)
			{
				if (f == frame)
				{
					progress = false;
					break;
				}
			}
		}
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
