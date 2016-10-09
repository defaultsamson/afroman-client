package ca.afroman.interfaces;

import ca.afroman.network.IncomingPacketWrapper;

public interface IPacketParser
{
	public void addPacketToParse(IncomingPacketWrapper packet);
	
	public ThreadGroup getThreadGroup();
	
	/**
	 * Parses incoming packets from their byte data into game information and operations.
	 * 
	 * @param packet
	 */
	public void parsePacket(IncomingPacketWrapper packet);
}
