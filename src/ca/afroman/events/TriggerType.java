package ca.afroman.events;

public enum TriggerType
{
	PLAYER_INTERACT,
	PLAYER_COLLIDE,
	PLAYER_UNCOLLIDE,
	ENTITY_INTERACT,
	ENTITY_COLLIDE,
	ENTITY_UNCOLLIDE;
	
	public static TriggerType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
