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
	public static int writeOp = SelectionKey.OP_WRITE;
	public static int readOp = SelectionKey.OP_READ;
	
	private SocketChannel socket;
	private Selector selector;
	private TCPSocket tcp;
	private SelectionKey readKey;
	private SelectionKey writeKey;
	
	public boolean isWriting;
	
	public byte[] read = new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
	public byte[] write;
	
	/**
	 * Create a TCPSocketChannel to handle the use of a single SocketChannel
	 * 
	 * @param selector the selector to register to
	 * @param socket the socket to handle
	 * @param blocking whether the socket will perform blocking operations or not
	 * @throws IOException
	 */
	public TCPSocketChannel(Selector selector, SocketChannel socket, boolean blocking) throws IOException
	{
		this.socket = socket;
		this.selector = selector;
		this.socket.configureBlocking(blocking);
		readKey = this.socket.register(selector, readOp, this);
		tcp = new TCPSocket(this.socket.socket());
	}
	
	public SelectionKey register(Selector selector, int operations) throws ClosedChannelException
	{
		this.selector = selector;
		readKey = socket.register(selector, operations, this);
		return readKey;
	}
	
	public boolean connect(SocketAddress remote, boolean blocking) throws IOException
	{
		socket = SocketChannel.open();
		socket.configureBlocking(blocking);
		boolean success = socket.connect(remote);
		/*
		 * while (!socket.finishConnect())
		 * {
		 * }
		 */
		readKey = socket.register(selector, readOp, this);
		return success;
	}
	
	public static byte[] read(SelectionKey key) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(ClientGame.RECEIVE_PACKET_BUFFER_LIMIT);
		int bytesRead = socket.read(buffer);
		
		if (bytesRead == -1)
		{
			socket.close();
			((TCPSocketChannel) key.attachment()).read = null;
			return null;
		}
		else
		{
			buffer.flip();
		}
		
		byte[] receive = buffer.array();
		
		System.out.print("Read: [");
		for (int i = 0; i < receive.length; i ++) {
			System.out.print(receive[i] + " ");
		}
		System.out.println("]");
		
		((TCPSocketChannel) key.attachment()).read = receive;
		return buffer.array();
	}
	
	public static void write(SelectionKey key) throws IOException
	{
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer output = ByteBuffer.wrap(((TCPSocketChannel) key.attachment()).write);
		socket.write(output);
		((TCPSocketChannel) key.attachment()).isWriting = false;
		
		byte[] send = output.array();
		System.out.print("Write: [");
		for (int i = 0; i < send.length; i ++) {
			System.out.print(send[i] + " ");
		}
		System.out.println("]");
	}
	
	public void sendData(byte[] data) throws ClosedChannelException
	{
		isWriting = true;
		write = data;
		writeKey = socket.register(selector, writeOp, this);
	}
	
	public byte[] receiveData() throws ClosedChannelException, PortUnreachableException
	{
		byte[] out = null;
		
		if (read != null && read[0] != 0)
		{
			out = read.clone();
			System.out.print("Get: [");
			for (int i = 0; i < out.length; i ++) {
				System.out.print(out[i] + " ");
			}
			System.out.println("]");
		}
		else
		{
			out = null;
		}
		
		read = new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
		return out;
		
	}
	
	public SocketChannel getSocket()
	{
		return socket;
	}
	
	public TCPSocket getTCP()
	{
		return tcp;
	}
	
	public void close() throws IOException
	{
		writeKey.cancel();
		readKey.cancel();
		socket.close();
	}
}
