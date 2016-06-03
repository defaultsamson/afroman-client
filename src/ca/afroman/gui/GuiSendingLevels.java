package ca.afroman.gui;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.client.ExitGameReason;
import ca.afroman.packet.PacketDisconnect;
import ca.afroman.packet.PacketStartGame;
import ca.afroman.packet.PacketStopServer;

public class GuiSendingLevels extends GuiScreen
{
	private GuiButton resendButton;
	private GuiButton stopButton;
	
	private long startTime;
	private int millsPassed;
	
	public GuiSendingLevels(GuiScreen parent)
	{
		super(parent);
	}
	
	@Override
	public void init()
	{
		resendButton = new GuiTextButton(this, 2000, 20 + 20, 116, 72, blackFont, "Resend Levels");
		resendButton.setEnabled(ClientGame.instance().isHostingServer());
		buttons.add(resendButton);
		
		// Draw a stop server button
		if (ClientGame.instance().isHostingServer())
		{
			stopButton = new GuiTextButton(this, 2001, 148 - 20, 116, 72, blackFont, "Stop Server");
		}
		// Draw a leave server button
		else
		{
			stopButton = new GuiTextButton(this, 2002, 148 - 20, 116, 72, blackFont, "Disconnect");
		}
		buttons.add(stopButton);
		
		startTime = System.currentTimeMillis();
		millsPassed = 0;
	}
	
	@Override
	public void tick()
	{
		millsPassed = (int) (System.currentTimeMillis() - startTime);
		
		super.tick();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		if (ClientGame.instance().isHostingServer())
		{
			// TODO not just fucking 3 you git
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 20, "Sent levels to (" + 3 + "/" + ClientGame.instance().sockets().getConnectedPlayers().size() + ") players");
			
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 45, "Waiting for client responses");
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 55, "for " + (millsPassed / 1000) + " seconds...");
			
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 80, "If nothing happens for a while,");
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 90, "try resending the levels.");
		}
		else
		{
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 20, "Recieving levels...");
		}
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
			case 2000: // TODO Resend Levels
				ClientGame.instance().sockets().sender().sendPacket(new PacketStartGame());
				break;
			case 2001: // Stop Server
				ClientGame.instance().sockets().sender().sendPacket(new PacketStopServer());
			case 2002: // Leave server
				ClientGame.instance().sockets().sender().sendPacket(new PacketDisconnect());
				ClientGame.instance().exitFromGame(ExitGameReason.DISCONNECT);
		}
	}
	
	@Override
	public void keyTyped()
	{
		
	}
}
