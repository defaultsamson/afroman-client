package ca.afroman.inventory;

import java.util.HashMap;
import java.util.Stack;

import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.GroundItem;
import ca.afroman.packet.PacketItemPickup;
import ca.afroman.packet.PacketPlayerInteract;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.server.ServerGame;

public class Inventory extends ServerClientObject
{
	private PlayerEntity owner;
	private HashMap<ItemType, Stack<GroundItem>> items;
	
	public Inventory(PlayerEntity player)
	{
		super(player.isServerSide());
		
		owner = player;
		
		items = new HashMap<ItemType, Stack<GroundItem>>();
		for (ItemType i : ItemType.values())
		{
			items.put(i, new Stack<GroundItem>());
		}
	}
	
	/**
	 * Tries to add a ground item to the inventory.
	 * 
	 * @param item the item to add
	 * @return whether or not the ground item could be added or not.
	 */
	public boolean addItem(GroundItem item)
	{
		return addItem(item, false);
	}
	
	/**
	 * Tries to add a ground item to the inventory.
	 * 
	 * @param item the item to add
	 * @param serverForce if the server is forcing the client player to pick up the item
	 * @return whether or not the ground item could be added or not.
	 */
	public boolean addItem(GroundItem item, boolean serverForce)
	{
		Stack<GroundItem> list = items.get(item.getItemType());
		
		if (list.size() < item.getItemType().getMaxStackSize())
		{
			if (isServerSide())
			{
				ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketItemPickup(owner.getRole(), item.getLevel().getLevelType(), item.getID()));
				addItem(item, list);
			}
			else
			{
				// If the server has told the player to pick it up
				if (serverForce)
				{
					addItem(item, list);
				}
				// Request the server to pick up the item
				else
				{
					ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerInteract(owner.getPosition()));
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private void addItem(GroundItem item, Stack<GroundItem> list)
	{
		item.removeFromLevel(!isServerSide());
		list.push(item);
	}
	
	public GroundItem getItem(ItemType type)
	{
		Stack<GroundItem> list = items.get(type);
		
		if (!list.isEmpty())
		{
			return list.pop();
		}
		return null;
	}
}
