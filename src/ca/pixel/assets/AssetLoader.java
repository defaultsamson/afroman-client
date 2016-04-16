package ca.pixel.assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class AssetLoader
{
	/**
	 * Loads an image from the system.
	 * 
	 * @param path the path to the image
	 * @return
	 */
	public static BufferedImage loadImageFrom(String path)
	{
		URL url = Class.class.getResource(path);
		BufferedImage image = null;
		
		try
		{
			image = ImageIO.read(url);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return image;
	}
}
