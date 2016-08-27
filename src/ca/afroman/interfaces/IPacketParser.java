package ca.afroman.interfaces;

import ca.afroman.packet.BytePacket;

public interface IPacketParser
{
	public void addPacketToParse(BytePacket packet);
	
	public ThreadGroup getThreadGroup();
	
	public void parsePacket(BytePacket packet);
}
