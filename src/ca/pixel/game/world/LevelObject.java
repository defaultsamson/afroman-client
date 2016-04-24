package ca.pixel.game.world;

import java.awt.Color;
import java.awt.Rectangle;

import ca.pixel.game.Game;
import ca.pixel.game.assets.Texture;

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
		this.width = 0;
		this.height = 0;
		
		// Instansiates to the first rectangle
		if (hitboxes[0] != null)
		{
			int lowestX = hitboxes[0].x;
			int highestX = lowestX + hitboxes[0].width;
			int lowestY = hitboxes[0].y;
			int highestY = lowestY + hitboxes[0].height;
			
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
			}
			
			this.width = highestX - lowestX;
			this.height = highestY - lowestY;
		}
		
		this.x = x;
		this.y = y;
		
		this.level = level;
		this.hitboxes = hitboxes;
	}
	
	public Rectangle[] hitboxInWorld()
	{
		Rectangle[] boxes = new Rectangle[hitboxes.length];
		
		for (int i = 0; i < hitboxes.length; i++)
		{
			boxes[i] = new Rectangle(hitboxes[i].x + x, hitboxes[i].y + y, hitboxes[i].width, hitboxes[i].height);
		}
		
		return boxes;
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
	
	public Level getLevel()
	{
		return level;
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
		if (this.hasHitbox() && other.hasHitbox())
		{
			return isColliding(other.hitboxInWorld());
		}
		return false;
	}
	
	public boolean isColliding(Rectangle... worldHitboxes)
	{
		for (Rectangle box : this.hitboxInWorld())
		{
			for (Rectangle oBox : worldHitboxes)
			{
				// If the hitboxes are colliding in world
				if (oBox.intersects(box)) return true;
			}
		}
		return false;
	}
	
	public abstract void tick();
	
	public boolean hasHitbox()
	{
		return hitboxes[0] != null;
	}
	
	public void render(Texture renderTo)
	{
		if (Game.instance().isHitboxDebugging())
		{
			if (this.hasHitbox())
			{
				for (Rectangle box : this.hitboxInWorld())
				{
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 0.3F));
					renderTo.getGraphics().fillRect(box.x - level.getCameraXOffset(), box.y - level.getCameraYOffset(), box.width - 1, box.height - 1);
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
					renderTo.getGraphics().drawRect(box.x - level.getCameraXOffset(), box.y - level.getCameraYOffset(), box.width - 1, box.height - 1);
				}
			}
		}
	}
}
