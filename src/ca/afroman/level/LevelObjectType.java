package ca.afroman.level;

public enum LevelObjectType
{
	LEVEL,
	TILE,
	ENTITY,
	HITBOX,
	POINT_LIGHT,
	FLICKERING_LIGHT;
	
	public static LevelObjectType fromOrdinal(int ordinal)
	{
		return LevelObjectType.values()[ordinal];
	}
}
