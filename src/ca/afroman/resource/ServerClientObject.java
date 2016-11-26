package ca.afroman.resource;

public class ServerClientObject
{
	private boolean isServerSide;
	
	public ServerClientObject(boolean isServerSide)
	{
		this.isServerSide = isServerSide;
	}
	
	public boolean isServerSide()
	{
		return isServerSide;
	}
}
