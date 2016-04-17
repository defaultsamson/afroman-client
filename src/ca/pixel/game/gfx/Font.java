package ca.pixel.game.gfx;

public class Font
{
	private static final int ROWS = 4;
	private static final int COLUMNS = 32;
	
	private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&" + "abcdefghijklmnopqrstuvwxyz'()*+," + "0123456789:;<=>?[\\]^_`{|}~-./" + "";
	
	/** Holds the textures for each character. */
	private Texture[] textures = new Texture[32 * 4];
	
	/**
	 * Loads a writable font from a texture.
	 * <p>
	 * <b>WARNING: </b> The fontTexture must be <i>EXACTLY</i> (256 x 32), else the loading scheme will not work.
	 * 
	 * @param fontTexture
	 */
	public Font(Texture fontTexture)
	{
		// If the provided texture does not meet the required dimension specifications
		if (fontTexture.getWidth() != 256 || fontTexture.getHeight() != 32) return;
		
		for (int y = 0; y < ROWS; y++)
		{
			for (int x = 0; x < COLUMNS; x++)
			{
				textures[(y * COLUMNS) + x] = fontTexture.getSubTexture(x * 8, y * 8, 8, 8);
			}
		}
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
				renderTo.draw(textures[charIndex], x + (i * 6), y);
				// renderTo.draw(, , );
			}
		}
	}
}
