package ca.afroman.network;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import ca.afroman.client.ClientGame;

public class TCPSocketChannel
{
	private Selector selector;
	private ServerSocketChannel serverChannel;
	private SocketChannel socketChannel;
	private ArrayList<byte[]> toSend;
	private ArrayList<byte[]> toReceive;
	private TCPSocket tcpSocket;
	private boolean isServerSide;
	private boolean reading;
	private boolean writing;
	private boolean connecting;
	private boolean accepting;
	
	public TCPSocketChannel(boolean isServerSide) throws IOException
	{
		selector = Selector.open();
		toSend = new ArrayList<byte[]>();
		toReceive = new ArrayList<byte[]>();
		this.isServerSide = isServerSide;
	}
	
	public void bind(SocketAddress remote) throws IOException
	{
		if (isServerSide)
		{
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(remote);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			accepting = true;
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
		selector.selectNow();
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		while (keyIterator.hasNext())
		{
			SelectionKey key = keyIterator.next();
			
			if (key.isAcceptable())
			{
				accept(key);
			}
			else if (key.isConnectable())
			{
				handshake(key);
			}
			else if (key.isReadable())
			{
				toReceive.add(read(key));
				reading = false;
			}
			else if (key.isWritable())
			{
				for (byte[] data : toSend)
					write(key, data);
				toSend.clear();
				writing = false;
			}
			keyIterator.remove();
		}
	}
	
	private void accept(SelectionKey key) throws IOException
	{
		SocketChannel client = serverChannel.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
	}
	
	private void handshake(SelectionKey key)
	{
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		
	}
	
	private byte[] read(SelectionKey key) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(ClientGame.RECEIVE_PACKET_BUFFER_LIMIT);
		int bytesRead = socket.read(buffer);
		int totalBytesRead = bytesRead;
		
		while (bytesRead > 0)
		{
			bytesRead = socket.read(buffer);
			totalBytesRead += bytesRead;
		}
		
		if (bytesRead == -1)
		{ // TODO: indicate disconnect
			socket.close();
			return null;
		}
		
		byte[] data = new byte[totalBytesRead];
		for (int i = 0; i < totalBytesRead; i++)
		{
			data[i] = buffer.get(i);
		}
		return data;
	}
	
	private void write(SelectionKey key, byte[] data) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		socket.write(buffer);
	}
	
	public void sendData(byte[] data) throws ClosedChannelException
	{
		toSend.add(data);
		writing = true;
		if (isServerSide)
		{
			serverChannel.register(selector, SelectionKey.OP_WRITE);
		}
		else
		{
			socketChannel.register(selector, SelectionKey.OP_WRITE);
		}
	}
	
	public void receiveData(byte[] destination) throws ClosedChannelException
	{
		reading = true;
		if (isServerSide)
		{
			// serverChannel.register(selector, SelectionKey.OP_READ);
		}
		else
		{
			socketChannel.register(selector, SelectionKey.OP_READ);
		}
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
	
}
