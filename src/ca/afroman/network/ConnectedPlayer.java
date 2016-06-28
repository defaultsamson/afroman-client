package ca.afroman.network;

import ca.afroman.client.Role;
import ca.afroman.util.IDCounter;

public class ConnectedPlayer
{
	private static IDCounter idCounter;
	
	public static IDCounter getIDCounter()
	{
		if (idCounter == null)
		{
			idCounter = new IDCounter();
		}
		
		return idCounter;
	}
	
	private Role role;
	private String username;
	private short id;
	
	/**
	 * A player object with a role, username, and ID number.
	 * 
	 * @param id the ID number
	 * @param role the role of the player
	 * @param username the username of the player
	 */
	public ConnectedPlayer(short id, Role role, String username)
	{
		this.id = id;
		this.role = role;
		this.username = username;
	}
	
	/**
	 * Sets the role of this player.
	 * 
	 * @param newRole the new role
	 */
	public void setRole(Role newRole)
	{
		this.role = newRole;
	}
	
	/**
	 * @return this player's role.
	 */
	public Role getRole()
	{
		return role;
	}
	
	/**
	 * @return this player's username.
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * @return this player's ID.
	 */
	public short getID()
	{
		return id;
	}
	
	public void setID(short id)
	{
		this.id = id;
	}
}
