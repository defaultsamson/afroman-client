package ca.afroman.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicTickThread;

public class ServerSocketSend extends DynamicTickThread
{
	private List<BytePacket> sendingPackets; // The packets that are still trying to be sent.
	private ServerSocketManager manager;
	
	/**
	 * Constantly sends required packets to any client until they confirm that they've received them.
	 */
	public ServerSocketSend(ServerSocketManager manager)
	{
		super(ServerGame.instance().getThreadGroup(), "Send", 1 / 5);
		
		this.manager = manager;
		
		sendingPackets = new ArrayList<BytePacket>();
	}
	
	private void addPacket(BytePacket packet)
	{
		if (!packet.mustSend()) return;
		
		synchronized (sendingPackets)
		{
			// Don't add it if it's just looping through and trying to add it again
			if (!sendingPackets.contains(packet))
			{
				sendingPackets.add(packet);
			}
		}
	}
	
	public List<BytePacket> getPacketQueue()
	{
		return sendingPackets;
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		sendingPackets.clear();
	}
	
	/**
	 * @param connection the connection that this was sending the packet to
	 * @param id the ID of the packet being sent
	 */
	public void removePacket(IPConnection connection, int id)
	{
		BytePacket toRemove = null;
		
		synchronized (sendingPackets)
		{
			// Find the packet that the connection is saying it received
			for (BytePacket pack : sendingPackets)
			{
				if (pack.getID() == id)
				{
					if (pack.getConnections().contains(connection))
					{
						pack.getConnections().remove(connection);
					}
					
					toRemove = pack;
					break;
				}
			}
			
			// Remove that packet from the queue
			if (toRemove != null && toRemove.getConnections().isEmpty())
			{
				sendingPackets.remove(toRemove);
			}
		}
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
		
		try
		{
			manager.socket().send(packet);
		}
		catch (IOException e)
		{
			logger().log(ALogType.CRITICAL, "I/O error while sending packet", e);
		}
	}
	
	/**
	 * Sends data to a Client.
	 * 
	 * @param packet the packet to send
	 * @param connection the Connection of the Client to send to
	 */
	public void sendPacket(BytePacket packet)
	{
		addPacket(packet);
		
		for (IPConnection con : packet.getConnections())
		{
			if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + con.asReadable() + "] " + packet.getType());
			sendData(packet.getData(), con.getIPAddress(), con.getPort());
		}
	}
	
	/**
	 * Sends a packet to all the connected clients.
	 * 
	 * @param packet the packet to send
	 */
	public void sendPacketToAllClients(BytePacket packet)
	{
		packet.getConnections().clear();
		
		for (IPConnectedPlayer connection : manager.getConnectedPlayers())
		{
			packet.getConnections().add(connection.getConnection());
		}
		
		sendPacket(packet);
	}
	
	@Override
	public void tick()
	{
		// TODO Don't try to force other packets if it's still pushing out crazy amounts of level packets to everyone
		if (!ServerGame.instance().isSendingLevels())
		{
			synchronized (sendingPackets)
			{
				// For each packet queued to send to the connection
				for (BytePacket pack : sendingPackets)
				{
					sendPacket(pack);
				}
			}
		}
	}
}
