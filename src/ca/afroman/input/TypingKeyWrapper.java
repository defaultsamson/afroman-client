package ca.afroman.input;

public class TypingKeyWrapper
{
	private Key key;
	private String shifting;
	private TypingModeWrapper modes1;
	private String normal;
	private TypingModeWrapper modes2;
	
	public TypingKeyWrapper(Key key, String normal, TypingModeWrapper modes)
	{
		this(key, normal, modes, normal, modes);
	}
	
	public TypingKeyWrapper(Key key, String shifting, TypingModeWrapper modes1, String normal, TypingModeWrapper modes2)
	{
		this.key = key;
		this.shifting = shifting;
		this.normal = normal;
		this.modes1 = modes1;
		this.modes2 = modes2;
	}
	
	public String getTypedChar(boolean shifting, TypingMode mode)
	{
		if (shifting)
		{
			if (modes1.contains(mode)) return getShiftingChar();
		}
		else
		{
			if (modes2.contains(mode)) return getNormalChar();
		}
		
		return "";
	}
	
	public String getShiftingChar()
	{
		return shifting;
	}
	
	public String getNormalChar()
	{
		return normal;
	}
	
	public Key getKey()
	{
		return key;
	}
}
