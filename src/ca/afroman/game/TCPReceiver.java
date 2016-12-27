package ca.afroman.game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PortUnreachableException;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IncomingPacketWrapper;
import ca.afroman.network.TCPSocket;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.IPUtil;

public class TCPReceiver extends DynamicThread
{
	private TCPSocket socket;
	private SocketManager manager;
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public TCPReceiver(boolean isServerSide, SocketManager manager, TCPSocket socket)
	{
		super(isServerSide, manager.getGame().getThread().getThreadGroup(), "Receive(" + IPUtil.asReadable(socket.getSocket().getInetAddress(), socket.getSocket().getPort()) + ")");
		
		this.manager = manager;
		this.socket = socket;
	}
	
	public TCPSocket getTCPSocket()
	{
		return socket;
	}
	
	@Override
	public void onRun()
	{
		try
		{
			byte[] buffer = socket.receive();
			
			if (buffer != null)
			{
				BytePacket pack = new BytePacket(buffer);
				InetAddress address = socket.getSocket().getInetAddress();
				int port = socket.getSocket().getPort();
				if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + IPUtil.asReadable(address, port) + "] " + pack.getType());
				
				manager.getGame().addPacketToParse(new IncomingPacketWrapper(pack, address, port));
			}
		}
		catch (PortUnreachableException e)
		{
			logger().log(ALogType.CRITICAL, "Port is unreachable: " + manager.socket().getPort(), e);
			if (!isServerSide())
			{
				ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), -1, "PORT", "UNREACHABLE");
			}
		}
		catch (IOException e)
		{
			if (isRunning) logger().log(ALogType.CRITICAL, "I/O error while receiving", e);
		}
	}
}
