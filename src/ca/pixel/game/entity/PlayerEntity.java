package ca.pixel.game.entity;

import ca.pixel.game.assets.Assets;
import ca.pixel.game.input.InputHandler;
import ca.pixel.game.world.Level;

public class PlayerEntity extends SpriteEntity
{
	private InputHandler input;
	
	public PlayerEntity(Level level, int x, int y, int speed, InputHandler input)
	{
		super(level, Assets.playerUp, Assets.playerDown, Assets.playerLeft, Assets.playerRight, Assets.playerIdleUp, Assets.playerIdleDown, Assets.playerIdleLeft, Assets.playerIdleRight, x, y, 16, 16, speed);
		
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
		
		move(xa, ya);
		
		super.tick();
	}
}
