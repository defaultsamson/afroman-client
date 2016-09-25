package ca.afroman.util;

public class ParamUtil
{
	public static String[] getParameters(String in)
	{
		return in.length() > 0 ? in.split(", ") : null;
	}
	
	public static String[] getRawSubParameters(String in)
	{
		int count = in.split("\\{").length - 1;
		
		if (count >= 1)
		{
			String[] ret = new String[count];
			
			// isolates all the sub-parameters
			for (int i = 0; i < count; i++)
			{
				String[] r1 = in.split("\\{")[1 + i].split("\\}");
				ret[i] = r1.length > 0 ? r1[0] : "";
			}
			
			return ret;
		}
		else
		{
			return null;
		}
	}
}
