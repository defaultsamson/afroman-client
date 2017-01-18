package ca.afroman.packet.battle;

import ca.afroman.battle.BattlePosition;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketSelectEntityClientServer extends BytePacket
{
	public PacketSelectEntityClientServer(BattlePosition pos, IPConnection... connection)
	{
		super(PacketType.BATTLE_SELECT_ENTITY, true, connection);
		
		content = new byte[]{typeOrd(), (byte) pos.ordinal()};
	}
}
