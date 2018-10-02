package ca.afroman.game;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.log.ALogType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.network.TCPSocket;
import ca.afroman.option.Options;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.technical.PacketServerClientStartTCP;
import ca.afroman.server.ServerGame;
import ca.afroman.thread.DynamicThread;

public class NSocketManager extends DynamicThread
{
	protected static final int BUFFER_SIZE = 48;
	protected static final int traffOps = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
	protected static final int hostOps = SelectionKey.OP_ACCEPT;
	protected static final int connectOps = SelectionKey.OP_CONNECT;
	
	/**
	 * Returns a usable port. If the provided one is eligible then it will return it, otherwise it will return the default port.
	 * 
	 * @param port
	 * @return
	 */
	public static int validatedPort(int port)
	{
		return (!(port < 0 || port > 0xFFFF)) ? port : Game.DEFAULT_PORT;
	}
	
	/**
	 * Returns a usable port. If the provided one is eligible then it will return it, otherwise it will return the default port.
	 * 
	 * @param port
	 * @return
	 */
	public static int validatedPort(String port)
	{
		if (port.length() > 0)
		{
			try
			{
				return validatedPort(Integer.parseInt(port));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		
		return Game.DEFAULT_PORT;
	}
	
	private ThreadGroup group;
	private Game game;
	
	private ServerSocketChannel server;
	private Selector selector;
	private DatagramSocket datagram;
	private IPConnection serverConnection;
	
	private boolean isStopping;
	
	public NSocketManager(boolean isServerSide, Game game, ThreadGroup group, String name) throws IOException
	{
		super(isServerSide, group, name);
		selector = Selector.open();
		this.game = game;
		this.group = group;
		this.isStopping = false;
	}
	
	@Override
	public void onRun()
	{
		while(!isStopping) {
			try
			{
				keyCheck();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void keyCheck() throws IOException {
		selector.select();
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

		while (keyIterator.hasNext()) {
			SelectionKey key = keyIterator.next();

			if (key.isAcceptable()) {
				accept(key);
			} else if (key.isConnectable()) {
				establish(key);
			} else if (key.isWritable()) {
				write(key);
			} else if (key.isReadable()) {
				read(key);
			}
			keyIterator.remove();
		}
	}
	
	public boolean setServerConnection(String address, int port)
	{
		port = validatedPort(port);
		
		serverConnection.setPort(port);
		
		if (address == null)
		{
			serverConnection.setIPAddress(null);
			return false;
		}
		
		InetAddress ip = null;
		
		try
		{
			ip = InetAddress.getByName(address);
		}
		catch (UnknownHostException e)
		{
			game.logger().log(ALogType.CRITICAL, "Couldn't resolve hostname", e);
			
			if (!isServerSide())
			{
				ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), -1, "UNKNOWN", "HOST");
			}
			return false;
		}
		
		serverConnection.setIPAddress(ip);
		
		if (isServerSide()) {
			Options.instance().serverPort = "" + port;
			
			try
			{
				server = ServerSocketChannel.open();
				server.configureBlocking(false);
				server.bind(new InetSocketAddress(ip, port));
				
				server.register(selector, hostOps);
			}
			catch (IOException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create server ServerSocket", e);
			}
			
			try
			{
				datagram = new DatagramSocket(serverConnection.getPort());
			}
			catch (SocketException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create server DatagramSocket", e);
				
				return false;
			}
		}
		else
		{
			Options.instance().clientPort = "" + port;
			
			try
			{
				datagram = new DatagramSocket();
				datagram.connect(serverConnection.getIPAddress(), serverConnection.getPort());
			}
			catch (SocketException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create client DatagramSocket", e);
				return false;
			}
		}
		
		return true;
	}
	
	protected void accept(SelectionKey key) throws IOException
	{
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		
		SocketChannel client = server.accept();
		
		if (client != null)
		{
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ, null);
		}
	}
	
	public void addConnection(IPConnection connection, String username)
	{
		if (isServerSide())
		{
			// Gives player a default role based on what critical roles are still required
			Role role = (getPlayerConnection(Role.PLAYER1) == null ? Role.PLAYER1 : (getPlayerConnection(Role.PLAYER2) == null ? Role.PLAYER2 : Role.SPECTATOR));
			
			short id = (short) ConnectedPlayer.getIDCounter().getNext();
			
			IPConnectedPlayer newConnection = new IPConnectedPlayer(connection, id, role, username);
			playerList.add(newConnection);
			
			try
			{
				sender().sendPacket(new PacketServerClientStartTCP(newConnection.getConnection()));
				Socket clientTCP = welcomeSocket().accept();
				TCPSocket tcp = new TCPSocket(clientTCP, isServerSide());
				newConnection.getConnection().setTCPSocket(tcp);
				
				synchronized (tcpSockets)
				{
					TCPReceiver rec = new TCPReceiver(isServerSide(), this, tcp);
					tcpSockets.add(rec);
					rec.startThis();
				}
			}
			catch (IOException e)
			{
				ServerGame.instance().logger().log(ALogType.WARNING, "Failed to accept connection from the welcome socket", e);
			}
		}
		else
		{
			ClientGame.instance().logger().log(ALogType.WARNING, "Client is trying to use addConnection() method in SocketManager");
		}
	}
	
	protected void establish(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		int waiter = 0;
		
		while(!channel.finishConnect()) {
			if (waiter > 10) {
				System.out.println(".");
				waiter = 0;
			} else {
				waiter++;
			}
		}
		
		System.out.println("Connected");
		channel.register(selector, SelectionKey.OP_READ, null);
	}
	
	protected void send(byte[] data) throws IOException {
		for (SelectionKey key: selector.keys()) {
			if ((key.interestOps() & SelectionKey.OP_READ) != 0)
			write(key.channel().register(selector, SelectionKey.OP_WRITE, data));
		}
	}
	
	public void sendPacket(BytePacket packet, IPConnection... exceptedConnections) {
		if (!isServerSide()) {
			packet.setConnections(ClientGame.instance().sockets().getServerConnection());
		}
		
		if (packet.mustSend()) {
			
		}
	}
	
	protected void write(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		buffer.clear();
		buffer.put((byte[]) key.attachment());
		buffer.flip();
		
		while (buffer.hasRemaining()) {
			client.write(buffer);
		}
		System.out.println("Sent: " + key.attachment() + " to: " + client.getRemoteAddress());
		client.register(selector, SelectionKey.OP_READ, null);
	}
	
	protected byte[] read(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		int bytesRead = client.read(buffer);
		
		while (bytesRead > 0) {
			bytesRead = client.read(buffer);
		}
		
		SocketAddress remoteAddress = client.getRemoteAddress();		
		if (bytesRead == -1) {
			System.out.println("Connection Closed by: " + remoteAddress);
			client.close();
			return null;
		}
		
		System.out.println("Received \"" + new String(buffer.array()) + "\" from: " + remoteAddress);
		return buffer.array();
	}
	
}
