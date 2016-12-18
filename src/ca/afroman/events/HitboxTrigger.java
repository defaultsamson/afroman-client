package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.input.InputType;
import ca.afroman.log.ALogType;
import ca.afroman.packet.PacketActivateTrigger;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class HitboxTrigger extends Event
{
	private List<TriggerType> triggerTypes;
	protected InputType input;
	
	/** The Entity that was last touching this. Used for TriggerType.PLAYER_UNTOUCH */
	private Entity lastHit = null;
	
	public HitboxTrigger(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, List<Integer> inTriggers, List<Integer> outTriggers, List<TriggerType> triggerTypes, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, position, inTriggers, outTriggers, hitboxes);
		
		initTriggerTypes(triggerTypes);
	}
	
	public List<TriggerType> getTriggerTypes()
	{
		return triggerTypes;
	}
	
	private void initTriggerTypes(List<TriggerType> triggerTypes)
	{
		this.triggerTypes = (triggerTypes != null ? triggerTypes : new ArrayList<TriggerType>());
		input = new InputType();
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
				PlayerEntity player = updateInput();
				
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
	
	@Override
	public void tryInteract(PlayerEntity triggerer)
	{
		if (isServerSide())
		{
			if (triggerTypes.contains(TriggerType.PLAYER_INTERACT))
			{
				if (triggerer.isColliding(getHitbox()))
				{
					trigger(triggerer);
					ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(getID(), level.getLevelType(), triggerer.getRole()));
				}
			}
		}
	}
	
	/**
	 * Updates this's input object based on collisions
	 * 
	 * @return the player that is colliding with this
	 */
	protected PlayerEntity updateInput()
	{
		PlayerEntity player = null;
		
		for (PlayerEntity p : level.getPlayers())
		{
			if (this.isColliding(p))
			{
				player = p;
				break;
			}
		}
		
		input.setPressed(player != null);
		
		return player;
	}
}
