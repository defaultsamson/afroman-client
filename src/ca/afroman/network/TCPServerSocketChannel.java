package ca.afroman.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCPServerSocketChannel
{
	public static int acceptOp = SelectionKey.OP_ACCEPT;
	
	private ServerSocketChannel server;
	private Selector selector;
	
	public TCPServerSocketChannel(int port, boolean blocking, int timeout, Selector selector) throws IOException
	{
		this.selector = selector;
		
		server = ServerSocketChannel.open();
		server.configureBlocking(blocking);
		server.socket().bind(new InetSocketAddress(port));
		server.socket().setSoTimeout(timeout);
		server.register(selector, acceptOp);
	}
	
	public void bind(int port) throws IOException {
		server.socket().bind(new InetSocketAddress(port));
	}
	
	public TCPSocketChannel accept(boolean blocking) throws IOException {
		SocketChannel client = server.accept();
		TCPSocketChannel tcp = new TCPSocketChannel(selector, client, blocking);
		return tcp;
	}
	
	public void close() throws IOException {
		server.close();
	}
	
}
