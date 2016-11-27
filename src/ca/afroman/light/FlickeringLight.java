package ca.afroman.light;

import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;

public class FlickeringLight extends PointLight
{
	private double displayRadius;
	private boolean goingUp = false;
	private int ticksPerFrame;
	private int tickCounter = 0;
	private double radius2;
	
	public FlickeringLight(boolean isMicromanaged, Vector2DDouble pos, double radius1, double radius2, int ticksPerFrame)
	{
		// Picks the larger of the 2 radi to use for anchoring the draw location
		super(isMicromanaged, pos, (radius1 > radius2 ? radius1 : radius2));
		
		// Picks the smaller of the 2 radi
		this.radius2 = (radius1 <= radius2 ? radius1 : radius2);
		this.displayRadius = getRadius(); // Starts at the larger radius
		this.ticksPerFrame = ticksPerFrame;
	}
	
	public double getRadius2()
	{
		return radius2;
	}
	
	public int getTicksPerFrame()
	{
		return ticksPerFrame;
	}
	
	public double internalRadiusOffset()
	{
		return getRadius() - displayRadius;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void renderCentered(LightMap renderTo)
	{
		// TODO control this with positions entirely and don't get their primitive types
		Vector2DInt offsetPos;
		
		if (level != null)
		{
			offsetPos = level.worldToScreen(position);
		}
		else
		{
			offsetPos = new Vector2DInt((int) position.getX(), (int) position.getY());
		}
		
		// System.out.println("Cancer");
		
		offsetPos.add((int) -displayRadius, (int) -displayRadius);
		
		renderTo.drawLight(offsetPos, displayRadius, colour);
	}
	
	@Override
	public void setRadius(double radius)
	{
		super.setRadius(radius);
		updateRadius();
	}
	
	public void setRadius2(double radius2)
	{
		this.radius2 = radius2;
		updateRadius();
	}
	
	public void setTicksPerFrame(int newValue)
	{
		ticksPerFrame = newValue;
	}
	
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
					displayRadius++;
				}
				else
				{
					displayRadius--;
				}
				
				// If it's going over the limit, loop back to frame 1, or ping pong
				if (displayRadius > getRadius())
				{
					// Makes animation play the other way.
					goingUp = !goingUp;
					// Puts back to the next frame
					displayRadius -= 2;
				}
				
				if (displayRadius < radius2) // height is used as the secondary radius
				{
					// Makes animation play the other way.
					goingUp = !goingUp;
					// Puts back to the next frame
					displayRadius += 2;
				}
			}
		}
	}
	
	private void updateRadius()
	{
		double smaller = (getRadius() <= radius2 ? getRadius() : radius2);
		double larger = (getRadius() > radius2 ? getRadius() : radius2);
		
		radius2 = smaller;
		radius = larger;
		
		this.displayRadius = getRadius(); // Starts at the larger radius
	}
}
