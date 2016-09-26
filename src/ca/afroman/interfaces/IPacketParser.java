package ca.afroman.interfaces;

import ca.afroman.network.IncomingPacketWrapper;

public interface IPacketParser
{
	public void addPacketToParse(IncomingPacketWrapper packet);
	
	public ThreadGroup getThreadGroup();
	
	public void parsePacket(IncomingPacketWrapper packet);
}
