package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.entity.Role;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.packet.PacketSetRole;

public class GuiChooseRole extends GuiScreen
{
	private int playerID;
	private ConnectedPlayer player;
	
	private SpriteAnimation player1;
	private SpriteAnimation player2;
	private LightMap lightmap;
	private FlickeringLight light1;
	private FlickeringLight light2;
	
	private GuiTextButton player1b;
	private GuiTextButton player2b;
	
	public GuiChooseRole(GuiScreen parentScreen, int playerID)
	{
		super(parentScreen);
		
		this.playerID = playerID;
		
		overrideInit();
	}
	
	@Override
	public void init()
	{
		player1 = Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(Game.WIDTH, Game.HEIGHT, new Color(0F, 0F, 0F, 0.3F));
		
		light1 = new FlickeringLight(0, 0, 42, 44, 8);
		light2 = new FlickeringLight(0, 0, 42, 44, 8);
	}
	
	/**
	 * A new initialisation. Specifically for this class so that
	 * all the player data stuff happens after the rest of the screen has been initialised.
	 */
	public void overrideInit()
	{
		player = game.socketClient.playerByID(playerID);
		
		Role role = player.getRole();
		
		this.buttons.add(new GuiTextButton(this, 200, (Game.WIDTH / 2) - (72 / 2), 116, blackFont, "Cancel"));
		player1b = new GuiTextButton(this, 201, (Game.WIDTH / 2) - (72 / 2) - 78, 116, blackFont, "Player 1");
		player1b.setEnabled(role != Role.PLAYER1);
		
		player2b = new GuiTextButton(this, 202, (Game.WIDTH / 2) - (72 / 2) + 78, 116, blackFont, "Player 2");
		player2b.setEnabled(role != Role.PLAYER2);
		
		this.buttons.add(player1b);
		this.buttons.add(player2b);
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		
	}
	
	@Override
	public void keyTyped()
	{
		
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 200:
				Game.instance().setCurrentScreen(this.parentScreen);
				break;
			case 201:
				// player.setRole(Role.PLAYER1);
				
				PacketSetRole packet1 = new PacketSetRole(playerID, Role.PLAYER1);
				game.socketClient.sendPacket(packet1);
				game.setCurrentScreen(this.parentScreen);
				break;
			case 202:
				// player.setRole(Role.PLAYER2);
				
				PacketSetRole packet2 = new PacketSetRole(playerID, Role.PLAYER2);
				game.socketClient.sendPacket(packet2);
				game.setCurrentScreen(this.parentScreen);
				break;
		}
	}
}
