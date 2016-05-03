package ca.afroman.entity;

import java.net.InetAddress;

import ca.afroman.input.InputHandler;

public class PlayerMPEntity extends PlayerEntity
{
	public InetAddress ipAddress;
	public int port;
	
	public PlayerMPEntity(int x, int y, int speed, InputHandler input, InetAddress ipAddress, int port)
	{
		super(1, x, y, speed, input);
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public PlayerMPEntity(int x, int y, int speed, InetAddress ipAddress, int port)
	{
		super(2, x, y, speed, null);
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	@Override
	public void tick()
	{
		super.tick();
	}
}
