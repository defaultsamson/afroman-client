package ca.afroman.packet;

public class PacketSendLevels extends Packet
{
	private boolean isSending;
	
	/**
	 * Designed to be sent from...
	 * <p>
	 * the host's <b>client</b> to the <b>server</b> to requests that the server sends all the levels to the players.
	 * <p>
	 * the <b>server</b> to the <b>client</b> to inform that the server is going to be sending all the levels.
	 * <p>
	 * 
	 * @param isSending whether the server is sending levels, or is stopping sending levels
	 */
	public PacketSendLevels(boolean isSending)
	{
		super(PacketType.SEND_LEVELS, true);
		
		this.isSending = isSending;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + (isSending ? 1 : 0)).getBytes();
	}
}
