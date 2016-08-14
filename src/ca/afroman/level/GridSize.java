package ca.afroman.level;

public enum GridSize
{
	NONE(0),
	SMALL(8),
	MEDIUM(16),
	LARGE(32);
	
	public static GridSize fromOrdinal(int ordinal)
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
	public static GridSize getLast(GridSize current)
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
	public static GridSize getNext(GridSize current)
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
	
	private int size;
	
	GridSize(int size)
	{
		this.size = size;
	}
	
	public int getSize()
	{
		return size;
	}
}
