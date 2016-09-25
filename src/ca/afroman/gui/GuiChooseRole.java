package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.client.Role;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.packet.PacketSetRole;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;

public class GuiChooseRole extends GuiScreen
{
	private short playerID;
	private ConnectedPlayer player;
	
	private SpriteAnimation player1;
	private SpriteAnimation player2;
	
	private GuiTextButton player1b;
	private GuiTextButton player2b;
	
	private Vector2DInt p1 = new Vector2DInt(58, 48);
	private Vector2DInt p2 = new Vector2DInt(ClientGame.WIDTH - 58 - 16, 48);
	private LightMap lightmap;
	private FlickeringLight light1;
	private FlickeringLight light2;
	
	public GuiChooseRole(GuiScreen parentScreen, short playerID)
	{
		super(parentScreen);
		
		this.playerID = playerID;
		
		player = ClientGame.instance().sockets().getPlayerConnection(playerID);
		
		Role role = player.getRole();
		
		this.addButton(new GuiTextButton(this, 200, (ClientGame.WIDTH / 2) - (72 / 2), 98, 72, blackFont, "Cancel"));
		player1b = new GuiTextButton(this, 201, (ClientGame.WIDTH / 2) - (72 / 2) - 54, 68, 72, blackFont, "Player 1");
		player1b.setEnabled(role != Role.PLAYER1);
		
		player2b = new GuiTextButton(this, 202, (ClientGame.WIDTH / 2) - (72 / 2) + 54, 68, 72, blackFont, "Player 2");
		player2b.setEnabled(role != Role.PLAYER2);
		
		this.addButton(player1b);
		this.addButton(player2b);
		
		player1 = Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT);
		
		light1 = new FlickeringLight(false, -1, new Vector2DDouble(p1.getX() + 8, p1.getY() + 8), 42, 44, 6);
		light2 = new FlickeringLight(false, -1, new Vector2DDouble(p2.getX() + 8, p2.getY() + 8), 42, 44, 6);
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(player1.getCurrentFrame(), p1);
		renderTo.draw(player2.getCurrentFrame(), p2);
		
		if (ClientGame.instance().isLightingOn())
		{
			lightmap.clear();
			light1.renderCentered(lightmap);
			light2.renderCentered(lightmap);
			lightmap.patch();
			
			renderTo.draw(lightmap, LightMap.PATCH_POSITION);
		}
		
		blackFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 20), "Choose a new role for " + player.getUsername());
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
				goToParentScreen();
				break;
			case 201:
				PacketSetRole packet1 = new PacketSetRole(playerID, Role.PLAYER1);
				ClientGame.instance().sockets().sender().sendPacket(packet1);
				goToParentScreen();
				break;
			case 202:
				PacketSetRole packet2 = new PacketSetRole(playerID, Role.PLAYER2);
				ClientGame.instance().sockets().sender().sendPacket(packet2);
				goToParentScreen();
				break;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ClientGame.instance().isLightingOn())
		{
			light1.tick();
			light2.tick();
		}
	}
}
