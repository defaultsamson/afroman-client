package ca.afroman.gui.build;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiSlider;
import ca.afroman.level.api.Level;

public class GuiFlickeringLightEditor extends GuiGrid
{
	private GuiSlider fpt;
	
	public GuiFlickeringLightEditor()
	{
		super();
		
		Level level = ClientGame.instance().getCurrentLevel();
		
		fpt = new GuiSlider(this, 0, 200 - 4 - 12 - (13 * 6) - 5, 3, 13 * 6, 1, 60, level.flickerCursor.getTicksPerFrame(), "TPF");
		
		addButton(fpt);
	}
	
	@Override
	public void updateValue(int sliderID, int newValue)
	{
		super.updateValue(sliderID, newValue);
		
		if (sliderID == 0)
		{
			if (ClientGame.instance().getCurrentLevel() != null)
			{
				Level level = ClientGame.instance().getCurrentLevel();
				
				level.flickerCursor.setTicksPerFrame(newValue);
			}
		}
	}
}
