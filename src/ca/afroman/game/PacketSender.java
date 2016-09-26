package ca.afroman.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

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
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public PacketSender(SocketManager manager)
	{
		super(manager.getGame().getThreadGroup(), "Send", 0);
		
		this.manager = manager;
	}
	
	@Override
	public boolean isServerSide()
	{
		return manager.isServerSide();
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
		
		// addPacket(packet);
		
		if (packet.mustSend()) // TODO Use TCP
		{
			for (IPConnection con : packet.getConnections())
			{
				if (con != null && con.getTCPSocket() != null)
				{
					con.getTCPSocket().sendData(packet.getData());
				}
			}
		}
		else // Use UDP
		{
			for (IPConnection con : packet.getConnections())
			{
				if (con != null && con.getIPAddress() != null)
				{
					if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + con.asReadable() + "] " + packet.getType());
					sendData(packet.getData(), con.getIPAddress(), con.getPort());
				}
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
		
	}
}
