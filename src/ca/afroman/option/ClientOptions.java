package ca.afroman.option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.gfx.LightMapState;
import ca.afroman.log.ALogType;
import ca.afroman.util.FileUtil;

public class ClientOptions
{
	public static final String OPTIONS_FILE = "options.txt";
	private static final String SPLITTER = ":";
	
	private static ClientOptions instance = null;
	
	public static ClientOptions instance()
	{
		if (instance == null)
		{
			instance = new ClientOptions();
			instance.initializeValues();
			instance.load();
		}
		return instance;
	}
	
	public boolean enableMusic;
	public String serverUsername;
	public String clientUsername;
	public String clientPassword;
	public String clientIP;
	public String clientPort;
	public boolean renderOffFocus;
	public boolean fullscreen;
	public LightMapState lighting;
	
	private void append(List<String> list, ClientOptionType type, boolean value)
	{
		list.add(type + SPLITTER + value);
	}
	
	private void append(List<String> list, ClientOptionType type, String value)
	{
		list.add(type + SPLITTER + value);
	}
	
	public void initializeValues()
	{
		enableMusic = true;
		serverUsername = "";
		clientUsername = "";
		clientPassword = "";
		clientIP = "";
		clientPort = "";
		renderOffFocus = true;
		fullscreen = false;
		lighting = LightMapState.ON;
	}
	
	public boolean isLightingOn()
	{
		return lighting != LightMapState.OFF;
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
					ClientOptionType type = ClientOptionType.valueOf(split[0]);
					String option = split.length > 1 && split[1] != null ? split[1] : "";
					
					switch (type)
					{
						default:
							ClientGame.instance().logger().log(ALogType.WARNING, "No OptionType found for type: " + type);
							break;
						case MUSIC:
							enableMusic = Boolean.parseBoolean(option);
							break;
						case SERVER_USERNAME:
							serverUsername = option;
							break;
						case CLIENT_USERNAME:
							clientUsername = option;
							break;
						case CLIENT_PASSWORD:
							clientPassword = option;
							break;
						case CLIENT_IP:
							clientIP = option;
							break;
						case CLIENT_PORT:
							clientPort = option;
							break;
						case RENDER_OFF_FOCUS:
							renderOffFocus = Boolean.parseBoolean(option);
							break;
						case FULLSCREEN:
							fullscreen = Boolean.parseBoolean(option);
							break;
						case LIGHT_MODE:
							lighting = LightMapState.valueOf(option);
							break;
					}
				}
				catch (Exception e)
				{
					ClientGame.instance().logger().log(ALogType.WARNING, "Failed to load line: " + line);
				}
			}
		}
		else
		{
			ClientGame.instance().logger().log(ALogType.WARNING, "No options file found, creating " + OPTIONS_FILE);
			save();
		}
	}
	
	public void save()
	{
		List<String> op = new ArrayList<String>();
		append(op, ClientOptionType.MUSIC, enableMusic);
		append(op, ClientOptionType.SERVER_USERNAME, serverUsername);
		append(op, ClientOptionType.CLIENT_USERNAME, clientUsername);
		append(op, ClientOptionType.CLIENT_PASSWORD, clientPassword);
		append(op, ClientOptionType.CLIENT_IP, clientIP);
		append(op, ClientOptionType.CLIENT_PORT, clientPort);
		append(op, ClientOptionType.RENDER_OFF_FOCUS, renderOffFocus);
		append(op, ClientOptionType.FULLSCREEN, fullscreen);
		append(op, ClientOptionType.LIGHT_MODE, lighting.toString());
		
		FileUtil.writeLines(op, new File(OPTIONS_FILE));
	}
}
