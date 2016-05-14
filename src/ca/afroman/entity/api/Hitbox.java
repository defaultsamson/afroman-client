package ca.afroman.entity.api;

import java.awt.geom.Rectangle2D;

public class Hitbox extends Rectangle2D.Double
{
	private static int nextAvailableID = 0;
	
	// All the required variables needed to create an Entity
	private int id;
	
	private static final long serialVersionUID = -318324421701678550L;
	
	public Hitbox(double x, double y, double width, double height)
	{
		this(-1, x, y, width, height);
	}
	
	public Hitbox(int id, double x, double y, double width, double height)
	{
		super(x, y, width, height);
		
		this.id = id;
	}
	
	/**
	 * @return this hitbox's ID.
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
