package ca.afroman.server;

import java.util.Scanner;

import ca.afroman.log.ALogType;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.CommandUtil;

public class ConsoleListener extends DynamicThread
{
	private static ConsoleListener game = null;
	
	public static ConsoleListener instance()
	{
		return game;
	}
	
	private Scanner sc = null;
	
	public ConsoleListener(boolean isServerSide)
	{
		super(isServerSide, ServerGame.instance().getThread().getThreadGroup(), "TypeListener");
		
		sc = new Scanner(System.in);
		
		if (game == null)
		{
			game = this;
		}
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
	public void stopThis()
	{
		super.stopThis();
		
		sc.close();
		game = null;
	}
}
