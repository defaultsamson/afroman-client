package ca.afroman.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicTickThread;

public class PacketSender extends DynamicTickThread implements IServerClient
{
	private SocketManager manager;
	private List<BytePacket> sendingPackets;
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public PacketSender(SocketManager manager)
	{
		super(manager.getGame().getThreadGroup(), "Send", 1 / 5);
		
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
	
	@Override
	public boolean isServerSide()
	{
		return manager.isServerSide();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		synchronized (sendingPackets)
		{
			sendingPackets.clear();
		}
	}
	
	/**
	 * For client use
	 * 
	 * @param connection
	 * @param packetID
	 */
	public void removePacket(int packetID)
	{
		removePacket(manager.getServerConnection(), packetID);
	}
	
	/**
	 * For server use.
	 * 
	 * @param connection
	 * @param packetID
	 */
	public void removePacket(IPConnection connection, int packetID)
	{
		synchronized (sendingPackets)
		{
			if (isServerSide())
			{
				BytePacket toRemove = null;
				
				for (BytePacket pack : sendingPackets)
				{
					if (pack.getID() == packetID)
					{
						if (isServerSide() && pack.getConnections().contains(connection))
						{
							pack.getConnections().remove(connection);
						}
						
						toRemove = pack;
						break;
					}
				}
				
				if (toRemove != null && (!isServerSide() || toRemove.getConnections().isEmpty())) sendingPackets.remove(toRemove);
			}
		}
	}
	
	/**
	 * Sends a byte array of data to the server.
	 */
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
	 * Sends a packet to the server if it's the client, or to the desired connection if it's the server.
	 * 
	 * @param packet the packet
	 */
	public void sendPacket(BytePacket packet)
	{
		if (!isServerSide()) // Pend the server connection
		{
			packet.setConnections(ClientGame.instance().sockets().getServerConnection());
		}
		
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
	
	/**
	 * Sends a packet to the server.
	 * 
	 * @param packet the packet
	 */
	public void sendPacketToAllClients(BytePacket packet)
	{
		if (isServerSide()) // Pend all connected players
		{
			packet.getConnections().clear();
			
			for (ConnectedPlayer connection : manager.getConnectedPlayers())
			{
				if (connection instanceof IPConnectedPlayer) packet.getConnections().add(((IPConnectedPlayer) connection).getConnection());
			}
		}
		else
		{
			logger().log(ALogType.DEBUG, "Server is using the method sendPacketToAllClients() in PacketSender. The client should be using sendPacket(). There isn't a problem at the moment, but it is bad practice and could cause future problems.");
		}
		
		sendPacket(packet);
	}
	
	@Override
	public void tick()
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
