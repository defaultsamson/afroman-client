package ca.afroman.packet.level;

import ca.afroman.game.Role;
import ca.afroman.level.api.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLevel extends BytePacket
{
	public PacketSetPlayerLevel(Role role, LevelType levelType, IPConnection... connection)
	{
		super(PacketType.SET_PLAYER_LEVEL, true, connection);
		
		byte[] level = ByteUtil.shortAsBytes((byte) levelType.ordinal());
		
		content = new byte[] { typeOrd(), (byte) role.ordinal(), level[0], level[1] };
	}
}
