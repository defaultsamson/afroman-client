package ca.afroman.packet;

public class PacketDisconnect extends Packet
{
	/**
	 * Designed to be sent from the <b>client</b> to the <b>server</b>.
	 * <p>
	 * Tells the server that a client is disconnecting.
	 */
	public PacketDisconnect()
	{
		super(PacketType.PLAYER_DISCONNECT, false);
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR).getBytes();
	}
}
