package ca.pixel.game.gfx;

import java.util.Random;

public class TextureArray
{
	/** Holds the textures for each character. */
	private Texture[] textures;
	
	/**
	 * Creates an array of textures from a sheet.
	 * <p>
	 * <b>WARNING: </b> This constructor assumes that the width and height of each texture
	 * is the same. If not all the textures have the same dimensions, this will break.
	 * 
	 * @param sheet the sheet
	 * @param xColumns how many textures there are in the horizontal plane
	 * @param yRows how many textures there are in the vertical plane
	 */
	public TextureArray(Texture sheet, int xColumns, int yRows)
	{
		this(sheet, xColumns, xColumns, sheet.getWidth() / xColumns, sheet.getHeight() / yRows);
	}
	
	/**
	 * Creates an array of textures from a sheet.
	 * <p>
	 * <b>WARNING: </b> This constructor assumes that the width and height of each texture
	 * is the same. If not all the textures have the same dimensions, this will break.
	 * 
	 * @param sheet the sheet
	 * @param xColumns how many textures there are in the horizontal plane
	 * @param yRows how many textures there are in the vertical plane
	 * @param width the width of each texture
	 * @param height the height of each texture
	 */
	public TextureArray(Texture sheet, int xColumns, int yRows, int width, int height)
	{
		textures = new Texture[xColumns * yRows];
		
		for (int y = 0; y < yRows; y++)
		{
			for (int x = 0; x < xColumns; x++)
			{
				textures[(y * xColumns) + x] = sheet.getSubTexture(x * width, y * height, width, height);
			}
		}
	}
	
	public Texture getTexture(int index)
	{
		return textures[index];
	}
	
	public Texture[] getTexturs(int index)
	{
		return textures;
	}
	
	public int length()
	{
		return textures.length;
	}
	
	public Texture getRandomTexture()
	{
		return getTexture(new Random().nextInt(length()));
	}
	
	public Texture getRandomTexture(int xSeed, int ySeed)
	{
		return getTexture(new Random(xSeed << 16 + ySeed).nextInt(length()));
	}
}
