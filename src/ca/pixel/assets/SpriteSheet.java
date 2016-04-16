package ca.pixel.assets;

import java.awt.image.BufferedImage;

public class SpriteSheet
{
	private BufferedImage spriteSheet;
	
	public SpriteSheet(BufferedImage spriteSheet)
	{
		this.spriteSheet = spriteSheet;
	}
	
	/**
	 * Gets a tile from this.
	 * 
	 * @param xPos the top left x position to start from
	 * @param yPos the top left y position to start from
	 * @param width the width of the sprite
	 * @param height the height of the sprite
	 * @return the single tile
	 */
	public BufferedImage getTile(int xPos, int yPos, int width, int height)
	{
		return spriteSheet.getSubimage(xPos, yPos, width, height);
	}
}
