package ca.afroman.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;

public class NSocketManager extends DynamicThread
{
	protected static final int BUFFER_SIZE = 48;
	
	private ThreadGroup group;
	private Game game;
	
	private ServerSocketChannel server;
	private Selector selector;
	
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
	
	public void host(InetSocketAddress address) throws IOException {
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.bind(address);
		
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " hosting on: " + address);
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	protected void accept(SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		
		if (client != null) {
			client.configureBlocking(false);
			SocketAddress remoteAddress = client.getRemoteAddress();
			System.out.println("Accepted connection from: " + remoteAddress);
			client.register(selector, SelectionKey.OP_READ, null);
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
	
	public void sendPacket(BytePacket packet) {
		
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
