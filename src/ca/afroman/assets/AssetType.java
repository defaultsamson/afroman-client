package ca.afroman.assets;

public enum AssetType
{
	INVALID,
	
	CAT,
	
	FILTER,
	
	FONT_BLACK,
	FONT_WHITE,
	FONT_NOBLE,
	
	PLAYER_ONE_RAW,
	PLAYER_ONE_UP,
	PLAYER_ONE_DOWN,
	PLAYER_ONE_LEFT,
	PLAYER_ONE_RIGHT,
	PLAYER_ONE_IDLE_UP,
	PLAYER_ONE_IDLE_DOWN,
	PLAYER_ONE_IDLE_LEFT,
	PLAYER_ONE_IDLE_RIGHT,
	
	PLAYER_TWO_RAW,
	PLAYER_TWO_UP,
	PLAYER_TWO_DOWN,
	PLAYER_TWO_LEFT,
	PLAYER_TWO_RIGHT,
	PLAYER_TWO_IDLE_UP,
	PLAYER_TWO_IDLE_DOWN,
	PLAYER_TWO_IDLE_LEFT,
	PLAYER_TWO_IDLE_RIGHT,
	
	SLIME,
	
	TILE_GRASS,
	TILE_GRASS_INNER_TOPLEFT,
	TILE_GRASS_INNER_TOPRIGHT,
	TILE_GRASS_INNER_BOTTOMLEFT,
	TILE_GRASS_INNER_BOTTOMRIGHT,
	TILE_GRASS_OUTER_TOPLEFT,
	TILE_GRASS_OUTER_TOPRIGHT,
	TILE_GRASS_OUTER_BOTTOMLEFT,
	TILE_GRASS_OUTER_BOTTOMRIGHT,
	TILE_GRASS_OUTER_RIGHT,
	TILE_GRASS_OUTER_LEFT,
	TILE_GRASS_OUTER_BOTTOM,
	TILE_GRASS_OUTER_TOP,
	
	TILE_DIRT,
	
	TILE_WATER,
	
	TILE_OBJECT_POST,
	
	TILE_WALL,
	TILE_WALL_GRASS,
	TILE_WALL_GRASS_FLIP,
	TILE_WALL_GRASS_SIDE,
	TILE_WALL_GRASS_SIDE_FLIP,
	TILE_WALL_WALL_N,
	TILE_WALL_WALL_E,
	TILE_WALL_WALL_S,
	TILE_WALL_WALL_W,
	
	TILE_WALL_OUTCORNER_N,
	TILE_WALL_OUTCORNER_E,
	TILE_WALL_OUTCORNER_S,
	TILE_WALL_OUTCORNER_W,
	
	TILE_WALL_INCORNER_BOTTOM_R,
	TILE_WALL_INCORNER_BOTTOM_L,
	TILE_WALL_INCORNER_TOP_L,
	TILE_WALL_INCORNER_TOP_R,
	
	TILE_WALL_PIPE,
	TILE_WALL_PIPE_HALF,
	TILE_WALL_PIPE_SIDE,
	TILE_WALL_PIPE_TLCORNER,
	TILE_WALL_PIPE_TRCORNER,
	TILE_WALL_PIPE_EDGE,
	
	TILE_BRIDGE_WOOD_SIDE,
	
	TILE_DOOR_FRONT_CLOSED,
	TILE_DOOR_FRONT_OPEN,
	TILE_DOOR_RIGHT_CLOSED,
	TILE_DOOR_RIGHT_OPEN,
	TILE_DOOR_BOTTOM_CLOSED,
	TILE_DOOR_BOTTOM_OPEN,
	TILE_DOOR_LEFT_CLOSED,
	TILE_DOOR_LEFT_OPEN,
	
	TILE_PLATE_TEST,
	
	TILE_AURA,
	
	TILE_ROCK,
	
	TILE_LAMP_LEFT,
	TILE_LAMP_RIGHT,
	TILE_LAMP,
	
	BUTTON_PRESSED,
	BUTTON_HOVER,
	BUTTON_NORMAL,
	
	ICON_UPDATE,
	ICON_SETTINGS,
	ICON_REFRESH,
	ICON_NEXT,
	
	TEXT_FIELD,
	TEXT_FIELD_CLEAR,
	
	AUDIO_BUTTON_PUSH,
	AUDIO_BUTTON_RELEASE,
	AUDIO_MENU_MUSIC;
	
	public static AssetType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	/**
	 * Gets the enum value of this prior to the <b>current</b> value.
	 * <p>
	 * If no value is found before the <b>current</b> value, the value at
	 * index <i>n - 1</i> will be returned, where <i>n</i> is the total
	 * number of values for this enumerator.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public AssetType getLast()
	{
		int newOrdinal = ordinal() - 1;
		
		if (newOrdinal < 0)
		{
			return fromOrdinal(values().length - 1);
		}
		else
		{
			return fromOrdinal(newOrdinal);
		}
	}
	
	/**
	 * Gets the enum value of this prior to the <b>current</b> value.
	 * <p>
	 * If no value is found before the <b>current</b> value, the value at
	 * index <i>n - 1</i> will be returned, where <i>n</i> is the total
	 * number of values for this enumerator.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public AssetType getLastDrawableAsset()
	{
		AssetType current = this;
		for (int i = 0; i < values().length; i++)
			if (Assets.getAsset(current = current.getLast()) instanceof DrawableAsset) return current;
		return null;
	}
	
	/**
	 * Gets the enum value of this past the <b>current</b> value.
	 * <p>
	 * If no value is found past the <b>current</b> value, the value at
	 * index 0 will be returned.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public AssetType getNext()
	{
		int newOrdinal = ordinal() + 1;
		
		if (newOrdinal > values().length - 1)
		{
			return fromOrdinal(0);
		}
		else
		{
			return fromOrdinal(newOrdinal);
		}
	}
	
	/**
	 * Gets the enum value of this past the <b>current</b> value.
	 * <p>
	 * If no value is found past the <b>current</b> value, the value at
	 * index 0 will be returned.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public AssetType getNextDrawableAsset()
	{
		AssetType current = this;
		for (int i = 0; i < values().length; i++)
			if (Assets.getAsset(current = current.getNext()) instanceof DrawableAsset) return current;
		return null;
	}
}
