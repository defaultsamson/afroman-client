package ca.afroman.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.Packet;
import ca.afroman.thread.DynamicTickThread;

public class ServerSocketSend extends DynamicTickThread
{
	private HashMap<IPConnection, List<Packet>> sendingPackets; // The packets that are still trying to be sent.
	private ServerSocketManager manager;
	
	/**
	 * Constantly sends required packets to any client until they confirm that they've received them.
	 */
	public ServerSocketSend(ServerSocketManager manager)
	{
		super(ServerGame.instance().getThreadGroup(), "Send", 2);
		
		this.manager = manager;
		
		sendingPackets = new HashMap<IPConnection, List<Packet>>();
	}
	
	@Override
	public void tick()
	{
		HashMap<IPConnection, List<Packet>> packs = getPacketQueue();
		
		synchronized (packs)
		{
			// For each connection
			for (Entry<IPConnection, List<Packet>> entry : packs.entrySet())
			{
				// For each packet queued to send to the connection
				for (Packet pack : entry.getValue())
				{
					sendPacket(pack, entry.getKey());
				}
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
	
	public List<Packet> getPacketsSendingTo(IPConnection connection)
	{
		HashMap<IPConnection, List<Packet>> packs = getPacketQueue();
		
		synchronized (packs)
		{
			for (Entry<IPConnection, List<Packet>> entry : packs.entrySet())
			{
				if (entry.getKey().equals(connection)) return entry.getValue();
			}
		}
		return null;
	}
	
	public void addConnection(IPConnection connection)
	{
		HashMap<IPConnection, List<Packet>> packs = getPacketQueue();
		
		synchronized (packs)
		{
			packs.put(connection, new ArrayList<Packet>());
		}
	}
	
	public void removeConnection(IPConnection connection)
	{
		HashMap<IPConnection, List<Packet>> packs = getPacketQueue();
		
		synchronized (packs)
		{
			packs.remove(connection);
		}
	}
	
	/**
	 * @param connection the connection that this was sending the packet to
	 * @param id the ID of the packet being sent
	 */
	public void removePacketFromQueue(IPConnection connection, int id)
	{
		Packet toRemove = null;
		
		List<Packet> sent = getPacketsSendingTo(connection);
		
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
	
	public void addPacketSendingTo(IPConnection connection, Packet packet)
	{
		if (!packet.mustSend()) return;
		
		List<Packet> packs = getPacketsSendingTo(connection);
		
		// Don't add it if it's just looping through and trying to add it again
		if (!packs.contains(packet))
		{
			packs.add(packet);
		}
	}
	
	public HashMap<IPConnection, List<Packet>> getPacketQueue()
	{
		return sendingPackets;
	}
	
	/**
	 * Sends data to a Client.
	 * 
	 * @param packet the packet to send
	 * @param connection the Connection of the Client to send to
	 */
	public void sendPacket(Packet packet, IPConnection connection)
	{
		addPacketSendingTo(connection, packet);
		sendData(packet.getData(), connection.getIPAddress(), connection.getPort());
	}
	
	/**
	 * Sends data to a Client.
	 * 
	 * @param data the data to send
	 * @param ipAddress the Client's IP address
	 * @param port the Client's port
	 * 
	 * @deprecated Still works to send raw data, but sendPacket() is preferred.
	 */
	@Deprecated
	private void sendData(byte[] data, InetAddress ipAddress, int port)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		
		if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + ipAddress.getHostAddress() + ":" + port + "] " + new String(data));
		
		try
		{
			manager.socket().send(packet);
		}
		catch (IOException e)
		{
			logger().log(ALogType.CRITICAL, "I/O error while sending packet.", e);
		}
	}
	
	/**
	 * Sends a packet to all the connected clients.
	 * 
	 * @param packet the packet to send
	 */
	public void sendPacketToAllClients(Packet packet)
	{
		for (IPConnectedPlayer connection : manager.getConnectedPlayers())
		{
			sendPacket(packet, connection.getConnection());
		}
	}
}
