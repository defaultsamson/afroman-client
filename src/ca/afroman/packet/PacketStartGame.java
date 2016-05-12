package ca.afroman.packet;

public class PacketStartGame extends Packet
{
	/**
	 * Designed to be sent from...
	 * <p>
	 * the host's <b>client</b> to the <b>server</b> to requests that the server starts the game.
	 * <p>
	 * the <b>server</b> to the <b>client</b> to inform that the server has started.
	 * <p>
	 */
	public PacketStartGame()
	{
		super(PacketType.START_SERVER);
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR).getBytes();
	}
}
