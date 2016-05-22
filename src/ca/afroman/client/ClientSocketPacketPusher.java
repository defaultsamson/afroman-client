package ca.afroman.client;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.packet.Packet;
import ca.afroman.thread.DynamicTickThread;

public class ClientSocketPacketPusher extends DynamicTickThread
{
	private List<Packet> sendingPackets; // The packets that are still trying to be sent.
	
	/**
	 * Constantly sends required packets to any client until they confirm that they've received them.
	 */
	public ClientSocketPacketPusher()
	{
		super(2);
		
		sendingPackets = new ArrayList<Packet>();
		
		this.setName("Client-SocketPusher");
	}
	
	@Override
	public void tick()
	{
		synchronized (this)
		{
			for (Packet pack : getPacketQueue())
			{
				ClientGame.instance().socket().sendPacket(pack);
			}
		}
	}
	
	@Override
	public void onPause()
	{
		
	}
	
	@Override
	public void onUnpause()
	{
		
	}
	
	@Override
	public void onStop()
	{
		sendingPackets.clear();
	}
	
	/**
	 * @param connection the connection that this was sending the packet to
	 * @param id the ID of the packet being sent
	 */
	public void removePacketFromQueue(int id)
	{
		Packet toRemove = null;
		
		List<Packet> sent = getPacketQueue();
		
		if (sent != null)
		{
			// Find the packet that the server is saying it recieved.
			for (Packet pack : sent)
			{
				if (pack.getID() == id)
				{
					toRemove = pack;
					break;
				}
			}
			
			// Remove that packet from the queue
			if (toRemove != null)
			{
				sent.remove(toRemove);
			}
		}
		else
		{
			System.out.println("[SERVER] [PUSHER] Cannot find the connection to remove the packet from.");
		}
	}
	
	public void addPacket(Packet packet)
	{
		if (!packet.mustSend()) return;
		
		List<Packet> packs = getPacketQueue();
		
		// Don't add it if it's just looping through and trying to add it again
		if (!packs.contains(packet))
		{
			packs.add(packet);
		}
	}
	
	public synchronized List<Packet> getPacketQueue()
	{
		return sendingPackets;
	}
}
