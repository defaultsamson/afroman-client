package ca.afroman.server;

public enum ConsoleCommand
{
	STOP("Stops the server", "Stops the server, disconnecting all players, and saving the game state.", "stop"),
	SAVE("Saves the game", "Forces the server to save the game state at the current moment.", "save"),
	DESTROY("Resets the game", "Resets the game to the very beginning, deleting any previous save file.", "destroy"),
	REBOOT("Reboots the server", "Stops the server and starts it back up immediately", "reboot"),
	HELP("Displays commands", "Displays a list of commands which can be issued to control the server.", "help [command]", "help save - Displays help information about the save command."),
	TP("Teleports player", "Teleports a player to a location. Optionally, can be teleported into a different level.", "tp <playerNum> <x> <y> [levelNum]", "tp 0 512 -32 0 - Teleports player 1 to (512, -32) in level 1.", "tp 1 2 444 4 - Teleports player 2 to (2, 444) in level 5.", "tp 1 1 1 - Teleports player 2 to (1, 1) in their current level.");
	
	private String sh;
	private String full;
	private String use;
	private String[] ex;
	
	ConsoleCommand(String shortDesc, String fullDesc, String usage, String... example)
	{
		sh = shortDesc;
		full = fullDesc;
		use = usage;
		this.ex = example;
	}
	
	public String[] getExamples()
	{
		return ex;
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
