package ca.afroman.assets;

import ca.afroman.resource.Vector2DInt;

public class Font extends DrawableAssetArray
{
	/** The width of a single character */
	public static final int CHAR_WIDTH = 6;
	private static final int ROWS = 4;
	private static final int COLUMNS = 32;
	
	private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&" + "abcdefghijklmnopqrstuvwxyz'()*+," + "0123456789:;<=>?[\\]^_`{|}~-./" + "";
	
	/**
	 * Loads a writable font from a texture.
	 * <p>
	 * <b>WARNING: </b> The fontTexture must be <i>EXACTLY</i> (256 x 32) pixels, else the loading scheme will not work.
	 * 
	 * @param fontTexture the texture to load the font from
	 */
	public Font(AssetType type, Texture fontTexture)
	{
		super(type, fontTexture.toTextureArray(COLUMNS, ROWS));
	}
	
	/**
	 * @deprecated Cannot draw a Font. Use render(Texture, Vector2DInt, String)
	 */
	@Override
	@Deprecated
	public void render(Texture renderTo, Vector2DInt pos)
	{
		
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
		Vector2DInt pos1 = pos.clone();
		
		for (int i = 0; i < message.length(); i++)
		{
			int charIndex = CHARS.indexOf(message.charAt(i));
			if (charIndex >= 0)
			{
				renderTo.draw((Texture) getDrawableAsset(charIndex), pos1);
			}
			pos1.add(CHAR_WIDTH, 0);
		}
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
		render(renderTo, pos.clone().add(-((message.length() * CHAR_WIDTH) / 2), 0), message);
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
		render(renderTo, pos.clone().add(-(message.length() * CHAR_WIDTH), 0), message);
	}
}
