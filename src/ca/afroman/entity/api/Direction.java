package ca.afroman.entity.api;

public enum Direction
{
	NONE(0, 0),
	
	UP(0, -1),
	DOWN(0, 1),
	LEFT(-1, 0),
	RIGHT(1, 0),
	
	UP_LEFT(-1, -1),
	UP_RIGHT(1, -1),
	
	DOWN_LEFT(-1, 1),
	DOWN_RIGHT(1, 1);
	
	public static Direction fromAmplitudes(byte xa, byte ya)
	{
		for (Direction d : values())
		{
			if (d.xa == xa && d.ya == ya) return d;
		}
		
		return null;
	}
	public static Direction fromAmplitudes(double xa, double ya)
	{
		// Normalizes all amplitudes to find direction
		
		byte dXa = 0;
		if (xa < 0)
		{
			dXa = -1;
		}
		else if (xa > 0)
		{
			dXa = 1;
		}
		
		byte dYa = 0;
		if (ya < 0)
		{
			dYa = -1;
		}
		else if (ya > 0)
		{
			dYa = 1;
		}
		
		return fromAmplitudes(dXa, dYa);
	}
	
	public static Direction fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	private int xa;
	
	private int ya;
	
	Direction(int xa, int ya)
	{
		this.xa = xa;
		this.ya = ya;
	}
	
	public int getXAmplitude()
	{
		return xa;
	}
	
	public int getYAmplitude()
	{
		return ya;
	}
}
