package ca.afroman.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.PortUnreachableException;

import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.interfaces.IPacketParser;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;

public class SocketReceive extends DynamicThread implements IServerClient
{
	private DatagramSocket socket;
	private boolean isServer;
	private IPacketParser game;
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public SocketReceive(DatagramSocket socket, boolean isServer, IPacketParser game)
	{
		super(game.getThreadGroup(), "Receive");
		
		this.socket = socket;
		this.isServer = isServer;
		this.game = game;
	}
	
	@Override
	public boolean isServerSide()
	{
		return isServer;
	}
	
	@Override
	public void onRun()
	{
		byte[] buffer = new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
		
		// Loads up the buffer with incoming data
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try
		{
			socket.receive(packet);
			
			BytePacket pack = new BytePacket(packet.getData(), new IPConnection(packet.getAddress(), packet.getPort()));
			if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + pack.getConnections().get(0).asReadable() + "] " + pack.getType());
			game.addPacketToParse(pack);
		}
		catch (PortUnreachableException e)
		{
			logger().log(ALogType.CRITICAL, "Port is unreachable", e);
			if (!isServerSide())
			{
				ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "PORT", "UNREACHABLE");
			}
		}
		catch (IOException e)
		{
			if (isRunning) logger().log(ALogType.CRITICAL, "I/O error while receiving", e);
		}
	}
}
