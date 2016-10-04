package ca.afroman.util;

public enum FileType
{
	INVALID(""),
	EXE("exe"),
	JAR("jar");
	
	private String ext;
	
	FileType(String extension)
	{
		ext = extension;
	}
	
	public String getExtension()
	{
		return ext;
	}
}
