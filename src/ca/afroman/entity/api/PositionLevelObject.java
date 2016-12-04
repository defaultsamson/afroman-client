package ca.afroman.entity.api;

import ca.afroman.level.api.Level;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.resource.Vector2DDouble;

public abstract class PositionLevelObject extends ServerClientObject
{
	private boolean isMicromanaged;
	protected Level level;
	protected Vector2DDouble position;
	
	/**
	 * Creates a new object with a level and position.
	 * 
	 * @param isServerSide whether this is on the server instance or not
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param position the position
	 */
	public PositionLevelObject(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position)
	{
		super(isServerSide);
		
		this.isMicromanaged = isMicromanaged;
		this.level = null;
		this.position = position;
	}
	
	/**
	 * Removes this from its current level and puts it in another level.
	 * 
	 * @param level the new level
	 */
	public abstract void addToLevel(Level newLevel);
	
	/**
	 * @return the level that this is in.
	 */
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * @return the position of this.
	 */
	public Vector2DDouble getPosition()
	{
		return position;
	}
	
	/**
	 * @return if this is managed by a manager such as an Event object.
	 */
	public boolean isMicroManaged()
	{
		return isMicromanaged;
	}
	
	/**
	 * Method runs when this has been interacted with.
	 */
	public void onInteract()
	{
		// TODO actually trigger this? or should this be trashed?
	}
	
	/**
	 * Removes this from its current level.
	 */
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	/**
	 * <i>Designed for use from the server only.</i>
	 * <p>
	 * Sets the position of this to the provided position.
	 * 
	 * @param position the new position
	 */
	public void setPosition(Vector2DDouble position)
	{
		this.position.setVector(position);
	}
}
