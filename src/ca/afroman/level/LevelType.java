package ca.afroman.level;

public enum LevelType
{
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
		return values()[ordinal];
	}
}
