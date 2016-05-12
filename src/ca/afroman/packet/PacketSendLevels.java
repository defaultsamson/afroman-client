package ca.afroman.packet;

public class PacketSendLevels extends Packet
{
	/**
	 * Designed to be sent from...
	 * <p>
	 * the host's <b>client</b> to the <b>server</b> to requests that the server sends all the levels to the players.
	 * <p>
	 * the <b>server</b> to the <b>client</b> to inform that the server is going to be sending all the levels.
	 * <p>
	 */
	public PacketSendLevels()
	{
		super(PacketType.SEND_LEVELS);
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR).getBytes();
	}
}
