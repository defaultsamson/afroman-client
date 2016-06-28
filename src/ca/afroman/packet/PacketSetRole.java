package ca.afroman.packet;

import ca.afroman.client.Role;
import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketSetRole extends BytePacket
{
	private byte[] toSend;
	
	public PacketSetRole(short playerID, Role newRole, IPConnection... connection)
	{
		super(PacketType.SETROLE, true, connection);
		
		byte[] id = ByteUtil.shortAsBytes(playerID);
		toSend = new byte[] { id[0], id[1], (byte) newRole.ordinal() };
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
