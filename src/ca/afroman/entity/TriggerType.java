package ca.afroman.entity;

public enum TriggerType
{
	PLAYER_INTERACT,
	PLAYER_COLLIDE,
	ENTITY_INTERACT,
	ENTITY_COLLIDE;
	
	public static TriggerType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
