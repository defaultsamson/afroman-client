package ca.afroman.game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.nio.channels.SocketChannel;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IncomingPacketWrapper;
import ca.afroman.network.TCPSocketChannel;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.IPUtil;

public class TCPReceiver extends DynamicThread
{
	private TCPSocketChannel socket;
	private SocketManager manager;
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public TCPReceiver(boolean isServerSide, SocketManager manager, TCPSocketChannel socket)
	{
		super(isServerSide, manager.getGame().getThread().getThreadGroup(), "Receive(" + IPUtil.asReadable(((SocketChannel) socket.getSocket()).socket().getInetAddress(), ((SocketChannel) socket.getSocket()).socket().getPort()) + ")");
		
		this.manager = manager;
		this.socket = socket;
	}
	
	public TCPSocketChannel getTCPSocketChannel()
	{
		return socket;
	}
	
	@Override
	public void onRun()
	{
		try
		{
			socket.keyCheck();
			
			byte[] buffer = new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
			
			if (!socket.isReading) {
				buffer = socket.receiveData();
			}
			
			if (buffer != null)
			{
				BytePacket pack = new BytePacket(buffer);
				InetAddress address = ((SocketChannel) socket.getSocket()).socket().getInetAddress();
				int port = ((SocketChannel) socket.getSocket()).socket().getPort();
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
	
	@Override
	public void stopThis()
	{
		super.stopThis();
		
		try
		{
			socket.getSocket().close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
