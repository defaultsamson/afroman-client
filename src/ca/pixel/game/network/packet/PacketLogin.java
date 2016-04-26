package ca.pixel.game.network.packet;

import ca.pixel.game.network.GameClient;
import ca.pixel.game.network.GameServer;

public class PacketLogin extends Packet 
{
	private String pass;
	
	public PacketLogin(String password)
	{
		super(PacketType.LOGIN);
		this.pass = password;
	}
	
	public PacketLogin(byte[] data)
	{
		super(PacketType.LOGIN);
		this.pass = readContent(data);
	}

	@Override
	public void writeData(GameClient client)
	{
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server)
	{
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + this.pass).getBytes();
	}
	
	public String getPassword()
	{
		return pass;
	}
}
