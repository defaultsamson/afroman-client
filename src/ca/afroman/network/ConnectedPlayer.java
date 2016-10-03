package ca.afroman.network;

import ca.afroman.game.Role;
import ca.afroman.resource.IDCounter;

public class ConnectedPlayer
{
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
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
	 * @return this player's ID.
	 */
	public short getID()
	{
		return id;
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
	
	public void setID(short id)
	{
		this.id = id;
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
}
