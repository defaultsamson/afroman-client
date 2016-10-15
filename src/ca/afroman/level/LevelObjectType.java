package ca.afroman.level;

public enum LevelObjectType
{
	LEVEL,
	TILE,
	ENTITY,
	HITBOX,
	POINT_LIGHT,
	FLICKERING_LIGHT,
	HITBOX_TRIGGER,
	HITBOX_TOGGLE,
	TP_TOGGLE;
	
	public static LevelObjectType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
