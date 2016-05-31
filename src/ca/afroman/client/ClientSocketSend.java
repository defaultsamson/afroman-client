package ca.afroman.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.log.ALogType;
import ca.afroman.packet.Packet;
import ca.afroman.server.ServerSocket;
import ca.afroman.thread.DynamicTickThread;

public class ClientSocketSend extends DynamicTickThread
{
	private ClientSocketManager manager;
	private List<Packet> sendingPackets; // The packets that are still trying to be sent.
	
	/**
	 * Constantly sends required packets to any client until they confirm that they've received them.
	 */
	public ClientSocketSend(ClientSocketManager manager)
	{
		super(manager.getThreadGroup(), "Send", 2);
		
		this.manager = manager;
		
		sendingPackets = new ArrayList<Packet>();
	}
	
	@Override
	public void tick()
	{
		List<Packet> packs = getPacketQueue();
		
		synchronized (packs)
		{
			// For each packet queued to send to the connection
			for (Packet pack : packs)
			{
				ClientGame.instance().sockets().sender().sendPacket(pack);
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
		
		synchronized (sent)
		{
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
				logger().log(ALogType.WARNING, "Cannot find the connection to remove the packet from.");
			}
		}
	}
	
	public void addPacketToQueue(Packet packet)
	{
		if (!packet.mustSend()) return;
		
		List<Packet> packs = getPacketQueue();
		
		// Don't add it if it's just looping through and trying to add it again
		if (!packs.contains(packet))
		{
			packs.add(packet);
		}
	}
	
	/**
	 * Sends a packet to the server.
	 * 
	 * @param packet the packet
	 */
	public void sendPacket(Packet packet)
	{
		addPacketToQueue(packet);
		sendData(packet.getData());
	}
	
	/**
	 * Sends a byte array of data to the server.
	 * 
	 * @param data the data
	 * 
	 * @deprecated Still works to send raw data, but sendPacket() is preferred.
	 */
	@Deprecated
	private void sendData(byte[] data)
	{
		InetAddress address = manager.getConnectedPlayer().getConnection().getIPAddress();
		
		if (address != null)
		{
			DatagramPacket packet = new DatagramPacket(data, data.length, address, ServerSocket.PORT);
			
			if (ClientSocketManager.TRACE_PACKETS) logger().log(ALogType.DEBUG, "[" + address + ":" + ServerSocket.PORT + "] " + new String(data));
			
			try
			{
				manager.socket().send(packet);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			logger().log(ALogType.WARNING, "Server address is null");
		}
	}
	
	public List<Packet> getPacketQueue()
	{
		return sendingPackets;
	}
}
