package ca.afroman.util;

public class CastUtil
{
	/**
	 * Takes a double and returns a displayable String
	 * of it. If it ends with .0 then it will be
	 * shortened to save space and make things easier to read.
	 * <p>
	 * e.g. Inputting 5.0 will return "5".
	 * <p>
	 * Inputting 3.2 will return "3.2".
	 * 
	 * @return
	 */
	public static String normalizeDouble(double num)
	{
		return new StringBuilder().append(num).toString().replace(".0", "");
	}
}
