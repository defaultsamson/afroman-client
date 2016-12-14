package ca.afroman.network;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

import ca.afroman.client.ClientGame;

public class TCPSocketChannel
{
	public static int defaultOps = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
	public static int serverOps = SelectionKey.OP_ACCEPT;
	public static int writeOp = SelectionKey.OP_WRITE;
	public static int readOp = SelectionKey.OP_READ;
	
	private SocketChannel socket;
	private Selector selector;
	
	public boolean isWriting;
	
	public byte[] read;
	public byte[] write;
	
	public TCPSocketChannel(Selector selector, SocketChannel socket, boolean blocking) throws IOException
	{
		this.socket = socket;
		this.selector = selector;
		socket.configureBlocking(blocking);
		socket.register(selector, defaultOps);
	}
	
	public SelectionKey register(Selector selector, int operations) throws ClosedChannelException
	{
		this.selector = selector;
		return socket.register(selector, operations);
	}
	
	public boolean connect(SocketAddress remote, boolean blocking) throws IOException
	{
		socket = SocketChannel.open();
		socket.configureBlocking(blocking);
		boolean success = socket.connect(remote);
		while (!socket.finishConnect())
		{
			
		}
		socket.register(selector, defaultOps);
		return success;
	}
	
	public byte[] read(SelectionKey key) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(ClientGame.RECEIVE_PACKET_BUFFER_LIMIT);
		int bytesRead;
		
		if ((bytesRead = socket.read(buffer)) > 0)
		{
			buffer.flip();
		}
		if (bytesRead < 0)
		{
			socket.close();
			return new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
		}
		
		((TCPSocketChannel) key.attachment()).read = buffer.array();
		return buffer.array();
	}
	
	public void write(SelectionKey key) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer output = ByteBuffer.wrap(((TCPSocketChannel) key.attachment()).write);
		socket.write(output);
		((TCPSocketChannel) key.attachment()).isWriting = false;
	}
	
	public SelectionKey sendData(byte[] data) throws ClosedChannelException
	{
		isWriting = true;
		write = data;
		return socket.register(selector, writeOp, this);
	}
	
	public byte[] receiveData() throws ClosedChannelException, PortUnreachableException
	{
		return read;
	}
	
	public SocketChannel getSocket()
	{
		return socket;
	}
}
