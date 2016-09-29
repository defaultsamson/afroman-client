package ca.afroman.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.afroman.network.IPConnection;
import ca.afroman.util.ArrayUtil;
import ca.afroman.util.ByteUtil;

public class BytePacket
{
	private PacketType type;
	private byte[] content;
	
	private List<IPConnection> connections;
	private boolean mustSend;
	
	/**
	 * Parses a BytePacket from raw byte data.
	 * 
	 * @param rawData
	 * @param sender
	 */
	public BytePacket(byte[] rawData)
	{
		try
		{
			ByteBuffer buf = ByteBuffer.wrap(rawData);
			
			type = PacketType.fromOrdinal(buf.getShort(0));
			content = Arrays.copyOfRange(rawData, ByteUtil.SHORT_BYTE_COUNT, rawData.length);
			connections = new ArrayList<IPConnection>();
		}
		catch (Exception e)
		{
//			System.err.println("Well shit, this packet is fucked");
//			e.printStackTrace();
		}
	}
	
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
		this.mustSend = mustSend;
		
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
	
	public List<IPConnection> getConnections()
	{
		return connections;
	}
	
	public byte[] getContent()
	{
		return content;
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
		byte[] content = getUniqueData();
		
		return ArrayUtil.concatByteArrays(type, content);
	}
	
	public PacketType getType()
	{
		return type;
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
	
	public boolean mustSend()
	{
		return mustSend;
	}
	
	public void setConnections(IPConnection... con)
	{// TODO use an array
		this.connections = Arrays.asList(con);
	}
	
	public void setConnections(List<IPConnection> con)
	{
		this.connections = con;
	}
}
