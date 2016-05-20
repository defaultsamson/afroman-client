package ca.afroman.console;

public class DebugTimer
{
	private static String type;
	private static long startTime;
	
	public static void start(String type)
	{
		DebugTimer.type = type;
		DebugTimer.startTime = System.currentTimeMillis();
	}
	
	public static void stop()
	{
		long timeTook = System.currentTimeMillis() - startTime;
		System.out.println("Timer " + type + " took " + timeTook + " ms");
	}
}
