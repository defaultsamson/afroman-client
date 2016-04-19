package ca.pixel.game.entity;

import ca.pixel.game.assets.Assets;
import ca.pixel.game.input.InputHandler;
import ca.pixel.game.world.Level;

public class PlayerEntity extends Entity
{
	private InputHandler input;
	
	public PlayerEntity(Level level, int x, int y, int speed, InputHandler input)
	{
		super(level, Assets.player, x, y, 16, 16, speed);
		
		this.input = input;
	}
	
	@Override
	public void tick()
	{
		int xa = 0;
		int ya = 0;
		
		if (input.up.isPressed())
		{
			ya--;
		}
		if (input.down.isPressed())
		{
			ya++;
		}
		if (input.left.isPressed())
		{
			xa--;
		}
		if (input.right.isPressed())
		{
			xa++;
		}
		
		if (xa != 0 || ya != 0)
		{
			move(xa, ya);
		}
		
		super.tick();
	}
}
