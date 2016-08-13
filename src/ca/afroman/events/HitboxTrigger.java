package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.input.InputType;
import ca.afroman.level.Level;
import ca.afroman.packet.PacketActivateTrigger;
import ca.afroman.server.ServerGame;
import ca.afroman.util.IDCounter;

public class HitboxTrigger extends InputType implements IEvent, IServerClient
{
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
	
	private boolean isServerSide;
	
	private List<TriggerType> triggerTypes;
	private List<Integer> inTriggers;
	private List<Integer> outTriggers;
	private Hitbox hitbox;
	
	public HitboxTrigger(boolean isServerSide, int id, double x, double y, double width, double height, List<TriggerType> triggerTypes, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this(isServerSide, id, new Hitbox(id, x, y, width, height), triggerTypes, inTriggers, outTriggers);
	}
	
	public HitboxTrigger(boolean isServerSide, int id, Hitbox box, List<TriggerType> triggerTypes, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.isServerSide = isServerSide;
		hitbox = box;
		this.triggerTypes = (triggerTypes != null ? triggerTypes : new ArrayList<TriggerType>());
		this.inTriggers = (inTriggers != null ? inTriggers : new ArrayList<Integer>());
		this.outTriggers = (outTriggers != null ? outTriggers : new ArrayList<Integer>());
	}
	
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	/**
	 * Removes this scripted event from its current level and puts it in another level.
	 * 
	 * @param level the new level.
	 */
	@Override
	public void addToLevel(Level newLevel)
	{
		if (hitbox.getLevel() == newLevel) return;
		
		if (hitbox.getLevel() != null)
		{
			hitbox.getLevel().getScriptedEvents().remove(this);
		}
		
		// Sets the new level
		hitbox.setLevel(newLevel);
		
		if (hitbox.getLevel() != null)
		{
			hitbox.getLevel().getScriptedEvents().add(this);
		}
	}
	
	@Override
	public List<Integer> getInTriggers()
	{
		return inTriggers;
	}
	
	public void setInTriggers(List<Integer> trigs)
	{
		inTriggers = trigs;
	}
	
	@Override
	public List<Integer> getOutTriggers()
	{
		return outTriggers;
	}
	
	public void setOutTriggers(List<Integer> trigs)
	{
		outTriggers = trigs;
	}
	
	public List<TriggerType> getTriggerTypes()
	{
		return triggerTypes;
	}
	
	public void setTriggerTypes(List<TriggerType> types)
	{
		triggerTypes = types;
	}
	
	/** The Entity that was last touching this. Used for TriggerType.PLAYER_UNTOUCH */
	private Entity lastHit = null;
	
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
				ServerPlayerEntity player = null;
				
				for (Entity p : this.hitbox.getLevel().getPlayers())
				{
					if (p.isColliding(this.getHitbox()))
					{
						player = (ServerPlayerEntity) p;
						break;
					}
				}
				
				this.setPressed(player != null);
				
				if (playerCollide && this.isPressedFiltered())
				{
					trigger(player);
					ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(this.getID(), this.getLevel().getType(), player.getRole()));
				}
				
				if (playerUncollide && this.isReleasedFiltered())
				{
					trigger(player);
					ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketActivateTrigger(this.getID(), this.getLevel().getType(), ((ServerPlayerEntity) lastHit).getRole()));
				}
				
				lastHit = player;
			}
		}
	}
	
	@Override
	public Level getLevel()
	{
		return hitbox.getLevel();
	}
	
	@Override
	public void trigger(Entity triggerer)
	{
		onTrigger(triggerer);
		
		for (int out : getOutTriggers())
		{
			// TODO chain for all levels
			getLevel().chainScriptedEvents(triggerer, out);
		}
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
		// if (getOutTriggers().isEmpty()) message += "(none)";
		//
		// ClientGame.instance().logger().log(ALogType.DEBUG, message);
		// }
	}
	
	@Override
	public int getID()
	{
		return hitbox.getID();
	}
	
	@Override
	public double getX()
	{
		return hitbox.getX();
	}
	
	@Override
	public double getY()
	{
		return hitbox.getY();
	}
	
	@Override
	public double getWidth()
	{
		return hitbox.getWidth();
	}
	
	@Override
	public double getHeight()
	{
		return hitbox.getHeight();
	}
	
	@Override
	public boolean isServerSide()
	{
		return isServerSide;
	}
	
	@Override
	public void removeFromLevel()
	{
		addToLevel(null);
	}
}
