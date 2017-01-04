package ca.afroman.packet.technical;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.server.ConsoleCommand;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.ListUtil;

public class PacketCommand extends BytePacket
{
	public PacketCommand(ConsoleCommand command, String[] parameters, IPConnection... connection)
	{
		super(PacketType.COMMAND, true, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		send.add(typeOrd());
		
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
		
		content = ListUtil.toByteArray(send);
	}
}
