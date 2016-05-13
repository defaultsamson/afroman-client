package ca.afroman.assets;

public class Font extends AssetArray
{
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
	
	public void renderRight(Texture renderTo, int x, int y, String message)
	{
		render(renderTo, x - (message.length() * 6), y, message);
	}
	
	public void renderCentered(Texture renderTo, int x, int y, String message)
	{
		render(renderTo, x - ((message.length() * 6) / 2), y, message);
	}
	
	public void render(Texture renderTo, int x, int y, String message)
	{
		for (int i = 0; i < message.length(); i++)
		{
			int charIndex = chars.indexOf(message.charAt(i));
			if (charIndex >= 0)
			{
				renderTo.draw((Texture) getAsset(charIndex), x + (i * 6), y);
			}
		}
	}
}
