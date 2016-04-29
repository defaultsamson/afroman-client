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
	
	public GuiConnectToServer(GuiScreen parent)
	{
		super(parent);
	}
	
	@Override
	public void init()
	{
		font = Assets.getFont(Assets.FONT_BLACK);
		
		game.socketClient.setServerIP(game.getServerIP());
		game.socketClient.sendPacket(new PacketRequestConnection(game.getUsername(), game.getPassword()));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		font.renderCentered(renderTo, Game.WIDTH / 2, 80, "Connecting to Server: " + game.getServerIP());
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
