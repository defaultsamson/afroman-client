package ca.afroman.level.api;

public enum LevelType
{
	NULL(null),
	MAIN("level1.txt"),
	TEST("level2.txt"),
	SECOND("level3.txt");
	
	public static LevelType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	private String fileName;
	
	LevelType(String fileName)
	{
		this.fileName = fileName;
	}
	
	public String getFileName()
	{
		return fileName;
	}
}
