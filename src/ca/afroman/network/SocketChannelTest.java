package ca.afroman.network;

import java.io.IOException;
import java.net.InetSocketAddress;

import ca.afroman.client.ClientGame;

public class SocketChannelTest
{
	
	public static void main(String[] args) throws IOException
	{
		byte[] out = new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
		TCPSocketChannel server = new TCPSocketChannel(true);
		server.bind(new InetSocketAddress(6767));
		TCPSocketChannel client = new TCPSocketChannel(false);
		client.connect(new InetSocketAddress("localhost", 6767));
		client.sendData(new byte[] {1,1,1,1});
		client.keyCheck();
		server.receiveData(out);
		System.out.print("[");
		for (int i = 0; i < out.length; i++) {
			System.out.print(out[i] + (i == out.length - 1 ? "]" : ","));
		}
		System.out.println();
	}
	
}
