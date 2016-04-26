package ca.pixel.game.entity;

import java.net.InetAddress;

import ca.pixel.game.input.InputHandler;
import ca.pixel.game.world.Level;

public class PlayerMPEntity extends PlayerEntity
{
	public InetAddress ipAddress;
	public int port;

	public PlayerMPEntity(Level level, int x, int y, int speed, InputHandler input, InetAddress ipAddress, int port)
	{
		super(1, level, x, y, speed, input);
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public PlayerMPEntity(Level level, int x, int y, int speed, InetAddress ipAddress, int port)
	{
		super(2, level, x, y, speed, null);
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	@Override
	public void tick()
	{
		super.tick();
	}
}
