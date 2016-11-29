package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.game.Game;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.packet.PacketActivateTrigger;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class TPTrigger extends Event
{
	private LevelType tpTo;
	private double tpX;
	private double tpY;
	
	public TPTrigger(boolean isServerSide, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		super(isServerSide, x, y, width, height, inTriggers, outTriggers);
		
		tpTo = null;
		tpX = 0;
		tpY = 0;
	}
	
	public LevelType getLevelTypeToTPTo()
	{
		return tpTo;
	}
	
	public double getTPX()
	{
		return tpX;
	}
	
	public double getTPY()
	{
		return tpY;
	}
	
	@Override
	public void onTrigger(Entity triggerer)
	{
		if (triggerer instanceof PlayerEntity)
		{
			PlayerEntity p = (PlayerEntity) triggerer;
			
			if (tpTo != null)
			{
				Level level = Game.instance(isServerSide()).getLevel(tpTo);
				p.addToLevel(level);
			}
			
			// Don't update position for client because it is getting updated from this event
			p.setPosition(false, new Vector2DDouble(tpX, tpY));
		}
	}
	
	/**
	 * If the level to TP to is null, it will teleport the player within the current level.
	 * 
	 * @param tpTo
	 */
	public void setLevelToTPTo(LevelType tpTo)
	{
		this.tpTo = tpTo;
	}
	
	public void setLocationToTPTo(double tpX, double tpY)
	{
		this.tpX = tpX;
		this.tpY = tpY;
	}
	
	@Override
	public void tick()
	{
		// Only activate the triggers if it's on the server side
		if (isServerSide())
		{
			List<PlayerEntity> conPlayers = new ArrayList<PlayerEntity>();
			
			for (PlayerEntity p : level.getPlayers())
			{
				if (p.isColliding(hitbox))
				{
					conPlayers.add(p);
				}
			}
			
			for (PlayerEntity p : conPlayers)
			{
				trigger(p);
				ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(getID(), level.getLevelType(), p.getRole()));
			}
		}
	}
}
