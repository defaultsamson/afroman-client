package ca.afroman.gfx;

import java.awt.Color;

public class FlickeringLight extends PointLight
{
	private int radius2;
	private int displayRadius;
	private boolean goingUp = false;
	private int ticksPerFrame;
	private int tickCounter = 0;
	
	public FlickeringLight(int x, int y, int radius1, int radius2, int ticksPerFrame)
	{
		this(x, y, radius1, radius2, ticksPerFrame, 1.0F);
	}
	
	public FlickeringLight(int x, int y, int radius1, int radius2, int ticksPerFrame, float intensity)
	{
		this( x, y, radius1, radius2, ticksPerFrame, intensity, ColourUtil.TRANSPARENT);
	}
	
	public FlickeringLight(int x, int y, int radius1, int radius2, int ticksPerFrame, float intensity, Color colour)
	{
		// Picks the larger of the 2 radi to use for anchoring the draw location
		super(x, y, (radius1 > radius2 ? radius1 : radius2), intensity, colour);
		
		// Picks the smalled of the 2 radi
		this.radius2 = (radius1 <= radius2 ? radius1 : radius2);
		this.displayRadius = radius; // Starts at the larger radius
		this.ticksPerFrame = ticksPerFrame;
	}
	
	@Override
	public void renderCentered(LightMap renderTo)
	{
		renderTo.drawLight(x - level.getCameraXOffset() - displayRadius, y - level.getCameraYOffset() - displayRadius, displayRadius, colour);
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
				if (displayRadius > radius)
				{
					// Makes animation play the other way.
					goingUp = !goingUp;
					// Puts back to the next frame
					displayRadius -= 2;
				}
				
				if (displayRadius < radius2)
				{
					// Makes animation play the other way.
					goingUp = !goingUp;
					// Puts back to the next frame
					displayRadius += 2;
				}
			}
		}
	}
	
	public int internalRadiusOffset()
	{
		return radius - displayRadius;
	}
}
