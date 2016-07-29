package ca.afroman.input;

public class TypingModeWrapper
{
	private TypingMode[] modes;
	
	public TypingModeWrapper(TypingMode... modes)
	{
		this.modes = modes;
	}
	
	public boolean contains(TypingMode mode)
	{
		for (TypingMode m : modes)
		{
			if (m == mode) return true;
		}
		
		return false;
	}
}
