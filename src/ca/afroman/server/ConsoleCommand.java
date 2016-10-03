package ca.afroman.server;

public enum ConsoleCommand
{
	STOP("Stops the server", "Stops the server, disconnecting all players, and saving the game state.", "stop"),
	SAVE("Saves the game", "Forces the server to save the game state at the current moment.", "save"),
	DESTROY("Resets the game", "Resets the game to the very beginning, deleting any previous save file.", "destroy"),
	REBOOT("Reboots the server", "Stops the server and starts it back up immediately", "reboot"),
	HELP("Displays commands", "Displays a list of commands which can be issued to control the server.", "help [command]");
	
	private String sh;
	private String full;
	private String use;
	
	ConsoleCommand(String shortDesc, String fullDesc, String usage)
	{
		sh = shortDesc;
		full = fullDesc;
		use = usage;
	}
	
	public String getFullDesc()
	{
		return full;
	}
	
	public String getShortDesc()
	{
		return sh;
	}
	
	public String getUsage()
	{
		return use;
	}
}
