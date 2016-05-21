package ca.afroman.packet;

public class PacketConfirmReceived extends Packet
{
	private int idRecieved;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * or
	 * <p>
	 * Designed to be sent from the <b>client</b> to the <b>server</b>.
	 * <p>
	 * 
	 * Tells one socket that the other socket has received the packet with the specified ID, so the socket can stop trying to send it.
	 * 
	 * @param id the ID being given
	 */
	public PacketConfirmReceived(int idRecieved)
	{
		super(PacketType.CONFIRM_RECEIVED, false);
		this.idRecieved = idRecieved;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + idRecieved).getBytes();
	}
}
