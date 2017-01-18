package ca.afroman.battle;

import ca.afroman.resource.Vector2DInt;
import ca.afroman.server.ConsoleCommand;

public enum BattlePosition
{
	LEFT_BOTTOM(new Vector2DInt(51, 113)),
	LEFT_MIDDLE(new Vector2DInt(61, 98)),
	LEFT_TOP(new Vector2DInt(71, 83)),
	
	RIGHT_TOP(new Vector2DInt(178, 88)),
	RIGHT_BOTTOM(new Vector2DInt(188, 108));
	
	private Vector2DInt referencePos;
	
	BattlePosition(Vector2DInt referencePos)
	{
		this.referencePos = referencePos;
	}
	
	public int getReferenceX()
	{
		return referencePos.getX();
	}
	
	public int getReferenceY()
	{
		return referencePos.getY();
	}
	
	public Vector2DInt getReference()
	{
		return referencePos;
	}
	
	public static BattlePosition fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
