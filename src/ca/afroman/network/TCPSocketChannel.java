package ca.afroman.network;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class TCPSocketChannel
{
	private Selector selector;
	private ServerSocketChannel serverChannel;
	private SocketChannel socketChannel;
	private int bufferSize;
	private ArrayList<byte[]> toSend;
	private ArrayList<byte[]> toReceive;
	private TCPSocket tcpSocket;
	private boolean isServerSide;
	
	public TCPSocketChannel(Socket socket, boolean isServerSide) throws IOException
	{
		selector = Selector.open();
		bufferSize = 48;
		toSend = new ArrayList<byte[]>();
		toReceive = new ArrayList<byte[]>();
		this.isServerSide = isServerSide;
	}
	
	public void host(SocketAddress remote) throws IOException
	{
		if (isServerSide)
		{
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(remote);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
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
	
	public void accept(SelectionKey key) throws IOException
	{
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		if (client != null)
		{
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
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
			{ // ready to accept connection
			
			}
			else if (key.isConnectable())
			{ // finished or failed finishing a connection
			
			}
			else if (key.isReadable())
			{ // ready to read
				toReceive.add(read(key, bufferSize));
			}
			else if (key.isWritable())
			{ // ready to write
				for (byte[] data : toSend)
					write(key, data, bufferSize);
				toSend.clear();
			}
			keyIterator.remove();
		}
	}
	
	private byte[] read(SelectionKey key, int bufferSize) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
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
	
	private void write(SelectionKey key, byte[] data, int bufferSize) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer = ByteBuffer.wrap(data);
		
		while (buffer.hasRemaining())
		{
			socket.write(buffer);
		}
	}
	
	public void sendData(byte[] data) throws ClosedChannelException
	{
		toSend.add(data);
		socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	
	public void receiveData(byte[] destination) throws ClosedChannelException
	{
		socketChannel.register(selector, SelectionKey.OP_READ);
	}
	
	public SocketChannel getSocket()
	{
		return socketChannel;
	}
	
	public boolean isBlocking()
	{
		return socketChannel.isBlocking();
	}
	
}
