package ca.pixel.game.assets;

import ca.pixel.game.gfx.Font;
import ca.pixel.game.gfx.Texture;

public class Assets
{
	public static final Texture sheet1 = Texture.fromResource("/spritesheet.png");
	public static final Texture fonts = Texture.fromResource("/fonts.png");
	
	public static final Font font_normal = new Font(fonts.getSubTexture(0, 0, 256, 32));
	
}
