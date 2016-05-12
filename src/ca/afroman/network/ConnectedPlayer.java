package ca.afroman.network;

import ca.afroman.player.Role;

public class ConnectedPlayer
{
	private static int nextAvailableID = 0;
	
	private Role role;
	private String username;
	private int id;
	
	/**
	 * A player object with a role, username, and the next available ID number.
	 * 
	 * @param role the role of the player
	 * @param username the username of the player
	 */
	public ConnectedPlayer(Role role, String username)
	{
		this(getNextAvailableID(), role, username);
	}
	
	/**
	 * A player object with a role, username, and ID number.
	 * 
	 * @param id the ID number
	 * @param role the role of the player
	 * @param username the username of the player
	 */
	public ConnectedPlayer(int id, Role role, String username)
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
	public int getID()
	{
		return id;
	}
	
	/**
	 * @return the next available ID for use. (Ignored previous ID's that are now free for use. TODO?)
	 */
	public static int getNextAvailableID()
	{
		int toReturn = nextAvailableID;
		nextAvailableID++;
		return toReturn;
	}
	
	/**
	 * Resets the nextAvailableID so that it starts counting from 0 again.
	 * <p>
	 * <b>WARNING: </b>only intended for use on server shutdowns.
	 */
	public static void resetNextAvailableID()
	{
		nextAvailableID = 0;
	}
}
