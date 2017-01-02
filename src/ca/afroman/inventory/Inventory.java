package ca.afroman.inventory;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Item;
import ca.afroman.log.ALogType;
import ca.afroman.packet.PacketItemDropClientServer;
import ca.afroman.packet.PacketItemDropServerClient;
import ca.afroman.packet.PacketItemPickup;
import ca.afroman.packet.PacketPlayerInteract;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class Inventory extends ServerClientObject
{
	private PlayerEntity owner;
	private HashMap<ItemType, Stack<Item>> items;
	private Item equippedItem;
	
	public Inventory(PlayerEntity player)
	{
		super(player.isServerSide());
		
		owner = player;
		
		items = new HashMap<ItemType, Stack<Item>>();
		for (ItemType i : ItemType.values())
		{
			items.put(i, new Stack<Item>());
		}
	}
	
	/**
	 * Tries to add a ground item to the inventory.
	 * 
	 * @param item the item to add
	 * @return whether or not the ground item could be added or not.
	 */
	public boolean addItem(Item item)
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
	public boolean addItem(Item item, boolean serverForce)
	{
		Stack<Item> list = items.get(item.getItemType());
		
		if (list.size() < item.getItemType().getMaxStackSize())
		{
			if (isServerSide())
			{
				ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketItemPickup(owner.getRole(), item.getID()));
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
	
	private void addItem(Item item, Stack<Item> list)
	{
		if (isEmpty())
		{
			setEquippedItem(item);
		}
		
		item.removeFromLevel(!isServerSide());
		list.push(item);
	}
	
	public void dropItem()
	{
		if (!isServerSide())
		{
			if (getEquippedItem() != null)
			{
				removeItem(getEquippedItem().getItemType());
			}
		}
		else
		{
			ServerGame.instance().logger().log(ALogType.WARNING, "Should not be invoking Inventory.dropItem() from the server");
		}
	}
	
	public Item getEquippedItem()
	{
		return equippedItem;
	}
	
	/**
	 * Equips the next item in the inventory.
	 */
	public void gotoNextItem()
	{
		if (!isEmpty())
		{
			ItemType start = getEquippedItem().getItemType();
			ItemType test = start;
			
			// While it's not looping
			while ((test = test.getNext()) != start)
			{
				Stack<Item> stack = items.get(test);
				if (!stack.isEmpty())
				{
					setEquippedItem(stack.get(0));
					break;
				}
			}
		}
	}
	
	public void gotoPrevItem()
	{
		if (!isEmpty())
		{
			ItemType start = getEquippedItem().getItemType();
			ItemType test = start;
			
			// While it's not looping
			while ((test = test.getLast()) != start)
			{
				Stack<Item> stack = items.get(test);
				if (!stack.isEmpty())
				{
					setEquippedItem(stack.get(0));
					break;
				}
			}
		}
	}
	
	public boolean isEmpty()
	{
		// If any of the stacks in the inventory are occupied
		for (Entry<ItemType, Stack<Item>> e : items.entrySet())
		{
			if (!e.getValue().isEmpty())
			{
				return false;
			}
		}
		
		return true;
	}
	
	private void removeItem(ItemType type)
	{
		removeItem(type, null, false);
	}
	
	/**
	 * Equips the next item in the inventory.
	 */
	public void removeItem(ItemType type, Vector2DDouble pos, boolean serverForce)
	{
		Stack<Item> list = items.get(type);
		
		if (isServerSide())
		{
			// Sets the drop position to the player's feet
			pos = owner.getPosition().clone().add(4, 9);
			if (removeItem(list, pos))
			{
				ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketItemDropServerClient(owner.getRole(), type, pos));
			}
		}
		else
		{
			// If the server has told the player to pick it up
			if (serverForce)
			{
				removeItem(list, pos);
			}
			// Request the server to pick up the item
			else
			{
				ClientGame.instance().sockets().sender().sendPacket(new PacketItemDropClientServer(type, owner.getPosition()));
			}
		}
		
	}
	
	private boolean removeItem(Stack<Item> list, Vector2DDouble pos)
	{
		Item droppedItem = list.pop();
		
		if (droppedItem != null)
		{
			droppedItem.addToLevel(owner.getLevel());
			droppedItem.setPosition(pos.getX(), pos.getY());
			
			if (!isServerSide())
			{
				// If All the things are empty, no equipped item
				if (isEmpty())
				{
					setEquippedItem(null);
				}
				// Else if only the current item list is empty,
				else if (list.isEmpty())
				{
					gotoNextItem();
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setEquippedItem(Item item)
	{
		equippedItem = item;
	}
}
