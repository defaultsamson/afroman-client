package ca.afroman.entity;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ca.afroman.ClientGame;
import ca.afroman.assets.Texture;
import ca.afroman.level.ClientLevel;
import ca.afroman.asset.AssetType;

public class TextureEntity extends ClientEntity
{
	public TextureEntity(ClientLevel level, AssetType asset, double x, double y, double width, double height, Rectangle2D.Double hitbox)
	{
		super(level, asset, x, y, width, height, hitbox);
	}
	
	public TextureEntity(ClientLevel level, AssetType asset, double x, double y, double width, double height, List<Rectangle2D.Double> hitboxes)
	{
		super(level, asset, x, y, width, height, hitboxes);
	}
	
	public TextureEntity(ClientLevel level, AssetType asset, double x, double y, double width, double height, Rectangle2D.Double... hitboxes)
	{
		super(level, asset, x, y, width, height, hitboxes);
	}
	
	public void render(Texture renderTo)
	{
		if (ClientGame.instance().isHitboxDebugging())
		{
			if (this.hasHitbox())
			{
				for (Rectangle2D.Double box : this.hitboxInLevel())
				{
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 0.3F));
					renderTo.getGraphics().fillRect(getLevel().worldToScreenX(box.x), getLevel().worldToScreenY(box.y), (int) box.width - 1, (int) box.height - 1);
					renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
					renderTo.getGraphics().drawRect(getLevel().worldToScreenX(box.x), getLevel().worldToScreenY(box.y), (int) box.width - 1, (int) box.height - 1);
				}
			}
		}
	}
}
