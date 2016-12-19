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
	public static int serverOp = SelectionKey.OP_ACCEPT;
	public static int writeOp = SelectionKey.OP_WRITE;
	public static int readOp = SelectionKey.OP_READ;
	
	private SocketChannel socket;
	private Selector selector;
	private TCPSocket tcp;
	
	public boolean isWriting;
	
	public byte[] read;
	public byte[] write;
	
	/**
	 * Create a TCPSocketChannel to handle the use of a single SocketChannel
	 * @param selector the selector to register to
	 * @param socket the socket to handle
	 * @param blocking whether the socket will perform blocking operations or not
	 * @throws IOException
	 */
	public TCPSocketChannel(Selector selector, SocketChannel socket, boolean blocking) throws IOException
	{
		this.socket = socket;
		this.selector = selector;
		socket.configureBlocking(blocking);
		socket.register(selector, defaultOps, this);
		tcp = new TCPSocket(socket.socket());
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
		socket.register(selector, defaultOps, this);
		return success;
	}
	
	public byte[] read(SelectionKey key) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(ClientGame.RECEIVE_PACKET_BUFFER_LIMIT);
		int bytesRead = socket.read(buffer);
		
		if (bytesRead == -1)
		{
			socket.close();
			((TCPSocketChannel) key.attachment()).read = null;
			return null;
		} else {
			buffer.flip();
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
		socket.register(selector, readOp, this);
		return read;
	}
	
	public SocketChannel getSocket()
	{
		return socket;
	}
	
	public TCPSocket getTCP()
	{
		return tcp;
	}
}
