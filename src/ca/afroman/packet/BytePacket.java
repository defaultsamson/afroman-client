package ca.afroman.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.afroman.network.IPConnection;
import ca.afroman.util.ArrayUtil;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.IDCounter;

public class BytePacket
{
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
	
	private PacketType type;
	private int id;
	private byte[] content;
	private List<IPConnection> connections;
	
	/**
	 * Creates a new BytePacket to send.
	 * 
	 * @param type the type of packet being sent
	 * @param mustSend whether this packet must be pushed until the other end confirms of receiving it
	 * @param receiver the desired receiver of the packet
	 */
	public BytePacket(PacketType type, boolean mustSend, IPConnection... receivers)
	{
		this.type = type;
		id = mustSend ? getIDCounter().getNext() : IDCounter.WASTE_ID;
		
		if (receivers != null)
		{
			connections = new ArrayList<IPConnection>(receivers.length);
			
			for (IPConnection con : receivers)
			{
				connections.add(con);
			}
		}
		else
		{
			connections = new ArrayList<IPConnection>();
		}
	}
	
	/**
	 * Parses a BytePacket from raw byte data.
	 * 
	 * @param rawData
	 * @param sender
	 */
	public BytePacket(byte[] rawData, IPConnection sender)
	{
		ByteBuffer buf = ByteBuffer.wrap(rawData);
		
		type = PacketType.fromOrdinal(buf.getShort(0));
		id = buf.getInt(2);
		content = Arrays.copyOfRange(rawData, ByteUtil.SHORT_BYTE_COUNT + ByteUtil.INT_BYTE_COUNT, rawData.length);
		connections = new ArrayList<IPConnection>(1);
		connections.add(sender);
	}
	
	/**
	 * Gets the data from this Packet in a sendable form.
	 * <p>
	 * <b>WARNING:</b> Only intended for reading, DO NOT OVERRIDE.
	 * <p>
	 * Override <code>getUniqueData()</code> instead
	 *
	 * @return the data
	 */
	public final byte[] getData()
	{
		byte[] type = ByteUtil.shortAsBytes((short) getType().ordinal());
		byte[] id = ByteUtil.intAsBytes(this.id);
		byte[] content = getUniqueData();
		
		return ArrayUtil.concatByteArrays(type, id, content);
	}
	
	/**
	 * Gets the data from this Packet in a sendable form.
	 * 
	 * @return the data
	 */
	public byte[] getUniqueData()
	{
		return new byte[] {};
	}
	
	public byte[] getContent()
	{
		return content;
	}
	
	public boolean mustSend()
	{
		return id != IDCounter.WASTE_ID;
	}
	
	public int getID()
	{
		return id;
	}
	
	public PacketType getType()
	{
		return type;
	}
	
	public List<IPConnection> getConnections()
	{
		return connections;
	}
	
	public void setConnections(IPConnection... con)
	{
		this.connections = Arrays.asList(con);
	}
	
	public void setConnections(List<IPConnection> con)
	{
		this.connections = con;
	}
}
