package ca.afroman.util;

public class OsUtil
{
	public static EnumOS getOSType()
	{
		String var0 = System.getProperty("os.name").toLowerCase();
		return var0.contains("win") ? EnumOS.WINDOWS : (var0.contains("mac") ? EnumOS.OSX : (var0.contains("solaris") ? EnumOS.SOLARIS : (var0.contains("sunos") ? EnumOS.SOLARIS : (var0.contains("linux") ? EnumOS.LINUX : (var0.contains("unix") ? EnumOS.LINUX : EnumOS.UNKNOWN)))));
	}
}
