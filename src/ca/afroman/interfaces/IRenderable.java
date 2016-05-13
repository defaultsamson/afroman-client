package ca.afroman.interfaces;

import ca.afroman.assets.Texture;

public interface IRenderable
{
	/**
	 * Draws this object on the <i>renderTo</i> object.
	 * 
	 * @param renderTo the Texture to draw to
	 * @param x the x ordinate to draw at
	 * @param y the y ordinate to draw at
	 */
	public abstract void render(Texture renderTo, int x, int y);
}
