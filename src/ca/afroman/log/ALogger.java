package ca.afroman.log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.logging.Logger;

import ca.afroman.stream.ConsoleOutputStream;
import ca.afroman.stream.MultiOutputStream;

public class ALogger extends Logger
{
	public static boolean tracePackets = true;
	
	private static MultiOutputStream out = null;
	private static MultiOutputStream err = null;
	
	/**
	 * Initialised the out and err streams.
	 */
	public static void initStreams()
	{
		if (out == null)
		{
			ConsoleOutputStream con = new ConsoleOutputStream();
			
			FileOutputStream fil = null;
			try
			{
				fil = new FileOutputStream("C:\\Users\\qwertysam\\Desktop\\log.txt");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			
			out = new MultiOutputStream(con, fil, System.out);
			err = new MultiOutputStream(con, fil, System.err);
			
			System.setOut(new PrintStream(out));
			System.setErr(new PrintStream(err));
		}
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
		System.out.println("[" + getDateAndTime() + "] [" + this.getName() + "] [" + type.toString() + "] " + msg);
	}
	
	public void log(ALogType type, String msg, Throwable thr)
	{
		this.log(type.getLevel(), msg, thr);
		System.err.println("[" + getDateAndTime() + "] [" + this.getName() + "] [" + type.toString() + "] " + msg + " (" + thr.getMessage() + ")");
		thr.printStackTrace();
	}
	
	/**
	 * @return YYYY-MM-DD/HH:MM:SS
	 */
	private static String getDateAndTime()
	{
		Calendar cal = Calendar.getInstance(); // TimeZone.getTimeZone("EST")
		
		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) < 10 ? "0" : "") + cal.get(Calendar.MONTH) + "-" + (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE) < 10 ? "0" : "") + cal.get(Calendar.MINUTE) + ":" + (cal.get(Calendar.SECOND) < 10 ? "0" : "") + cal.get(Calendar.SECOND);
	}
}
