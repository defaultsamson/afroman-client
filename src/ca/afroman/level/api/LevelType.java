package ca.afroman.level.api;

public enum LevelType
{
	MAIN("Main"),
	TEST("Testing"),
	SECOND("Second");
	
	public static LevelType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	private String name;
	
	LevelType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
