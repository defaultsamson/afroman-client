package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.ClientGame;
import ca.afroman.asset.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.packet.PacketSetRole;
import ca.afroman.player.Role;

public class GuiChooseRole extends GuiScreen
{
	private int playerID;
	private ConnectedPlayer player;
	
	private SpriteAnimation player1;
	private SpriteAnimation player2;
	
	private GuiTextButton player1b;
	private GuiTextButton player2b;
	
	private int p1X = 58;
	private int p1Y = 48;
	private int p2X = ClientGame.WIDTH - 58 - 16;
	private int p2Y = 48;
	private LightMap lightmap;
	private FlickeringLight light1;
	private FlickeringLight light2;
	
	public GuiChooseRole(GuiScreen parentScreen, int playerID)
	{
		super(parentScreen);
		
		this.playerID = playerID;
		
		overrideInit();
	}
	
	@Override
	public void init()
	{
		
	}
	
	/**
	 * A new initialisation. Specifically for this class so that
	 * all the player data stuff happens after the rest of the screen has been initialised.
	 */
	public void overrideInit()
	{
		player = game.socketClient.playerByID(playerID);
		
		Role role = player.getRole();
		
		this.buttons.add(new GuiTextButton(this, 200, (ClientGame.WIDTH / 2) - (72 / 2), 98, blackFont, "Cancel"));
		player1b = new GuiTextButton(this, 201, (ClientGame.WIDTH / 2) - (72 / 2) - 54, 68, blackFont, "Player 1");
		player1b.setEnabled(role != Role.PLAYER1);
		
		player2b = new GuiTextButton(this, 202, (ClientGame.WIDTH / 2) - (72 / 2) + 54, 68, blackFont, "Player 2");
		player2b.setEnabled(role != Role.PLAYER2);
		
		this.buttons.add(player1b);
		this.buttons.add(player2b);
		
		player1 = Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, new Color(0F, 0F, 0F, 0.3F));
		
		light1 = new FlickeringLight(null, p1X + 8, p2Y + 8, 42, 44, 6);
		light2 = new FlickeringLight(null, p2X + 8, p1Y + 8, 42, 44, 6);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		light1.tick();
		light2.tick();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(player1.getCurrentFrame(), p1X, p1Y);
		renderTo.draw(player2.getCurrentFrame(), p2X, p2Y);
		
		lightmap.clear();
		light1.renderCentered(lightmap);
		light2.renderCentered(lightmap);
		lightmap.patch();
		
		renderTo.draw(lightmap, 0, 0);
		
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 20, "Choose a new role for " + player.getUsername());
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
				ClientGame.instance().setCurrentScreen(this.parentScreen);
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
