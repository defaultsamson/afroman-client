package ca.afroman.level;

public enum LevelType
{
	NULL(null),
	MAIN("level1.txt");
	
	LevelType(String fileName)
	{
		this.fileName = fileName;
	}
	
	private String fileName;
	
	public String getFileName()
	{
		return fileName;
	}
	
	public static LevelType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
