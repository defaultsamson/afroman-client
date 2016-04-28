package ca.afroman.gui;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.network.GameClient;
import ca.afroman.packet.PacketRequestConnection;

public class GuiConnectToServer extends GuiScreen
{
	private Font font;
	
	public GuiConnectToServer(Game game, GuiScreen parent)
	{
		super(game, parent);
	}
	
	@Override
	public void init()
	{
		font = Assets.getFont(Assets.FONT_NORMAL);
		
		String ip = GuiJoinServer.ipText;
		String pass = GuiJoinServer.passwordText;
		
		game.socketClient.setServerIP(ip);
		game.socketClient.sendPacket(new PacketRequestConnection(pass));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		font.renderCentered(renderTo, Game.WIDTH / 2, 80, "Connecting to Server: " + GuiJoinServer.ipText);
		font.renderCentered(renderTo, Game.WIDTH / 2, 100, "Client ID: " + (GameClient.id == -1 ? "NONE" : GameClient.id));
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		
	}
	
	@Override
	public void keyTyped()
	{
		
	}
}
