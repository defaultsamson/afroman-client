package ca.afroman.log;

import java.util.logging.Logger;

public class ALogger extends Logger
{
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
		System.out.println("[" + this.getName() + "] [" + type.toString() + "] " + msg);
	}
	
	public void log(ALogType type, String msg, Throwable thr)
	{
		this.log(type.getLevel(), msg, thr);
		System.out.println("[" + this.getName() + "] [" + type.toString() + "] " + msg + " (" + thr.getMessage() + ")");
		thr.printStackTrace();
	}
}
