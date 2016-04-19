package ca.pixel.game.assets;

import ca.pixel.game.gfx.Font;
import ca.pixel.game.gfx.SpriteAnimation;
import ca.pixel.game.gfx.Texture;
import ca.pixel.game.gfx.TextureArray;

public class Assets
{
	public static final Texture sheet1 = Texture.fromResource("/spritesheet.png");
	public static final Texture fonts = Texture.fromResource("/fonts.png");
	
	public static final Font font_normal = new Font(fonts.getSubTexture(0, 0, 256, 32));
	
	// public static final Texture player = sheet1.getSubTexture(0, 0, 16, 16);
	public static final TextureArray player = new TextureArray(sheet1.getSubTexture(0, 0, 16 * 3, 16 * 4), 3, 4, 16, 16);
	
	public static final SpriteAnimation playerUp = new SpriteAnimation(true, 15, player.getTexture(9), player.getTexture(10), player.getTexture(11));
	public static final SpriteAnimation playerLeft = new SpriteAnimation(true, 15, player.getTexture(3), player.getTexture(4), player.getTexture(5));
	public static final SpriteAnimation playerRight = new SpriteAnimation(true, 15, player.getTexture(6), player.getTexture(7), player.getTexture(8));
	public static final SpriteAnimation playerDown = new SpriteAnimation(true, 15, player.getTexture(0), player.getTexture(1), player.getTexture(2));
	
	public static final SpriteAnimation playerIdleUp = new SpriteAnimation(true, 0, player.getTexture(10));
	public static final SpriteAnimation playerIdleLeft = new SpriteAnimation(true, 0, player.getTexture(4));
	public static final SpriteAnimation playerIdleRight = new SpriteAnimation(true, 0, player.getTexture(7));
	public static final SpriteAnimation playerIdleDown = new SpriteAnimation(true, 0, player.getTexture(1));
	
	// public static final Texture grass = sheet1.getSubTexture(48, 0, 16, 16);
	public static final TextureArray grass = new TextureArray(sheet1.getSubTexture(48, 0, 16 * 6, 16 * 1), 6, 1, 16, 16);
	public static final Texture stone = sheet1.getSubTexture(48, 16, 16, 16);
}
