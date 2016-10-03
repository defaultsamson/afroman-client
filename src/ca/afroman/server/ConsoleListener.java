package ca.afroman.server;

import java.util.Scanner;

import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.option.Options;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.ArrayUtil;

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
			text = text.toUpperCase();
			String[] params = text.split(" ");
			
			if (!ArrayUtil.isEmpty(params))
			{
				try
				{
					ConsoleCommand command = ConsoleCommand.valueOf(params[0]);
					
					// If the user typed "cmd help"
					if (params.length > 1 && params[1].equalsIgnoreCase("HELP"))
					{
						ALogger.logA("");
						ALogger.logA("Usage: " + command.getUsage());
						ALogger.logA("");
						ALogger.logA(command.getFullDesc());
						ALogger.logA("");
					}
					else // Perform normal action
					{
						try
						{
							switch (command)
							{
								default:
									ALogger.logA("Command not yet implemented");
									break;
								case STOP:
									logger().log(ALogType.DEBUG, "Stopping server...");
									ServerGame.instance().stopThis();
									stopThis();
									break;
								case REBOOT:
									logger().log(ALogType.DEBUG, "Rebooting server...");
									ServerGame.instance().stopThis();
									
									while (ServerGame.instance() != null)
									{
										try
										{
											Thread.sleep(100);
										}
										catch (InterruptedException e)
										{
											e.printStackTrace();
										}
									}
									
									logger().log(ALogType.DEBUG, "Starting server...");
									
									new ServerGame(Options.instance().serverIP, Options.instance().serverPassword, Options.instance().serverPort);
									
									break;
								case HELP:
									ALogger.logA("");
									ALogger.logA("HELP - " + ConsoleCommand.HELP.getUsage());
									ALogger.logA("");
									ALogger.logA("Commands:");
									
									for (ConsoleCommand cmd : ConsoleCommand.values())
									{
										ALogger.logA(cmd.toString().toLowerCase() + " - " + cmd.getShortDesc());
									}
									
									ALogger.logA("");
									break;
							}
						}
						catch (Exception e)
						{
							ALogger.logA(ALogType.DEBUG, "Invalid arguments, see \"help " + command.toString().toLowerCase() + "\" ");
						}
					}
				}
				catch (Exception e)
				{
					ALogger.logA(ALogType.DEBUG, "Invalid command, see the \"help\" command");
				}
			}
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
