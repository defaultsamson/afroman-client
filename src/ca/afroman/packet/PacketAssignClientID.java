package ca.afroman.packet;

public class PacketAssignClientID extends Packet
{
	private int id;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Tells the client what their ID is.
	 * 
	 * @param id the ID being given
	 */
	public PacketAssignClientID(int id)
	{
		super(PacketType.ASSIDN_CLIENTID);
		this.id = id;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + id).getBytes();
	}
}
