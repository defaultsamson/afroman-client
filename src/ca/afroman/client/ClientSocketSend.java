package ca.afroman.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicTickThread;

public class ClientSocketSend extends DynamicTickThread
{
	private ClientSocketManager manager;
	private List<BytePacket> sendingPackets; // The packets that are still trying to be sent.
	
	/**
	 * Constantly sends required packets to any client until they confirm that they've received them.
	 */
	public ClientSocketSend(ClientSocketManager manager)
	{
		super(ClientSocketManager.threadGroupInstance(), "Send", 1 / 2);
		
		this.manager = manager;
		
		sendingPackets = new ArrayList<BytePacket>();
	}
	
	public void addPacket(BytePacket packet)
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
	public void removePacket(int id)
	{
		BytePacket toRemove = null;
		
		List<BytePacket> sent = getPacketQueue();
		
		synchronized (sent)
		{
			// Find the packet that the server is saying it recieved.
			for (BytePacket pack : sent)
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
	}
	
	/**
	 * Sends a byte array of data to the server.
	 * 
	 * @param data the data
	 * @param con the server connection
	 * 
	 * @deprecated Still works to send raw data, but sendPacket() is preferred.
	 */
	@Deprecated
	private void sendData(byte[] data, InetAddress address, int port)
	{
		if (address != null)
		{
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			
			try
			{
				manager.socket().send(packet);
			}
			catch (IOException e)
			{
				logger().log(ALogType.CRITICAL, "I/O error while sending packet", e);
			}
		}
		else
		{
			logger().log(ALogType.WARNING, "Server address is null");
		}
	}
	
	/**
	 * Sends a packet to the server.
	 * 
	 * @param packet the packet
	 */
	public void sendPacket(BytePacket packet)
	{
		packet.setConnections(manager.getServerConnection().getConnection());
		addPacket(packet);
		
		for (IPConnection con : packet.getConnections())
		{
			if (con != null && con.getIPAddress() != null)
			{
				if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + con.asReadable() + "] " + packet.getType());
				sendData(packet.getData(), con.getIPAddress(), con.getPort());
			}
		}
	}
	
	@Override
	public void tick()
	{
		List<BytePacket> packs = getPacketQueue();
		
		synchronized (packs)
		{
			// For each packet queued to send to the connection
			for (BytePacket pack : packs)
			{
				ClientGame.instance().sockets().sender().sendPacket(pack);
			}
		}
	}
}
