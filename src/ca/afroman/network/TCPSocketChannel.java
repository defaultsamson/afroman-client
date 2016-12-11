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
	private SocketChannel socketChannel;
	private int bufferSize;
	private ArrayList<byte[]> toSend;
	private ArrayList<byte[]> toReceive;
	private TCPSocket tcpSocket;
	
	public TCPSocketChannel(int bufferSize) throws IOException
	{
		selector = Selector.open();
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		this.bufferSize = bufferSize;
		toSend = new ArrayList<byte[]>();
		tcpSocket = new TCPSocket(socketChannel.socket());
	}
	
	public void keyCheck() throws IOException {
		selector.selectNow();
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		while (keyIterator.hasNext()) {
			SelectionKey key = keyIterator.next();
			
			if (key.isAcceptable()) { // ready to accept connection
				
			} else if (key.isConnectable()) { // finished or failed fininshing a connection
				
			} else if (key.isReadable()) { // ready to read
				toReceive.add(read(key, bufferSize));
			} else if (key.isWritable()) { // ready to write
				for (byte[] data : toSend) write(key, data, bufferSize);
			}
			keyIterator.remove();
		}
	}
	
	private byte[] read (SelectionKey key, int bufferSize) throws IOException {
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		int bytesRead = socket.read(buffer);
		int totalBytesRead = bytesRead;
		
		while (bytesRead > 0) {
			bytesRead =  socket.read(buffer);
			totalBytesRead += bytesRead;
		}
		
		if (bytesRead == -1) { // TODO: indicate disconnect
			socket.close();
			return null;
		}
		
		byte[] data = new byte[totalBytesRead];
		for (int i = 0; i < totalBytesRead; i++) {
			data[i] = buffer.get(i);
		}
		return data;
	}
	
	private void write (SelectionKey key, byte[] data, int bufferSize) throws IOException {
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.wrap(data);
		
		while (buffer.hasRemaining()) {
			socket.write(buffer);
		}
	}
	
	public void sendData (byte[] data) throws ClosedChannelException {
		toSend.add(data);
		socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	
	public void receiveData (byte[] destination) throws ClosedChannelException {
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

}
