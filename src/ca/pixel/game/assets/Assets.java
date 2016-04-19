package ca.pixel.game.assets;

import ca.pixel.game.gfx.Font;
import ca.pixel.game.gfx.Texture;
import ca.pixel.game.gfx.TextureArray;

public class Assets
{
	public static final Texture sheet1 = Texture.fromResource("/spritesheet.png");
	public static final Texture fonts = Texture.fromResource("/fonts.png");
	
	public static final Font font_normal = new Font(fonts.getSubTexture(0, 0, 256, 32));
	
	// public static final Texture player = sheet1.getSubTexture(0, 0, 16, 16);
	public static final TextureArray player = new TextureArray(sheet1.getSubTexture(0, 0, 16 * 3, 16 * 4), 3, 4, 16, 16);
	
	// public static final Texture grass = sheet1.getSubTexture(48, 0, 16, 16);
	public static final TextureArray grass = new TextureArray(sheet1.getSubTexture(48, 0, 16 * 6, 16 * 1), 6, 1, 16, 16);
	public static final Texture stone = sheet1.getSubTexture(48, 16, 16, 16);
}
