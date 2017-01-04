package ca.afroman.packet.level;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketPlayerMoveServerClient extends BytePacket
{
	public PacketPlayerMoveServerClient(Role player, byte dXa, byte dYa, IPConnection... connection)
	{
		super(PacketType.PLAYER_MOVE, false, connection);
		
		content = new byte[] { typeOrd(), (byte) player.ordinal(), dXa, dYa };
	}
}
