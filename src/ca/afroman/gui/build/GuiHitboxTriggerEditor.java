package ca.afroman.gui.build;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.HitboxTriggerWrapper;
import ca.afroman.events.TriggerType;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiTextButton;
import ca.afroman.gui.GuiTextField;
import ca.afroman.input.InputHandler;
import ca.afroman.input.TypingMode;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.LevelObjectType;
import ca.afroman.log.ALogType;
import ca.afroman.packet.PacketEditTrigger;
import ca.afroman.packet.PacketRemoveLevelObject;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.util.ArrayUtil;

public class GuiHitboxTriggerEditor extends GuiScreen
{
	private GuiTextField triggers;
	private GuiTextField inTriggers;
	private GuiTextField outTriggers;
	
	private GuiTextButton finish;
	private GuiTextButton cancel;
	
	private ClientLevel level;
	private HitboxTrigger trigger;
	
	public GuiHitboxTriggerEditor(ClientLevel level, int triggerID)
	{
		super(null);
		
		this.level = level;
		
		int width = (ClientGame.WIDTH - 40);
		
		trigger = (HitboxTrigger) level.getScriptedEvent(triggerID);
		
		triggers = new GuiTextField(this, 20, 28, width);
		triggers.setMaxLength(5000);
		triggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		addButton(triggers);
		
		inTriggers = new GuiTextField(this, 20, 58, width);
		
		inTriggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		inTriggers.setMaxLength(5000);
		addButton(inTriggers);
		
		outTriggers = new GuiTextField(this, 20, 88, width);
		outTriggers.setTypingMode(TypingMode.ONLY_NUMBERS_AND_COMMA);
		outTriggers.setMaxLength(5000);
		addButton(outTriggers);
		
		cancel = new GuiTextButton(this, 201, (ClientGame.WIDTH / 2) + 8, 112, 84, blackFont, "Cancel");
		finish = new GuiTextButton(this, 200, (ClientGame.WIDTH / 2) - 84 - 8, 112, 84, blackFont, "Finished");
		
		addButton(cancel);
		addButton(finish);
		addButton(new GuiTextButton(this, 202, (ClientGame.WIDTH / 2) + 60, 6, 54, blackFont, "Delete"));
		addButton(new GuiTextButton(this, 204, (ClientGame.WIDTH / 2) + 14, 6, 42, blackFont, "Copy"));
		initInfo(trigger.getTriggerTypes(), trigger.getInTriggers(), trigger.getOutTriggers());
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		nobleFont.render(renderTo, new Vector2DInt(36, 18), "Trigger Types");
		nobleFont.render(renderTo, new Vector2DInt(36, 48), "In Triggers");
		nobleFont.render(renderTo, new Vector2DInt(36, 78), "Out Triggers");
	}
	
	private void initInfo(List<TriggerType> triggers, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		StringBuilder sb = new StringBuilder();
		
		for (TriggerType t : triggers)
		{
			sb.append(t.ordinal());
			sb.append(',');
		}
		
		this.triggers.setText(sb.toString());
		
		StringBuilder sb2 = new StringBuilder();
		
		for (int e : inTriggers)
		{
			sb2.append(e);
			sb2.append(',');
		}
		
		this.inTriggers.setText(sb2.toString());
		
		StringBuilder sb3 = new StringBuilder();
		
		for (int e : outTriggers)
		{
			sb3.append(e);
			sb3.append(',');
		}
		
		this.outTriggers.setText(sb3.toString());
		
		keyTyped();
	}
	
	@Override
	public void keyTyped()
	{
		boolean finished = true;
		
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
	public void pressAction(int buttonID, boolean isLeft)
	{
		// Rids of the click so that the Level doesn't get it
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		switch (buttonID)
		{
			case 204: // COPY
				ClientGame.instance().input().setClipboard(trigger.toString());
				break;
			case 202:
				ClientGame.instance().sockets().sender().sendPacket(new PacketRemoveLevelObject(trigger.getID(), level.getType(), LevelObjectType.HITBOX_TRIGGER));
			case 201:
				goToParentScreen();
				break;
			case 200:
				String[] trigs = this.triggers.getText().split(",");
				List<TriggerType> triggers = new ArrayList<TriggerType>(trigs.length);
				if (!ArrayUtil.isEmpty(trigs))
				{
					for (String t : trigs)
					{
						triggers.add(TriggerType.fromOrdinal(Integer.parseInt(t)));
					}
				}
				
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
				
				PacketEditTrigger pack = new PacketEditTrigger(level.getType(), trigger.getID(), triggers, inTriggers, outTriggers);
				ClientGame.instance().sockets().sender().sendPacket(pack);
				goToParentScreen();
				break;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ClientGame.instance().input().control.isPressed() && ClientGame.instance().input().v.isPressedFiltered())
		{
			try
			{
				HitboxTriggerWrapper w = HitboxTriggerWrapper.fromString(InputHandler.getClipboard());
				
				if (w != null)
				{
					initInfo(w.getTriggers(), w.getInTriggers(), w.getOutTriggers());
				}
				else
				{
					ClientGame.instance().logger().log(ALogType.DEBUG, "Failed to parse pasted text into GuiHitboxTriggerEditor");
				}
			}
			catch (Exception e)
			{
				ClientGame.instance().logger().log(ALogType.DEBUG, "Failed to parse pasted text into GuiHitboxTriggerEditor");
			}
		}
		
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
}
