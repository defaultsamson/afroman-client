package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.game.Role;
import ca.afroman.interfaces.ITickable;
import ca.afroman.inventory.Inventory;
import ca.afroman.resource.Vector2DInt;

public class BattlingPlayerWrapper extends BattlingEntityWrapper
{
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
		
		this.fightPos = role == Role.PLAYER1 ? new Vector2DInt(197, 51) : new Vector2DInt(162, 71);
		
		asset = idleAsset = role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_ONE) : Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_TWO);
	}
	
	@Override
	public void render(Texture renderTo)
	{
		asset.render(renderTo, fightPos);// fightPos);
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
		if (asset instanceof ITickable)
		{
			// Ticks the IBattleables DrawableAsset
			((ITickable) asset).tick();
		}
	}
}
