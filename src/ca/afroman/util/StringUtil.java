package ca.afroman.util;

public class StringUtil
{
	public static int getLengthOfLongestString(String... strings)
	{
		int longest = 0;
		
		for (int i = 0; i < strings.length; i++)
		{
			String test = strings[i];
			if (test != null)
			{
				longest = Math.max(longest, test.length());
			}
		}
		
		return longest;
	}
}
