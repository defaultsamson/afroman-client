package ca.afroman.gui.build;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.TriggerType;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.gui.GuiTextField;
import ca.afroman.input.TypingMode;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.LevelObjectType;
import ca.afroman.packet.PacketEditTrigger;
import ca.afroman.packet.PacketRemoveLevelObject;
import ca.afroman.util.ArrayUtil;

public class GuiHitboxTriggerEditor extends GuiScreen
{
	private GuiTextField triggers;
	private GuiTextField inTriggers;
	private GuiTextField outTriggers;
	
	private GuiTextButton finish;
	private GuiTextButton cancel;
	private GuiTextButton delete;
	
	private ClientLevel level;
	private int triggerID;
	
	public GuiHitboxTriggerEditor(ClientLevel level, int triggerID)
	{
		super(null);
		
		this.level = level;
		this.triggerID = triggerID;
		
		int width = (ClientGame.WIDTH - 40);
		
		HitboxTrigger trigger = (HitboxTrigger) level.getScriptedEvent(triggerID);
		
		String text = "";
		
		for (TriggerType t : trigger.getTriggerTypes())
		{
			text += t.ordinal() + ",";
		}
		
		triggers = new GuiTextField(this, 20, 28, width);
		triggers.setFocussed();
		triggers.setText(text);
		triggers.setMaxLength(5000);
		triggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		buttons.add(triggers);
		
		text = "";
		
		for (int e : trigger.getInTriggers())
		{
			text += e + ",";
		}
		
		inTriggers = new GuiTextField(this, 20, 58, width);
		inTriggers.setText(text);
		inTriggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		inTriggers.setMaxLength(5000);
		buttons.add(inTriggers);
		
		text = "";
		
		for (int e : trigger.getOutTriggers())
		{
			text += e + ",";
		}
		
		outTriggers = new GuiTextField(this, 20, 88, width);
		outTriggers.setText(text);
		outTriggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		outTriggers.setMaxLength(5000);
		buttons.add(outTriggers);
		
		cancel = new GuiTextButton(this, 201, (ClientGame.WIDTH / 2) + 8, 112, 84, blackFont, "Cancel");
		delete = new GuiTextButton(this, 202, (ClientGame.WIDTH / 2) + 46, 6, 54, blackFont, "Delete");
		finish = new GuiTextButton(this, 200, (ClientGame.WIDTH / 2) - 84 - 8, 112, 84, blackFont, "Finished");
		
		buttons.add(cancel);
		buttons.add(delete);
		buttons.add(finish);
		keyTyped();
	}
	
	@Override
	public void init()
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
			if (triggers.isFocussed())
			{
				inTriggers.setFocussed();
			}
			else if (inTriggers.isFocussed())
			{
				outTriggers.setFocussed();
			}
			else
			{
				triggers.setFocussed();
			}
		}
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		nobleFont.render(renderTo, 36, 18, "Trigger Types");
		nobleFont.render(renderTo, 36, 48, "In Triggers");
		nobleFont.render(renderTo, 36, 78, "Out Triggers");
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
				List<TriggerType> triggers = new ArrayList<TriggerType>();
				String[] trigs = this.triggers.getText().split(",");
				if (!ArrayUtil.isEmpty(trigs))
				{
					for (String t : trigs)
					{
						triggers.add(TriggerType.fromOrdinal(Integer.parseInt(t)));
					}
				}
				
				List<Integer> inTriggers = new ArrayList<Integer>();
				String[] inTrigs = this.inTriggers.getText().split(",");
				if (!ArrayUtil.isEmpty(inTrigs))
				{
					for (String t : inTrigs)
					{
						inTriggers.add(Integer.parseInt(t));
					}
				}
				
				List<Integer> outTriggers = new ArrayList<Integer>();
				String[] outTrigs = this.outTriggers.getText().split(",");
				if (!ArrayUtil.isEmpty(outTrigs))
				{
					for (String t : outTrigs)
					{
						outTriggers.add(Integer.parseInt(t));
					}
				}
				
				PacketEditTrigger pack = new PacketEditTrigger(level.getType(), triggerID, triggers, inTriggers, outTriggers);
				ClientGame.instance().sockets().sender().sendPacket(pack);
				goToParentScreen();
				break;
		}
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		
	}
	
	@Override
	public void keyTyped()
	{
		boolean finished = true;
		
		if (!this.triggers.getText().contains(",,"))
		{
			String[] trigs = this.triggers.getText().split(",");
			
			if (!ArrayUtil.isEmpty(trigs))
			{
				for (String t : trigs)
				{
					try
					{
						int ord = Integer.parseInt(t);
						if (TriggerType.fromOrdinal(ord) == null)
						{
							finished = false;
							break;
						}
					}
					catch (NumberFormatException e)
					{
						finished = false;
						break;
					}
				}
			}
		}
		else
		{
			finished = false;
		}
		
		if (finished)
		{
			if (!this.triggers.getText().contains(",,"))
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
			else
			{
				finished = false;
			}
		}
		
		if (finished)
		{
			if (!this.triggers.getText().contains(",,"))
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
			else
			{
				finished = false;
			}
		}
		
		finish.setEnabled(finished);
	}
}
