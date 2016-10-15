package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.input.InputType;
import ca.afroman.level.LevelObjectType;
import ca.afroman.log.ALogType;
import ca.afroman.packet.PacketActivateTrigger;
import ca.afroman.resource.IDCounter;
import ca.afroman.server.ServerGame;

public class HitboxTrigger extends Event
{
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
	
	private List<TriggerType> triggerTypes;
	private InputType input;
	
	/** The Entity that was last touching this. Used for TriggerType.PLAYER_UNTOUCH */
	private Entity lastHit = null;
	
	public HitboxTrigger(boolean isServerSide, int id, double x, double y, double width, double height, List<TriggerType> triggerTypes, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		super(isServerSide, id, x, y, width, height, inTriggers, outTriggers);
		
		this.triggerTypes = (triggerTypes != null ? triggerTypes : new ArrayList<TriggerType>());
		input = new InputType();
	}
	
	public List<TriggerType> getTriggerTypes()
	{
		return triggerTypes;
	}
	
	@Override
	public void onTrigger(Entity triggerer)
	{
		// if (!isServerSide())
		// {
		// String message = "Out Triggers: ";
		//
		// for (int out : getOutTriggers())
		// {
		// message += out + ", ";
		// }
		//
		// if (outTriggers.isEmpty()) message += "(none)";
		//
		// ClientGame.instance().logger().log(ALogType.DEBUG, message);
		// }
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
					ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(getID(), level.getType(), player.getRole()));
				}
				
				if (playerUncollide && input.isReleasedFiltered())
				{
					if (lastHit != null)
					{
						trigger(lastHit);
						ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(getID(), level.getType(), ((PlayerEntity) lastHit).getRole()));
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
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(LevelObjectType.HITBOX_TRIGGER.toString());
		sb.append('(');
		sb.append(getHitbox().getX());
		sb.append(", ");
		sb.append(getHitbox().getY());
		sb.append(", ");
		sb.append(getHitbox().getWidth());
		sb.append(", ");
		sb.append(getHitbox().getHeight());
		sb.append(", {");
		
		// Saves trigger types
		for (int k = 0; k < getTriggerTypes().size(); k++)
		{
			sb.append(getTriggerTypes().get(k).toString());
			if (k != getTriggerTypes().size() - 1) sb.append(", ");
		}
		
		sb.append("}, {");
		
		// Saves in triggers
		for (int k = 0; k < inTriggers.size(); k++)
		{
			sb.append(inTriggers.get(k));
			if (k != inTriggers.size() - 1) sb.append(", ");
		}
		
		sb.append("}, {");
		
		// Saves out triggers
		for (int k = 0; k < outTriggers.size(); k++)
		{
			sb.append(outTriggers.get(k));
			if (k != outTriggers.size() - 1) sb.append(", ");
		}
		
		sb.append("})");
		
		return sb.toString();
	}
}
