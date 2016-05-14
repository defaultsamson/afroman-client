package ca.afroman.thread;

public abstract class DynamicTickRenderThread extends DynamicTickThread
{
	protected int frames;
	protected int fps;
	
	public DynamicTickRenderThread(int ticksPerSecond)
	{
		super(ticksPerSecond);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		frames = 0;
		fps = 0;
	}
	
	/**
	 * Runs every time that the thread loops.
	 */
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
		if (shouldRender)
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
	
	public abstract void render();
	
	public int getFramesPerSecond()
	{
		return fps;
	}
}
