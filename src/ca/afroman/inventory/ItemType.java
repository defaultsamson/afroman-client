package ca.afroman.inventory;

public enum ItemType
{
	HAIR_PIN(3),
	BRASS_KNUCKLES(1),
	HAIR_PICK(2);
	
	private int maxStackSize;
	
	ItemType(int maxStackSize)
	{
		this.maxStackSize = maxStackSize;
	}
	
	public int getMaxStackSize()
	{
		return maxStackSize;
	}
}
