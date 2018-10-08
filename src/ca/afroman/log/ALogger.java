package ca.afroman.log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.logging.Logger;

import samson.stream.Console;

public class ALogger extends Logger
{
	public static boolean tracePackets = true;
	
	/**
	 * @return YYYY-MM-DD/HH:MM:SS
	 */
	private static String getDateAndTime()
	{
		Calendar cal = Calendar.getInstance(); // TimeZone.getTimeZone("EST")
		
		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) < 10 ? "0" : "") + cal.get(Calendar.MONTH) + "-" + (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE) < 10 ? "0" : "") + cal.get(Calendar.MINUTE) + ":" + (cal.get(Calendar.SECOND) < 10 ? "0" : "") + cal.get(Calendar.SECOND);
	}
	
	/**
	 * Initialises the file log streams.
	 */
	public static void initStreams()
	{
		FileOutputStream fil = null;
		try
		{
			fil = new FileOutputStream("log.txt");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		Console.getOutputStream().addStream(fil);
		Console.getErrorStream().addStream(fil);
	}
	
	/**
	 * Logs a message anonymously. (Doesn't sign message with thread stamp.)
	 * 
	 * @param type
	 * @param msg
	 */
	public static void logA(ALogType type, String msg)
	{
		System.out.println("[" + getDateAndTime() + "] [" + type + "] " + msg);
	}
	
	/**
	 * Logs a message anonymously. (Doesn't sign message with thread stamp.)
	 * 
	 * @param type
	 * @param msg
	 */
	public static void logA(ALogType type, String msg, Throwable thr)
	{
		System.err.println("[" + getDateAndTime() + "] [" + type + "] " + msg + " (" + thr.getMessage() + ")");
		thr.printStackTrace();
	}
	
	/**
	 * Logs a message anonymously. (Doesn't sign message with thread stamp.)
	 * 
	 * @param type
	 * @param msg
	 */
	public static void logA(String msg)
	{
		System.out.println("[" + getDateAndTime() + "] " + msg);
	}
	
	public ALogger(String name)
	{
		this(name, null);
	}
	
	private ALogger(String name, String resourceBundleName)
	{
		super(name, resourceBundleName);
	}
	
	public void log(ALogType type, String msg)
	{
		this.log(type.getLevel(), msg);
		System.out.println("[" + getDateAndTime() + "] [" + this.getName() + "] [" + type + "] " + msg);
	}
	
	public void log(ALogType type, String msg, Throwable thr)
	{
		this.log(type.getLevel(), msg, thr);
		System.err.println("[" + getDateAndTime() + "] [" + this.getName() + "] [" + type + "] " + msg + " (" + thr.getMessage() + ")");
		thr.printStackTrace();
	}
}
