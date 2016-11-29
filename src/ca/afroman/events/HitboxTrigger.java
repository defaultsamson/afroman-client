package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.input.InputType;
import ca.afroman.log.ALogType;
import ca.afroman.packet.PacketActivateTrigger;
import ca.afroman.server.ServerGame;

public class HitboxTrigger extends Event
{
	private List<TriggerType> triggerTypes;
	private InputType input;
	
	/** The Entity that was last touching this. Used for TriggerType.PLAYER_UNTOUCH */
	private Entity lastHit = null;
	
	public HitboxTrigger(boolean isServerSide, double x, double y, double width, double height, List<TriggerType> triggerTypes, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		super(isServerSide, x, y, width, height, inTriggers, outTriggers);
		
		this.triggerTypes = (triggerTypes != null ? triggerTypes : new ArrayList<TriggerType>());
		input = new InputType();
	}
	
	public List<TriggerType> getTriggerTypes()
	{
		return triggerTypes;
	}
	
	public void setTriggerTypes(List<TriggerType> types)
	{
		triggerTypes = types;
	}
	
	@Override
	public void tick()
	{
		// Only activate the triggers if it's on the server side
		if (isServerSide())
		{
			boolean playerCollide = this.triggerTypes.contains(TriggerType.PLAYER_COLLIDE);
			boolean playerUncollide = this.triggerTypes.contains(TriggerType.PLAYER_UNCOLLIDE);
			
			if (playerCollide || playerUncollide)
			{
				PlayerEntity player = null;
				
				for (PlayerEntity p : level.getPlayers())
				{
					if (p.isColliding(hitbox))
					{
						player = p;
						break;
					}
				}
				
				input.setPressed(player != null);
				
				if (playerCollide && input.isPressedFiltered())
				{
					trigger(player);
					ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(getID(), level.getLevelType(), player.getRole()));
				}
				
				if (playerUncollide && input.isReleasedFiltered())
				{
					if (lastHit != null)
					{
						trigger(lastHit);
						ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(getID(), level.getLevelType(), ((PlayerEntity) lastHit).getRole()));
					}
					else
					{
						ServerGame.instance().logger().log(ALogType.WARNING, "The last hit player was unable to be found.");
					}
				}
				
				lastHit = player;
			}
		}
	}
}
