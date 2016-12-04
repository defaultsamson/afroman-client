package ca.afroman.entity.api;

import java.awt.geom.Rectangle2D;

import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;

public class Hitbox extends PositionLevelObject implements Cloneable
{
	private Rectangle2D.Double box;
	
	/**
	 * A hitbox.
	 * <p>
	 * TODO make a better Hitbox system (extend Entity)
	 * 
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param x the x ordinate of this
	 * @param y the y ordinate of this
	 * @param width the width of this
	 * @param height the height of this
	 */
	public Hitbox(boolean isServerSide, boolean isMicroManaged, double x, double y, double width, double height)
	{
		super(isServerSide, isMicroManaged, new Vector2DDouble(x, y));
		
		// If level hitbox, this.position and box.x & box.y both hold the same world coordinates
		// If relative hitbox, this.position holds the relative coordinates, and box.x & box.y hold the world coordinates
		
		box = new Rectangle2D.Double(x, y, width, height);
	}
	
	/**
	 * Removes this hitbox from its current level and puts it in another level.
	 * 
	 * @param level the new level.
	 */
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			level.getHitboxes().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getHitboxes().add(this);
		}
	}
	
	@Override
	public Hitbox clone()
	{
		Hitbox box = new Hitbox(isServerSide(), isMicroManaged(), position.getX(), position.getY(), getWidth(), getHeight());
		box.addToLevel(level);
		return box;
	}
	
	/**
	 * @param x the x ordinate to test for
	 * @param y the y ordinate to test for
	 * @return whether or not this contains the provided point.
	 */
	public boolean contains(double x, double y)
	{
		return box.contains(x, y);
	}
	
	/**
	 * @return the height of this.
	 */
	public double getHeight()
	{
		return box.getHeight();
	}
	
	/**
	 * @return the width of this.
	 */
	public double getWidth()
	{
		return box.getWidth();
	}
	
	/**
	 * Gets the in-level x ordinate of this. <code>Hitbox.getPosition()</code>
	 * will retrieve the relative position of this if it belongs to an entity
	 * and uses <code>hitbox.updateRelativeHitboxToPosition()</code> to update
	 * the in-level x ordinate for this hitbox relative to that Entity's position.
	 * 
	 * @return the in-level x ordinate.
	 */
	public double getX()
	{
		return box.getX();
	}
	
	/**
	 * Gets the in-level y ordinate of this. <code>Hitbox.getPosition()</code>
	 * will retrieve the relative position of this if it belongs to an entity
	 * and uses <code>hitbox.updateRelativeHitboxToPosition()</code> to update
	 * the in-level y ordinate for this hitbox relative to that Entity's position.
	 * 
	 * @return the in-level y ordinate.
	 */
	public double getY()
	{
		return box.getY();
	}
	
	/**
	 * @param other the Hitbox to test against
	 * 
	 * @return whether or not this is colliding with the provided Hitbox.
	 */
	public boolean isColliding(Hitbox other)
	{
		return box.intersects(other.box);
	}
	
	/**
	 * Updates this Hitbox's in-level coordinates to the provided position plus
	 * this's relative coordinates.
	 * <p>
	 * e.g. This's relative coordinates are (1, 5). The Entity that this Hitbox
	 * belongs to is at point (100, -55), and invokes this method to update the
	 * in-level coordinates of this. <code>Hitbox.getPosition()</code> will still
	 * result in the coordinates (1, 5), because that position represents the relative
	 * position. Using getX() and getY() will return point (101, -50), because
	 * the in-level coordinates of this have now updated to use the relative
	 * coordinates relative to the provided point.
	 * <p>
	 * <i>NOTE:</i> Other operations such as <code>Hitbox.isColliding()</code> and
	 * <code>Hitbox.contains()</code> will all use in-level coordinates by default.
	 * <p>
	 * If this Hitbox is static, then the relative position will already be the
	 * in-level coordinates by default, so this method does not need to be invoked.
	 * 
	 * @param pos the position to update this to (relatively).
	 */
	public void updateRelativeHitboxToPosition(Vector2DDouble pos)
	{
		box.x = position.getX() + pos.getX();
		box.y = position.getY() + pos.getY();
	}
}
