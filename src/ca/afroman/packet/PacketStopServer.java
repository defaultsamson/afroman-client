package ca.afroman.packet;

public class PacketStopServer extends Packet
{
	/**
	 * Designed to be sent from...
	 * <p>
	 * the host's <b>client</b> to the <b>server</b> to requests that the server shuts down.
	 * <p>
	 * the <b>server</b> to the <b>client</b> to inform that the server has stopped.
	 * <p>
	 */
	public PacketStopServer()
	{
		super(PacketType.STOP_SERVER);
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR).getBytes();
	}
}
