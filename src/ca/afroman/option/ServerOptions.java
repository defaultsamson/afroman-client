package ca.afroman.option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.game.Game;
import ca.afroman.log.ALogType;
import ca.afroman.server.ServerGame;
import ca.afroman.util.FileUtil;

public class ServerOptions
{
	public static final String OPTIONS_FILE = "server_options.txt";
	private static final String SPLITTER = ":";
	
	private static ServerOptions instance = null;
	
	public static ServerOptions instance()
	{
		if (instance == null)
		{
			instance = new ServerOptions();
			instance.initializeValues();
			instance.load();
		}
		return instance;
	}
	
	public String serverPassword;
	public String serverIP;
	public String serverPort;
	
	private void append(List<String> list, ServerOptionType type, boolean value)
	{
		list.add(type + SPLITTER + value);
	}
	
	private void append(List<String> list, ServerOptionType type, String value)
	{
		list.add(type + SPLITTER + value);
	}
	
	public void initializeValues()
	{
		serverPassword = "";
		serverIP = "" + Game.IPv4_LOCALHOST;
		serverPort = "" + Game.DEFAULT_PORT;
	}
	
	public void load()
	{
		File file = new File(OPTIONS_FILE);
		if (file.exists())
		{
			List<String> lines = FileUtil.readAllLines(file);
			
			for (String line : lines)
			{
				try
				{
					String[] split = line.split("\\" + SPLITTER);
					ServerOptionType type = ServerOptionType.valueOf(split[0]);
					String option = split.length > 1 && split[1] != null ? split[1] : "";
					
					switch (type)
					{
						default:
							ServerGame.instance().logger().log(ALogType.WARNING, "No OptionType found for type: " + type);
							break;
						case SERVER_PASSWORD:
							serverPassword = option;
							break;
						case SERVER_IP:
							serverIP = option;
							break;
						case SERVER_PORT:
							serverPort = option;
							break;
					}
				}
				catch (Exception e)
				{
					System.out.println("Failed to load line: " + line);
				}
			}
		}
		else
		{
			System.out.println("No options file found, creating " + OPTIONS_FILE);
			save();
		}
	}
	
	public void save()
	{
		List<String> op = new ArrayList<String>();
		append(op, ServerOptionType.SERVER_PASSWORD, serverPassword);
		append(op, ServerOptionType.SERVER_IP, serverIP);
		append(op, ServerOptionType.SERVER_PORT, serverPort);
		
		FileUtil.writeLines(op, new File(OPTIONS_FILE));
	}
}
