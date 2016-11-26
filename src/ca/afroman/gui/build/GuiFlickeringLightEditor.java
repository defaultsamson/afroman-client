package ca.afroman.gui.build;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiSlider;
import ca.afroman.level.api.Grid;
import ca.afroman.light.FlickeringLight;

public class GuiFlickeringLightEditor extends GuiGrid
{
	private GuiSlider fpt;
	private FlickeringLight light;
	
	public GuiFlickeringLightEditor(Grid grid, FlickeringLight light)
	{
		super(grid);
		
		this.light = light;
		
		fpt = new GuiSlider(this, 0, 200 - 4 - 12 - (13 * 6) - 5, 3, 13 * 6, 1, 60, light.getTicksPerFrame(), "TPF");
		
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
				light.setTicksPerFrame(newValue);
			}
		}
	}
}
