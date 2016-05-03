package ca.afroman.entity;

import java.awt.Rectangle;

import ca.afroman.assets.Assets;
import ca.afroman.input.InputHandler;

public class PlayerEntity extends SpriteEntity
{
	private InputHandler input;
	private int player;
	
	/**
	 * 
	 * @param player can be player 1 or 2
	 * @param level
	 * @param x
	 * @param y
	 * @param speed
	 * @param input
	 */
	public PlayerEntity(int player, int x, int y, int speed, InputHandler input)
	{
		super((player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_UP) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_UP)), (player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_DOWN) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_DOWN)), (player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_LEFT) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_LEFT)), (player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_RIGHT) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_RIGHT)), (player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_UP) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_UP)), (player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_DOWN) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_DOWN)), (player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_LEFT) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_LEFT)),
				(player == 1 ? Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_RIGHT) : Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_RIGHT)), x, y, speed, new Rectangle(3, 3, 10, 13));
		
		this.player = player;
		this.input = input;
	}
	
	@Override
	public void tick()
	{
		int xa = 0;
		int ya = 0;
		
		if (input != null)
		{
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
		}
		
		move(xa, ya);
		
		super.tick();
	}
	
	public int getPlayerID()
	{
		return player;
	}
}
