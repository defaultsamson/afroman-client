package ca.afroman.entity;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.level.ClientLevel;

public class TextureEntity extends ClientEntity
{
	Texture texture;
	
	public TextureEntity(int id, ClientLevel level, AssetType asset, double x, double y, double width, double height, Hitbox hitbox)
	{
		super(id, level, asset, x, y, width, height, hitbox);
		
		texture = Assets.getTexture(asset);
	}
	
	public TextureEntity(int id, ClientLevel level, AssetType asset, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		super(id, level, asset, x, y, width, height, hitboxes);
		
		texture = Assets.getTexture(asset);
	}
	
	public void render(Texture renderTo)
	{
		if (texture != null) renderTo.draw(texture, getLevel().worldToScreenX(getX()), getLevel().worldToScreenY(getY()));
		
		if (ClientGame.instance().isHitboxDebugging())
		{
			if (this.hasHitbox())
			{
				for (Hitbox box : this.hitboxInLevel())
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
