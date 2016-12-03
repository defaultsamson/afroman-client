package ca.afroman.packet;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;

public class PacketPlayerMove extends BytePacket
{
	private byte[] toSend;
	
	public PacketPlayerMove(Role player, byte dXa, byte dYa, IPConnection... connection)
	{
		super(PacketType.PLAYER_MOVE, false, connection);
		
		toSend = new byte[] { (byte) player.ordinal(), dXa, dYa };
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
