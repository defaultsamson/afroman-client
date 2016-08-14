package ca.afroman.level;

public enum BuildMode
{
	TILE,
	LIGHT,
	HITBOX,
	EVENT;
	
	public static BuildMode fromOrdinal(int ordinal)
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
	 * @param current the current item to check past
	 * @return the next item on the list of this enumerator.
	 */
	public static BuildMode getLast(BuildMode current)
	{
		int newOrdinal = current.ordinal() - 1;
		
		if (newOrdinal < 0)
		{
			return fromOrdinal(values().length - 1);
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
	 * @param current the current item to check past
	 * @return the next item on the list of this enumerator.
	 */
	public static BuildMode getNext(BuildMode current)
	{
		int newOrdinal = current.ordinal() + 1;
		
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
