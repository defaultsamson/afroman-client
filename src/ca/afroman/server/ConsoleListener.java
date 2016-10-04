package ca.afroman.server;

import java.util.Scanner;

import ca.afroman.log.ALogType;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.CommandUtil;

public class ConsoleListener extends DynamicThread
{
	Scanner sc;
	
	public ConsoleListener()
	{
		super(ServerGame.instance().getThreadGroup(), "TypeListener");
	}
	
	@Override
	public void onRun()
	{
		try
		{
			String text = sc.nextLine();
			CommandUtil.issueCommand(text);
		}
		catch (Exception e)
		{
			logger().log(ALogType.WARNING, "Error listening for command", e);
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		sc = new Scanner(System.in);
	}
}
