package ca.afroman.thread;

import ca.afroman.entity.api.IServerClient;

public abstract class DynamicTickRenderThread extends DynamicTickThread implements IServerClient
{
	protected int frames;
	protected int fps;
	private boolean isServerSide;
	
	public DynamicTickRenderThread(ThreadGroup group, String name, boolean isServerSide, double ticksPerSecond)
	{
		super(group, name, ticksPerSecond);
		
		this.isServerSide = isServerSide;
	}
	
	public int getFramesPerSecond()
	{
		return fps;
	}
	
	@Override
	public boolean isServerSide()
	{
		return isServerSide;
	}
	
	/**
	 * Runs every time that the thread loops.
	 */
	// DO NOT INVOKE super.onRun()
	@Override
	public void onRun()
	{
		long now = System.nanoTime();
		delta += (now - lastTime) / nsPerTick;
		lastTime = now;
		boolean shouldRender = true; // true for unlimited frames, false for limited to tick rate
		
		while (delta >= 1)
		{
			ticks++;
			tickCount++;
			tick();
			delta--;
			shouldRender = true;
		}
		
		// Only render when something has been updated
		if (!isServerSide && shouldRender)
		{
			frames++;
			render();
		}
		
		// If current time - the last time we updated is >= 1 second
		if (System.currentTimeMillis() - lastTimer >= 1000)
		{
			tps = ticks;
			fps = frames;
			lastTimer += 1000;
			frames = 0;
			ticks = 0;
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		frames = 0;
		fps = 0;
	}
	
	public abstract void render();
}
