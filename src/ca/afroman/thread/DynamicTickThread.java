package ca.afroman.thread;

import ca.afroman.interfaces.ITickable;

public abstract class DynamicTickThread extends DynamicThread implements ITickable
{
	protected long lastTime;
	protected double ticksPerSecond;
	protected double nsPerTick;
	
	protected int ticks;
	protected int tickCount;
	protected int tps;
	
	protected long lastTimer;
	protected double delta;
	
	public DynamicTickThread(ThreadGroup group, String name, double ticksPerSecond)
	{
		super(group, name);
		
		if (ticksPerSecond < 1) ticksPerSecond = 1;
		
		this.ticksPerSecond = ticksPerSecond;
	}
	
	@Override
	public void onStart()
	{
		lastTime = System.nanoTime();
		nsPerTick = 1000000000 / ticksPerSecond;
		
		ticks = 0;
		tickCount = 0;
		tps = 0;
		
		lastTimer = System.currentTimeMillis();
		delta = 0;
	}
	
	/**
	 * Runs every time that the thread loops.
	 */
	@Override
	public void onRun() // TODO synchronized? seems like it could be unnecessary
	{
		long now = System.nanoTime();
		delta += (now - lastTime) / nsPerTick;
		lastTime = now;
		
		while (delta >= 1)
		{
			ticks++;
			tickCount++;
			tick();
			delta--;
		}
		
		// If current time - the last time we updated is >= 1 second
		if (System.currentTimeMillis() - lastTimer >= 1000)
		{
			tps = ticks;
			lastTimer += 1000;
			ticks = 0;
		}
	}
	
	@Override
	public abstract void tick();
	
	public int getTicksPerSecond()
	{
		return tps;
	}
	
	public int getTickCount()
	{
		return tickCount;
	}
}
