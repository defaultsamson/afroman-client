package ca.afroman.assets;

import ca.afroman.resource.Vector2DInt;

public class Font extends DrawableAssetArray
{
	/** The width of a single character */
	private static final int SPACE_WIDTH = 3;
	private static final int ROWS = 4;
	private static final int COLUMNS = 32;
	
	private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&" + "abcdefghijklmnopqrstuvwxyz'()*+," + "0123456789:;<=>?[\\]^_`{|}~-./" + "";
	public static final int[] DEFAULT_WIDTHS = { 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 4, 6, 6, 6, 6, /* ln 2 */ 6, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5, 4, 6, 5, 5, 5, 5, 5, 5, 4, 5, 6, 6, 5, 5, 5, 4, 3, 3, 6, 6, 3 /* ln 3 */, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 3, 3, 4, 6, 4, 6, 4, 6, 4, 6, 6, 4, 5, 2, 5, 6, 6, 3, 6 };
	// public static final int[] NOBLE_WIDTHS = { 7, 7, 7, 7, 7, 7, 7, 7, 5, 6, 7, 7, 7, 7, 6, 7, 6, 7, 6, 7, 7, 7, 7, 7, 7, 7, 4, 6, 1, 1, 1, 1, /* ln 2 */ 7, 7, 6, 6, 7, 7, 7, 6, 3, 3, 6, 3, 7, 6, 7, 7, 7, 7, 6, 6, 7, 7, 7, 8, 6, 8, 3, 1, 1, 1, 1, 3, /* ln 3 */ 7, 6, 6, 6, 5, 6, 6, 6, 6, 6, 3, 1, 1, 7, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 5 };
	
	/**
	 * Loads a writable font from a texture.
	 * <p>
	 * <b>WARNING: </b> The fontTexture must be <i>EXACTLY</i> (256 x 32) pixels, else the loading scheme will not work.
	 * 
	 * @param fontTexture the texture to load the font from
	 */
	public Font(AssetType type, Texture fontTexture, int[] widths)
	{
		super(type, fontTexture.toTextureArray(COLUMNS, ROWS));
		
		for (int i = 0; i < CHARS.length(); i++)
		{
			assets[i] = ((Texture) assets[i]).getSubTexture(assets[i].getAssetType(), 0, 0, widths[i], assets[i].getHeight());
		}
	}
	
	public int getWidth(int charIndex)
	{
		return getDrawableAsset(charIndex).getWidth();
	}
	
	public int getWidth(String message)
	{
		int x = 0;
		for (int i = 0; i < message.length(); i++)
		{
			int charIndex = CHARS.indexOf(message.charAt(i));
			if (charIndex >= 0)
			{
				x += getDrawableAsset(charIndex).getWidth(); // CHAR_WIDTH;
			}
			else
			{
				x += SPACE_WIDTH;
			}
		}
		return x;
	}
	
	/**
	 * @deprecated Cannot draw a Font. Use render(Texture, int, int, String)
	 */
	@Override
	@Deprecated
	public void render(Texture renderTo, int x, int y)
	{
		
	}
	
	/**
	 * Renders a String of text, anchoring the left of the text to the provided position.
	 * 
	 * @param renderTo the object to draw this to
	 * @param pos the position to draw this at
	 * @param message the text to draw
	 */
	public void render(Texture renderTo, int x, int y, String message)
	{
		for (int i = 0; i < message.length(); i++)
		{
			int charIndex = CHARS.indexOf(message.charAt(i));
			if (charIndex >= 0)
			{
				Texture character = (Texture) getDrawableAsset(charIndex);
				renderTo.draw(character, x, y);
				
				x += character.getWidth(); // CHAR_WIDTH;
			}
			else
			{
				x += SPACE_WIDTH;
			}
		}
	}
	
	/**
	 * Renders a String of text, anchoring the left of the text to the provided position.
	 * 
	 * @param renderTo the object to draw this to
	 * @param pos the position to draw this at
	 * @param message the text to draw
	 */
	public void render(Texture renderTo, Vector2DInt pos, String message)
	{
		render(renderTo, pos.getX(), pos.getY(), message);
	}
	
	/**
	 * Renders a String of text, anchoring the middle of the text to the provided position.
	 * 
	 * @param renderTo the object to draw this to
	 * @param pos the position to draw this at
	 * @param message the text to draw
	 */
	public void renderCentered(Texture renderTo, int x, int y, String message)
	{
		render(renderTo, x - (getWidth(message) / 2), y, message);
	}
	
	/**
	 * Renders a String of text, anchoring the middle of the text to the provided position.
	 * 
	 * @param renderTo the object to draw this to
	 * @param pos the position to draw this at
	 * @param message the text to draw
	 */
	public void renderCentered(Texture renderTo, Vector2DInt pos, String message)
	{
		renderCentered(renderTo, pos.getX(), pos.getY(), message);
	}
	
	/**
	 * Renders a String of text, anchoring the right of the text to the provided position.
	 * 
	 * @param renderTo the object to draw this to
	 * @param pos the position to draw this at
	 * @param message the text to draw
	 */
	public void renderRight(Texture renderTo, int x, int y, String message)
	{
		render(renderTo, x - getWidth(message), y, message);
	}
	
	/**
	 * Renders a String of text, anchoring the right of the text to the provided position.
	 * 
	 * @param renderTo the object to draw this to
	 * @param pos the position to draw this at
	 * @param message the text to draw
	 */
	public void renderRight(Texture renderTo, Vector2DInt pos, String message)
	{
		renderRight(renderTo, pos.getX(), pos.getY(), message);
	}
}
