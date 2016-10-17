package ca.afroman.light;

import java.awt.Color;

public class ColourUtil
{
	public static final int ALPHA_COLOUR1 = ColourUtil.fromHex("0xFFFF00FF").getRGB();
	public static final int ALPHA_COLOUR2 = ColourUtil.fromHex("0xFF7F007F").getRGB();
	
	public static final int BUFFER_WASTE = ColourUtil.fromHex("0x00FF00FF").getRGB(); // A random colour that will never be used on the lightmap
	public static final Color AMBIENT_COLOUR = ColourUtil.fromHex("0xCC000000");
	public static final Color WHITE = ColourUtil.fromHex("0xFFFFFFFF");
	public static final Color TRANSPARENT = ColourUtil.fromHex("0x00000000");
	
	/**
	 * Assumed format of <b>hex</b> is <i>0xAARRGGBB</i>
	 * 
	 * @param hex the hexadecimal colour value
	 * @return a Colour object with the specifications from the hex value
	 */
	public static Color fromHex(String hex)
	{
		return new Color(Integer.valueOf(hex.substring(4, 6), 16), Integer.valueOf(hex.substring(6, 8), 16), Integer.valueOf(hex.substring(8, 10), 16), Integer.valueOf(hex.substring(2, 4), 16));
	}
}
