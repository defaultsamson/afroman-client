package ca.afroman.gui;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.util.CommandUtil;

public class GuiCommand extends GuiScreen
{
	private static List<String> previousCommands = new ArrayList<String>();
	
	private GuiTextField input;
	private int browsingIndex;
	private String currentText;
	
	public GuiCommand(GuiScreen parent)
	{
		super(parent);
		
		input = new GuiTextField(this, 0, ClientGame.HEIGHT - 16, ClientGame.WIDTH);
		addButton(input);
		input.setText("/");
		input.setFocussed();
		currentText = input.getText();
		
		browsingIndex = -1;
	}
	
	@Override
	public void keyTyped()
	{
		if (browsingIndex == -1)
		{
			currentText = input.getText();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(Texture renderTo)
	{
		// Renders this overtop of other GUIs
		getParent().render(renderTo);
		super.render(renderTo);
	}
	
	private void setText(String text)
	{
		input.setFocussed(false);
		input.setText(text);
		input.setFocussed();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ClientGame.instance().input().escape.isPressedFiltered())
		{
			goToParentScreen();
		}
		if (ClientGame.instance().input().up.isPressedFiltered())
		{
			if (browsingIndex < previousCommands.size() - 1)
			{
				browsingIndex++;
				
				setText(previousCommands.get(browsingIndex));
			}
		}
		if (ClientGame.instance().input().down.isPressedFiltered())
		{
			if (browsingIndex > 0)
			{
				browsingIndex--;
				
				setText(previousCommands.get(browsingIndex));
			}
			else if (browsingIndex == 0)
			{
				browsingIndex = -1;
				
				setText(currentText);
			}
		}
		if (ClientGame.instance().input().enter.isPressedFiltered())
		{
			previousCommands.add(0, input.getText());
			
			CommandUtil.issueCommand(input.getText());
			goToParentScreen();
		}
	}
}
