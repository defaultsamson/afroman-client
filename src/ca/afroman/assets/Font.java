package ca.afroman.assets;

import ca.afroman.resource.Vector2DInt;

public class Font extends AssetArray
{
	public static final int CHAR_WIDTH = 6;
	private static final int ROWS = 4;
	private static final int COLUMNS = 32;
	
	private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&" + "abcdefghijklmnopqrstuvwxyz'()*+," + "0123456789:;<=>?[\\]^_`{|}~-./" + "";
	
	/**
	 * Loads a writable font from a texture.
	 * <p>
	 * <b>WARNING: </b> The fontTexture must be <i>EXACTLY</i> (256 x 32), else the loading scheme will not work.
	 * 
	 * @param fontTexture
	 */
	public Font(AssetType type, Texture fontTexture)
	{
		super(type, fontTexture.toTextureArray(COLUMNS, ROWS));
	}
	
	public void render(Texture renderTo, Vector2DInt pos, String message)
	{
		Vector2DInt pos1 = pos.clone();
		
		for (int i = 0; i < message.length(); i++)
		{
			int charIndex = chars.indexOf(message.charAt(i));
			if (charIndex >= 0)
			{
				renderTo.draw((Texture) getAsset(charIndex), pos1);
			}
			pos1.add(CHAR_WIDTH, 0);
		}
	}
	
	public void renderCentered(Texture renderTo, Vector2DInt pos, String message)
	{
		render(renderTo, pos.clone().add(-((message.length() * CHAR_WIDTH) / 2), 0), message);
	}
	
	public void renderRight(Texture renderTo, Vector2DInt pos, String message)
	{
		render(renderTo, pos.clone().add(-(message.length() * CHAR_WIDTH), 0), message);
	}
}
