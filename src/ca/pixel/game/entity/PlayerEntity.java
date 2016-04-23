package ca.pixel.game.entity;

import java.awt.Rectangle;

import ca.pixel.game.assets.Assets;
import ca.pixel.game.input.InputHandler;
import ca.pixel.game.world.Level;

public class PlayerEntity extends SpriteEntity
{
	private InputHandler input;
	
	public PlayerEntity(Level level, int x, int y, int speed, InputHandler input)
	{
		super(level, Assets.getSpriteAnimation(Assets.PLAYER_UP), Assets.getSpriteAnimation(Assets.PLAYER_DOWN), Assets.getSpriteAnimation(Assets.PLAYER_LEFT), Assets.getSpriteAnimation(Assets.PLAYER_RIGHT), Assets.getSpriteAnimation(Assets.PLAYER_IDLE_UP), Assets.getSpriteAnimation(Assets.PLAYER_IDLE_DOWN), Assets.getSpriteAnimation(Assets.PLAYER_IDLE_LEFT), Assets.getSpriteAnimation(Assets.PLAYER_IDLE_RIGHT), x, y, speed, new Rectangle(3, 3, 10, 13));
		
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
