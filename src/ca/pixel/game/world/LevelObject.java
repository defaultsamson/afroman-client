package ca.pixel.game.world;

import java.awt.Rectangle;

import ca.pixel.game.gfx.Texture;

public abstract class LevelObject
{
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected Level level;
	protected Rectangle[] hitboxes;
	
	public LevelObject(Level level, int x, int y, Rectangle... hitboxes)
	{
		// Instansiates to the first rectangle
		int lowestX = hitboxes[0].x;
		int highestX = lowestX + hitboxes[0].width;
		int lowestY = hitboxes[0].y;
		int highestY = lowestY + hitboxes[0].width;
		
		// Goes through all the hitboxes to find the maximum bounds
		for (Rectangle rect : hitboxes)
		{
			if (rect.x < lowestX)
			{
				lowestX = rect.x;
			}
			if (rect.x + rect.width > highestX)
			{
				highestX = rect.x + rect.width;
			}
			
			if (rect.y < lowestY)
			{
				lowestY = rect.y;
			}
			if (rect.y + rect.height > highestY)
			{
				highestY = rect.y + rect.height;
			}
			
			// Sets the new hitbox coordinates to in-world coordinates
			rect.x += x;
			rect.y += y;
		}
		
		this.x = x;
		this.y = y;
		
		this.width = highestX - lowestX;
		this.height = highestY - lowestY;
		this.level = level;
		this.hitboxes = hitboxes;
	}
	
	public static Rectangle rectToWorld(Rectangle rect, int objectX, int objectY)
	{
		return new Rectangle(rect.x + objectX, rect.y + objectY, rect.width, rect.height);
	}
	
	public void setX(int newX)
	{
		this.x = newX;
	}
	
	public void setY(int newY)
	{
		this.y = newY;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	/**
	 * @return the width of the hitbox.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * @return the height of the hitbox.
	 */
	public int getHeight()
	{
		return height;
	}
	
	public boolean isColliding(LevelObject other)
	{
		for (Rectangle oBox : other.hitboxes)
		{
			for (Rectangle box : hitboxes)
			{
				// If the hitboxes are colliding in world
				if (rectToWorld(oBox, other.x, other.y).intersects(rectToWorld(box, x, y))) return true;
			}
		}
		
		return false;
	}
	
	public abstract void tick();
	
	public abstract void render(Texture renderTo);
}
