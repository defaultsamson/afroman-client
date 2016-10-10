package ca.afroman.util;

import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.option.Options;
import ca.afroman.server.ConsoleCommand;
import ca.afroman.server.ServerGame;

public class CommandUtil
{
	private static boolean wentThroughUpdate = false;
	
	private static void displayHelp(ConsoleCommand command)
	{
		ALogger.logA("-----------------------------");
		ALogger.logA("");
		ALogger.logA("Usage: " + command.getUsage());
		ALogger.logA(command.getFullDesc());
		
		if (!ArrayUtil.isEmpty(command.getExamples()))
		{
			ALogger.logA("");
			ALogger.logA("Examples:");
			
			for (String ex : command.getExamples())
			{
				ALogger.logA(ex);
			}
		}
		
		ALogger.logA("");
	}
	
	public static void issueCommand(String input)
	{
		input = input.toUpperCase();
		
		while (input.startsWith("/"))
		{
			input = input.substring(1);
		}
		
		String[] params = input.split(" ");
		
		if (!ArrayUtil.isEmpty(params))
		{
			try
			{
				ConsoleCommand command = ConsoleCommand.valueOf(params[0]);
				
				// If the user typed "cmd help"
				if (params.length > 1 && params[1].equalsIgnoreCase("HELP"))
				{
					displayHelp(command);
				}
				else // Perform normal action
				{
					try
					{
						boolean isCommandLine = false;
						
						if (ServerGame.instance() != null)
						{
							isCommandLine = ServerGame.instance().isCommandLine();
						}
						
						switch (command)
						{
							default:
								ALogger.logA("Command not yet implemented");
								break;
							case STOP:
								if (ServerGame.instance() != null)
								{
									ALogger.logA(ALogType.DEBUG, "Stopping server...");
									ServerGame.instance().stopThis();
								}
								else
								{
									ALogger.logA(ALogType.WARNING, "Server isn't up");
								}
								break;
							case REBOOT:
								if (ServerGame.instance() != null)
								{
									if (isCommandLine)
									{
										ALogger.logA(ALogType.DEBUG, "Rebooting server...");
										ServerGame.instance().stopThis();
										
										while (ServerGame.instance() != null)
										{
											try
											{
												Thread.sleep(10);
											}
											catch (InterruptedException e)
											{
												e.printStackTrace();
											}
										}
										
										ALogger.logA(ALogType.DEBUG, "Starting server...");
										
										new ServerGame(isCommandLine, Options.instance().serverIP, Options.instance().serverPassword, Options.instance().serverPort);
									}
									else
									{
										ALogger.logA(ALogType.DEBUG, "Cannot reboot server on client");
									}
								}
								else
								{
									ALogger.logA(ALogType.WARNING, "Server isn't up");
								}
								break;
							case HELP:
								if (params.length > 1)
								{
									ConsoleCommand cmm = ConsoleCommand.valueOf(params[1]);
									if (cmm != null)
									{
										displayHelp(cmm);
										break;
									}
								}
								
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
							case UPDATE:
								if (wentThroughUpdate)
								{
									UpdateUtil.applyUpdate(isCommandLine);
								}
								
								if (UpdateUtil.updateQuery())
								{
									ALogger.logA("Update found (" + VersionUtil.toString(UpdateUtil.serverVersion) + ")");
									ALogger.logA("Type \"update\" again to update");
									wentThroughUpdate = true;
								}
								else
								{
									ALogger.logA("No updates found");
								}
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
}
