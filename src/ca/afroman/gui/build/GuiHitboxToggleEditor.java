package ca.afroman.gui.build;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.events.HitboxToggleReceiver;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.gui.GuiTextField;
import ca.afroman.input.TypingMode;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.LevelObjectType;
import ca.afroman.packet.PacketEditHitboxToggle;
import ca.afroman.packet.PacketRemoveLevelObject;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.util.ArrayUtil;

public class GuiHitboxToggleEditor extends GuiScreen
{
	private GuiTextField inTriggers;
	private GuiTextField outTriggers;
	
	private GuiTextButton finish;
	private GuiTextButton cancel;
	private GuiTextButton delete;
	private GuiTextButton enabled;
	private boolean isEnabled;
	
	private ClientLevel level;
	private int triggerID;
	
	public GuiHitboxToggleEditor(ClientLevel level, int triggerID)
	{
		super(null);
		
		this.level = level;
		this.triggerID = triggerID;
		
		int width = (ClientGame.WIDTH - 40);
		
		HitboxToggleReceiver trigger = (HitboxToggleReceiver) level.getScriptedEvent(triggerID);
		
		isEnabled = trigger.isEnabled();
		
		StringBuilder sb2 = new StringBuilder();
		
		for (int e : trigger.getInTriggers())
		{
			sb2.append(e);
			sb2.append(',');
		}
		
		inTriggers = new GuiTextField(this, 20, 58, width);
		inTriggers.setText(sb2.toString());
		inTriggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		inTriggers.setMaxLength(5000);
		addButton(inTriggers);
		
		StringBuilder sb3 = new StringBuilder();
		
		for (int e : trigger.getOutTriggers())
		{
			sb3.append(e);
			sb3.append(',');
		}
		
		outTriggers = new GuiTextField(this, 20, 88, width);
		outTriggers.setText(sb3.toString());
		outTriggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		outTriggers.setMaxLength(5000);
		addButton(outTriggers);
		
		cancel = new GuiTextButton(this, 201, (ClientGame.WIDTH / 2) + 8, 112, 84, blackFont, "Cancel");
		delete = new GuiTextButton(this, 202, (ClientGame.WIDTH / 2) + 46, 6, 54, blackFont, "Delete");
		finish = new GuiTextButton(this, 200, (ClientGame.WIDTH / 2) - 84 - 8, 112, 84, blackFont, "Finished");
		enabled = new GuiTextButton(this, 203, (ClientGame.WIDTH / 2) - 33, 26, 66, blackFont, (isEnabled ? "0" : "X") + " Enabled");
		
		addButton(cancel);
		addButton(delete);
		addButton(finish);
		addButton(enabled);
		keyTyped();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		nobleFont.render(renderTo, new Vector2DInt(36, 48), "In Triggers");
		nobleFont.render(renderTo, new Vector2DInt(36, 78), "Out Triggers");
	}
	
	@Override
	public void keyTyped()
	{
		boolean finished = true;
		
		if (finished)
		{
			String[] trigs = this.inTriggers.getText().split(",");
			if (!ArrayUtil.isEmpty(trigs))
			{
				for (String t : trigs)
				{
					try
					{
						Integer.parseInt(t);
					}
					catch (NumberFormatException e)
					{
						finished = false;
						break;
					}
				}
			}
		}
		
		if (finished)
		{
			String[] trigs = this.outTriggers.getText().split(",");
			if (!ArrayUtil.isEmpty(trigs))
			{
				for (String t : trigs)
				{
					try
					{
						Integer.parseInt(t);
					}
					catch (NumberFormatException e)
					{
						finished = false;
						break;
					}
				}
			}
		}
		
		finish.setEnabled(finished);
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		// Rids of the click so that the Level doesn't get it
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		switch (buttonID)
		{
			case 202:
				ClientGame.instance().sockets().sender().sendPacket(new PacketRemoveLevelObject(triggerID, level.getType(), LevelObjectType.HITBOX_TRIGGER));
			case 201:
				goToParentScreen();
				break;
			case 200:
				String[] inTrigs = this.inTriggers.getText().split(",");
				List<Integer> inTriggers = new ArrayList<Integer>(inTrigs.length);
				if (!ArrayUtil.isEmpty(inTrigs))
				{
					for (String t : inTrigs)
					{
						inTriggers.add(Integer.parseInt(t));
					}
				}
				
				String[] outTrigs = this.outTriggers.getText().split(",");
				List<Integer> outTriggers = new ArrayList<Integer>(outTrigs.length);
				if (!ArrayUtil.isEmpty(outTrigs))
				{
					for (String t : outTrigs)
					{
						outTriggers.add(Integer.parseInt(t));
					}
				}
				
				PacketEditHitboxToggle pack = new PacketEditHitboxToggle(level.getType(), isEnabled, triggerID, inTriggers, outTriggers);
				ClientGame.instance().sockets().sender().sendPacket(pack);
				goToParentScreen();
				break;
			case 203:
				isEnabled = !isEnabled;
				enabled.setText((isEnabled ? "0" : "X") + " Enabled");
				break;
		}
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ClientGame.instance().input().escape.isPressedFiltered())
		{
			goToParentScreen();
		}
		
		if (ClientGame.instance().input().tab.isPressedFiltered())
		{
			if (inTriggers.isFocussed())
			{
				outTriggers.setFocussed();
			}
			else
			{
				inTriggers.setFocussed();
			}
		}
	}
}
