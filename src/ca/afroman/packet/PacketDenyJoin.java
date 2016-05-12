package ca.afroman.packet;

public class PacketDenyJoin extends Packet
{
	private DenyJoinReason reason;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Denies a client from joining the server, telling the user why they were denied access.
	 * 
	 * @param reason the reason why
	 */
	public PacketDenyJoin(DenyJoinReason reason)
	{
		super(PacketType.DENY_JOIN);
		
		this.reason = reason;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + reason.ordinal()).getBytes();
	}
}
