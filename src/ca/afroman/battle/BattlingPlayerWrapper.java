package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.game.Role;
import ca.afroman.interfaces.ITickable;
import ca.afroman.inventory.Inventory;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.resource.Vector2DInt;

public class BattlingPlayerWrapper extends BattlingEntityWrapper
{
	private FlickeringLight light;
	private Texture shadow;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	
	private Vector2DInt fightPos;
	
	private Inventory inv;
	private Role role;
	
	public BattlingPlayerWrapper(PlayerEntity fighting)
	{
		super(fighting);
		inv = fighting.getInventory();
		role = fighting.getRole();
		
		this.fightPos = role == Role.PLAYER1 ? new Vector2DInt(183, 54) : new Vector2DInt(194, 79);
		
		asset = idleAsset = role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_ONE) : Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_TWO);
		shadow = Assets.getTexture(AssetType.BATTLE_SHADOW);
		light = new FlickeringLight(true, fightPos.toVector2DDouble(), 60, 70, 3);
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		shadow.render(renderTo, fightPos.getX() - 1, fightPos.getY() + 25);
		asset.render(renderTo, fightPos);// fightPos);
		light.renderCentered(lightmap);
		light.renderCentered(lightmap);
	}
	
	@Override
	public void setIsThisTurn(boolean isThisTurn)
	{
		super.setIsThisTurn(isThisTurn);
		
		if (isServerSide())
		{
			// TODO
			// ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetBattleTurn(Role?));
		}
	}
	
	@Override
	public void tick()
	{
		if (isServerSide())
		{
			// TODO controls and shite
		}
		else if (role == ClientGame.instance().getRole())
		{
			// TODO controls and shite
			// Jump
			if (ClientGame.instance().input().up.isPressed())
			{
				
			}
			// Duck
			if (ClientGame.instance().input().down.isPressed())
			{
				
			}
		}
		
		if (asset instanceof ITickable)
		{
			// Ticks the IBattleables DrawableAsset
			((ITickable) asset).tick();
		}
		
		light.setPosition(fightPos.getX() + 5, fightPos.getY() + 3);
		light.tick();
	}
}
