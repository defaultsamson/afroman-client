package ca.afroman.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.Set;

import ca.afroman.client.ClientGame;

public class TCPSocketChannel
{
	private Selector selector;
	private ServerSocketChannel serverChannel;
	private SocketChannel socketChannel;
	private byte[] toSend;
	private byte[] toReceive;
	private TCPSocket tcpSocket;
	public boolean isServerSide;
	public boolean isReading;
	public boolean isWriting;
	public boolean isAccepting;
	
	public TCPSocketChannel(boolean isServerSide) throws IOException
	{
		selector = Selector.open();
		this.isServerSide = isServerSide;
	}
	
	public TCPSocketChannel(SocketChannel socket) throws IOException
	{
		selector = Selector.open();
		socketChannel = socket;
		socketChannel.configureBlocking(false);
		isServerSide = false;
	}
	
	public void bind(int port) throws IOException {
		bind(new InetSocketAddress(port));
	}
	
	public void bind(SocketAddress remote) throws IOException
	{
		if (isServerSide)
		{
			serverChannel = ServerSocketChannel.open();
			serverChannel.socket().bind(remote);
			serverChannel.configureBlocking(false);
			
			int ops = serverChannel.validOps();
			SelectionKey selectKey = serverChannel.register(selector, ops, null);
			isAccepting = true;
		}
	}
	
	public void connect(SocketAddress remote) throws IOException
	{
		if (!isServerSide)
		{
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.connect(remote);
			while (!socketChannel.finishConnect())
			{
				
			}
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	
	public void keyCheck() throws IOException
	{
		int readyChannels = selector.selectNow();
		
		if (readyChannels == 0) return;
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		while (keyIterator.hasNext())
		{
			SelectionKey key = keyIterator.next();
			
			if (key.isAcceptable())
			{
				//accept(key);
			}
			else if (key.isConnectable())
			{
				//handshake(key);
			}
			else if (key.isReadable())
			{
				toReceive = (read(key));
				isReading = false;
			}
			else if (key.isWritable())
			{
				write(key, toSend);
				isWriting = false;
			}
			keyIterator.remove();
		}
	}
	
	private void accept(SelectionKey key) throws IOException
	{
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		client.configureBlocking(false);
		
		int ops = client.validOps();
		client.register(selector, ops);
	}
	
	/**
	 * Blocking version of accepting a client connection, required to obtain the client socket
	 * @return the client socket
	 * @throws IOException
	 */
	public SocketChannel accept() throws IOException
	{
		SocketChannel client = serverChannel.accept();
		client.configureBlocking(false);
		
		int ops = client.validOps();
		client.register(selector, ops);
		return client;
	}
	
	private void handshake(SelectionKey key)
	{
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
	}
	
	private byte[] read(SelectionKey key) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(ClientGame.RECEIVE_PACKET_BUFFER_LIMIT);
		int bytesRead;
		
		if ((bytesRead = socket.read(buffer)) > 0) {
			buffer.flip();
		}
		if (bytesRead < 0) {
			socket.close();
			return new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
		}
		
		byte[] data = buffer.array();
		return data;
	}
	
	private byte[] write(SelectionKey key, byte[] data) throws IOException
	{
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer output = ByteBuffer.wrap(data);
		client.write(output);
		data = null;
		return output.array();
	}
	
	public void sendData(byte[] data) throws ClosedChannelException
	{
		toSend = data;
		isWriting = true;
		if (isServerSide)
		{
			serverChannel.register(selector, SelectionKey.OP_WRITE);
		}
		else
		{
			socketChannel.register(selector, SelectionKey.OP_WRITE);
		}
	}
	
	public byte[] receiveData() throws ClosedChannelException
	{
		return toReceive;
	}
	
	public AbstractSelectableChannel getSocket()
	{
		if (isServerSide)
		{
			return serverChannel;
		}
		else
		{
			return socketChannel;
		}
		
	}
	
	public boolean isBlocking()
	{
		if (isServerSide)
		{
			return serverChannel.isBlocking();
		}
		else
		{
			return socketChannel.isBlocking();
		}
	}
	
	public void close() throws IOException
	{
		if (serverChannel.isOpen()) serverChannel.close();
		if (socketChannel.isOpen()) socketChannel.close();
	}
	
	public boolean isConnected() {
		if (!isServerSide)
		{
			return socketChannel.isConnected();
		} else {
			return serverChannel.isRegistered();
		}
	}
	
}
