package ca.afroman.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.channels.ClosedChannelException;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicTickThread;

public class PacketSender extends DynamicTickThread
{
	private SocketManager manager;
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public PacketSender(boolean isServerSide, SocketManager manager)
	{
		super(isServerSide, manager.getGame().getThread().getThreadGroup(), "Send", 0);
		
		this.manager = manager;
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
				manager.socket().socket().send(packet);
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
		
		if (packet.mustSend())
		{
			for (IPConnection con : packet.getConnections())
			{
				if (con != null && con.getTCPSocketChannel() != null)
				{
					try
					{
						con.getTCPSocketChannel().sendData(packet.getData());
					}
					catch (ClosedChannelException e)
					{
						logger().log(ALogType.WARNING, "Channel closed before data could be sent", e);
						e.printStackTrace();
					}
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
	 * Sends a packet from the server.
	 * 
	 * @param packet the packet
	 */
	public void sendPacketToAllClients(BytePacket packet, IPConnection... exceptedConnections)
	{
		if (isServerSide()) // Pend all connected players
		{
			// ArrayList<IPConnection> cons = new ArrayList<IPConnection>();
			
			List<ConnectedPlayer> players = manager.getConnectedPlayers();
			
			IPConnection[] cons = new IPConnection[players.size()];
			
			for (int i = 0; i < cons.length; i++)
			{
				// just assume that they're all connected players to speed up process
				IPConnection con = ((IPConnectedPlayer) players.get(i)).getConnection();
				
				boolean isAllowed = true;
				for (IPConnection exc : exceptedConnections)
				{
					if (exc == con)
					{
						isAllowed = false;
						break;
					}
				}
				
				if (isAllowed) cons[i] = con;
			}
			
			packet.setConnections(cons);
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
