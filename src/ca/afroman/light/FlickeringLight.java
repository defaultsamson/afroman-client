package ca.afroman.light;

import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DDouble;

public class FlickeringLight extends PointLight
{
	private int[] radi;
	private int frame;
	private boolean goingUp = false;
	private int ticksPerFrame;
	private int tickCounter = 0;
	private double radius2;
	
	public FlickeringLight(boolean isMicromanaged, Vector2DDouble pos, double radius1, double radius2, int ticksPerFrame)
	{
		super(isMicromanaged, pos, radius1);
		
		// Picks the smaller of the 2 radi
		this.frame = 0;
		this.ticksPerFrame = ticksPerFrame;
		
		initRadius(radius1, radius2);
	}
	
	@Override
	protected int getDisplayRadius()
	{
		return radi[frame];
	}
	
	public double getRadius2()
	{
		return radius2;
	}
	
	public int getTicksPerFrame()
	{
		return ticksPerFrame;
	}
	
	private void initRadius(double radius1, double radius2)
	{
		// Picks the larger of the 2 radi to use for anchoring the draw location
		double smaller = (radius1 <= radius2 ? radius1 : radius2);
		double larger = (radius1 > radius2 ? radius1 : radius2);
		
		this.radius2 = smaller;
		radius = larger;
		
		int radiusDifference = (int) (larger - smaller);
		
		if (radiusDifference == 0)
		{
			System.err.println("Flickering lights should never have 2 of the same radi passed to them");
			radi = new int[] { (int) larger };
		}
		else
		{
			radi = new int[radiusDifference];
			
			for (int i = 0; i < radi.length; i++)
			{
				// Creates the radius for the frame based on a cosine function
				// Uses the first PI/2 of the function to map out all the values, then dsimply ping-pongs back and forth
				radi[i] = (int) (smaller + (radiusDifference * ((Math.cos((i * Math.PI) / radi.length) / 2D) + 0.5)));
			}
		}
		
		frame = 0;
	}
	
	@Override
	public void setRadius(double radius)
	{
		super.setRadius(radius);
		initRadius(getRadius(), getRadius2());
	}
	
	public void setRadius2(double radius2)
	{
		this.radius2 = radius2;
		initRadius(getRadius(), getRadius2());
	}
	
	public void setTicksPerFrame(int newValue)
	{
		ticksPerFrame = newValue;
	}
	
	@Override
	public void tick()
	{
		if (Options.instance().isLightingOn())
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
						frame++;
					}
					else
					{
						frame--;
					}
					
					// If it's going over the limit, loop back to frame 1, or ping pong
					if (frame > radi.length - 1)
					{
						// Makes animation play the other way.
						goingUp = !goingUp;
						// Puts back to the next frame
						frame -= 2;
					}
					
					if (frame < 0)
					{
						// Makes animation play the other way.
						goingUp = !goingUp;
						// Puts back to the next frame
						frame += 2;
					}
				}
			}
		}
	}
}
