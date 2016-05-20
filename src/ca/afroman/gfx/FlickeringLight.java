package ca.afroman.gfx;

import java.awt.Color;

import ca.afroman.level.ClientLevel;

public class FlickeringLight extends PointLight
{
	private double displayRadius;
	private boolean goingUp = false;
	private int ticksPerFrame;
	private int tickCounter = 0;
	
	public FlickeringLight(int id, ClientLevel level, double x, double y, double radius1, double radius2, int ticksPerFrame)
	{
		this(id, level, x, y, radius1, radius2, ticksPerFrame, ColourUtil.TRANSPARENT);
	}
	
	public FlickeringLight(int id, ClientLevel level, double x, double y, double radius1, double radius2, int ticksPerFrame, Color colour)
	{
		// Picks the larger of the 2 radi to use for anchoring the draw location
		super(id, level, x, y, (radius1 > radius2 ? radius1 : radius2), colour);
		
		// Picks the smaller of the 2 radi
		this.height = (radius1 <= radius2 ? radius1 : radius2);
		this.displayRadius = getRadius(); // Starts at the larger radius
		this.ticksPerFrame = ticksPerFrame;
	}
	
	@Override
	public void renderCentered(LightMap renderTo)
	{
		double xOffset = x;
		double yOffset = y;
		
		if (level != null && level instanceof ClientLevel)
		{
			ClientLevel cLevel = (ClientLevel) this.level;
			
			xOffset = cLevel.worldToScreenX(x);
			yOffset = cLevel.worldToScreenY(y);
		}
		
		renderTo.drawLight(xOffset - displayRadius, yOffset - displayRadius, displayRadius, colour);
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
				
				if (displayRadius < height) // height is used as the secondary radius
				{
					// Makes animation play the other way.
					goingUp = !goingUp;
					// Puts back to the next frame
					displayRadius += 2;
				}
			}
		}
	}
	
	public double internalRadiusOffset()
	{
		return getRadius() - displayRadius;
	}
}
