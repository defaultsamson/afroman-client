package ca.afroman.packet;

import ca.afroman.level.api.BuildMode;

public enum PacketType
{
	INVALID,
	
	REQUEST_CONNECTION,
	COMMAND,
	DENY_JOIN,
	ASSIGN_CLIENTID,
	UPDATE_PLAYERLIST,
	SETROLE,
	PLAYER_DISCONNECT,
	STOP_SERVER,
	START_SERVER,
	
	SEND_LEVELS,
	
	ADD_LEVEL_PLAYER,
	
	ACTIVATE_TRIGGER,
	
	REQUEST_PLAYER_MOVE,
	SET_ENTITY_LOCATION,
	SET_PLAYER_LOCATION,
	PLAYER_INTERACT;
	
	public static PacketType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	/**
	 * Gets the enum value of this prior to the <b>current</b> value.
	 * <p>
	 * If no value is found before the <b>current</b> value, the value at
	 * index <i>n - 1</i> will be returned, where <i>n</i> is the total
	 * number of values for this enumerator.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public PacketType getLast()
	{
		int newOrdinal = ordinal() - 1;
		
		if (newOrdinal < 0)
		{
			return fromOrdinal(BuildMode.values().length - 1);
		}
		else
		{
			return fromOrdinal(newOrdinal);
		}
	}
	
	/**
	 * Gets the enum value of this past the <b>current</b> value.
	 * <p>
	 * If no value is found past the <b>current</b> value, the value at
	 * index 0 will be returned.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public PacketType getNext()
	{
		int newOrdinal = ordinal() + 1;
		
		if (newOrdinal > values().length - 1)
		{
			return fromOrdinal(0);
		}
		else
		{
			return fromOrdinal(newOrdinal);
		}
	}
}
