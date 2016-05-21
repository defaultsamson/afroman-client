package ca.afroman.packet;

public class PacketAssignClientID extends Packet
{
	private int clientID;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Tells the client what their ID is.
	 * 
	 * @param id the ID being given
	 */
	public PacketAssignClientID(int clientID)
	{
		super(PacketType.ASSIDN_CLIENTID, true);
		this.clientID = clientID;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + clientID).getBytes();
	}
}
