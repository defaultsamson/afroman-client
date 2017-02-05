package ca.afroman.packet.level;

import ca.afroman.game.Role;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLevel extends BytePacket
{
	public PacketSetPlayerLevel(Role role, Level level, IPConnection... connection)
	{
		super(PacketType.SET_PLAYER_LEVEL, true, connection);
		
		LevelType levelType = level == null ? LevelType.NULL : level.getLevelType();
		
		byte[] levelByte = ByteUtil.shortAsBytes((byte) levelType.ordinal());
		
		content = new byte[] { typeOrd(), (byte) role.ordinal(), levelByte[0], levelByte[1] };
	}
}
