package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.network.IPConnection;
import ca.afroman.server.ConsoleCommand;
import ca.afroman.util.ByteUtil;

public class PacketCommand extends BytePacket
{
	private byte[] toSend;
	
	public PacketCommand(ConsoleCommand command, String[] parameters, IPConnection... connection)
	{
		super(PacketType.COMMAND, true, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		for (byte e : ByteUtil.intAsBytes(command.ordinal()))
		{
			send.add(e);
		}
		
		send.add((byte) (parameters.length - 1));
		
		// Don't do the parameter at index 0 because that is the ConsoleCommand enum
		for (int i = 1; i < parameters.length; i++)
		{
			for (byte b : parameters[i].getBytes())
			{
				send.add(b);
			}
			
			send.add(Byte.MAX_VALUE);
			send.add(Byte.MIN_VALUE);
			send.add(Byte.MAX_VALUE);
			send.add(Byte.MIN_VALUE);
		}
		
		toSend = new byte[send.size()];
		
		int i = 0;
		for (byte e : send)
		{
			toSend[i] = e;
			i++;
		}
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
