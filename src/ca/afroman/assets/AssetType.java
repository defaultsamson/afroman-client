package ca.afroman.assets;

public enum AssetType
{
	INVALID(false),
	
	CAT(true),
	
	FILTER(false),
	
	FONT_BLACK(false),
	FONT_WHITE(false),
	FONT_NOBLE(false),
	
	PLAYER_ONE_RAW(false),
	PLAYER_ONE_UP(false),
	PLAYER_ONE_DOWN(false),
	PLAYER_ONE_LEFT(false),
	PLAYER_ONE_RIGHT(false),
	PLAYER_ONE_IDLE_UP(false),
	PLAYER_ONE_IDLE_DOWN(false),
	PLAYER_ONE_IDLE_LEFT(false),
	PLAYER_ONE_IDLE_RIGHT(false),
	
	PLAYER_TWO_RAW(false),
	PLAYER_TWO_UP(false),
	PLAYER_TWO_DOWN(false),
	PLAYER_TWO_LEFT(false),
	PLAYER_TWO_RIGHT(false),
	PLAYER_TWO_IDLE_UP(false),
	PLAYER_TWO_IDLE_DOWN(false),
	PLAYER_TWO_IDLE_LEFT(false),
	PLAYER_TWO_IDLE_RIGHT(false),
	
	SLIME(false),
	
	TILE_GRASS(true),
	TILE_GRASS_INNER_TOPLEFT(true),
	TILE_GRASS_INNER_TOPRIGHT(true),
	TILE_GRASS_INNER_BOTTOMLEFT(true),
	TILE_GRASS_INNER_BOTTOMRIGHT(true),
	TILE_GRASS_OUTER_TOPLEFT(true),
	TILE_GRASS_OUTER_TOPRIGHT(true),
	TILE_GRASS_OUTER_BOTTOMLEFT(true),
	TILE_GRASS_OUTER_BOTTOMRIGHT(true),
	TILE_GRASS_OUTER_RIGHT(true),
	TILE_GRASS_OUTER_LEFT(true),
	TILE_GRASS_OUTER_BOTTOM(true),
	TILE_GRASS_OUTER_TOP(true),
	
	TILE_GRASS_TALL_RIGHT(true),
	TILE_GRASS_TALL_LEFT(true),
	TILE_GRASS_WIDE_RIGHT(true),
	TILE_GRASS_WIDE_LEFT(true),
	TILE_GRASS_FLOWER_RIGHT(true),
	TILE_GRASS_FLOWER_LEFT(true),
	
	TILE_DIRT(true),
	
	TILE_WATER(true),
	
	TILE_OBJECT_POST(true),
	
	TILE_WALL(true),
	TILE_WALL_GRASS(true),
	TILE_WALL_GRASS_FLIP(true),
	TILE_WALL_GRASS_SIDE(true),
	TILE_WALL_GRASS_SIDE_FLIP(true),
	
	TILE_WALL_OUTCORNER_N(true),
	TILE_WALL_OUTCORNER_E(true),
	TILE_WALL_OUTCORNER_S(true),
	TILE_WALL_OUTCORNER_W(true),
	
	TILE_WALL_INCORNER_BOTTOM_R(true),
	TILE_WALL_INCORNER_BOTTOM_L(true),
	TILE_WALL_INCORNER_TOP_L(true),
	TILE_WALL_INCORNER_TOP_R(true),
	
	TILE_WALL_PIPE(true),
	TILE_WALL_PIPE_HALF(true),
	TILE_WALL_PIPE_SIDE(true),
	TILE_WALL_PIPE_TLCORNER(true),
	TILE_WALL_PIPE_TRCORNER(true),
	TILE_WALL_PIPE_EDGE(true),
	
	TILE_BRIDGE_WOOD_SIDE(true),
	
	TILE_DOOR_UP_CLOSED(true),
	TILE_DOOR_UP_OPEN(true),
	TILE_DOOR_RIGHT_CLOSED(true),
	TILE_DOOR_RIGHT_OPEN(true),
	TILE_DOOR_DOWN_CLOSED(true),
	TILE_DOOR_DOWN_OPEN(true),
	TILE_DOOR_LEFT_CLOSED(true),
	TILE_DOOR_LEFT_OPEN(true),
	
	TILE_PLATE_UP(true),
	TILE_PLATE_DOWN(true),
	
	TILE_SWITCH_LEFT(true),
	TILE_SWITCH_RIGHT(true),
	
	TILE_BLOCK(true),
	TILE_BlOCK_ANIMATED(true),
	
	TILE_AURA(true),
	
	TILE_ROCK(true),
	
	TILE_LAMP_LEFT(true),
	TILE_LAMP_RIGHT(true),
	TILE_LAMP(true),
	
	BUTTON_PRESSED(false),
	BUTTON_HOVER(false),
	BUTTON_NORMAL(false),
	
	ICON_UPDATE(false),
	ICON_SETTINGS(false),
	ICON_REFRESH(false),
	ICON_NEXT(false),
	
	TEXT_FIELD(false),
	TEXT_FIELD_CLEAR(false),
	
	AUDIO_BUTTON_PUSH(false),
	AUDIO_BUTTON_RELEASE(false),
	AUDIO_MENU_MUSIC(false);
	
	/**
	 * Gets the AssetType value with the corresponding ordinal value.
	 * 
	 * @param ordinal the ordinal value
	 * @return the matching AssetType.
	 */
	public static AssetType fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	private boolean isPlacableInBuildMode;
	
	/**
	 * A type value used to define and differentiate Asset objects from one another.
	 * 
	 * @param isPlacableInBuildMode whether or not the Asset object with this type should be placeable from within build mode
	 */
	AssetType(boolean isPlacableInBuildMode)
	{
		this.isPlacableInBuildMode = isPlacableInBuildMode;
	}
	
	/**
	 * Gets the enum value of this prior to this value.
	 * <p>
	 * If no value is found before this value, the value at index
	 * <i>n - 1</i> will be returned, where <i>n</i> is the total
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
	
	// /**
	// * Gets the enum value of this prior to this value, where the
	// * value of Assets.getAsset(getLast()) is an instance of DrawableAsset.
	// * <p>
	// * If no value is found before this value, the value at index
	// * <i>n - 1</i> will be returned, where <i>n</i> is the total
	// * number of values for this enumerator.
	// *
	// * @return the next item on the list of this enumerator.
	// */
	// public AssetType getLastDrawableAsset()
	// {
	// AssetType current = this;
	// for (int i = 0; i < values().length; i++)
	// if (Assets.getAsset(current = current.getLast()) instanceof DrawableAsset) return current;
	// return null;
	// }
	
	/**
	 * Gets the enum value of this prior to this value, where the
	 * value of Assets.getAsset(getLast()) is an instance of DrawableAsset,
	 * and is also set to be used in Build mode.
	 * <p>
	 * If no value is found before this value, the value at index
	 * <i>n - 1</i> will be returned, where <i>n</i> is the total
	 * number of values for this enumerator.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public AssetType getLastBuildModeAsset()
	{
		AssetType current = this;
		for (int i = 0; i < values().length; i++)
		{
			current = current.getLast();
			if (current.isPlacableInBuildMode && Assets.getAsset(current) instanceof DrawableAsset) return current;
		}
		return null;
	}
	
	// /**
	// * Gets the enum value of this past this value, where the
	// * value of Assets.getAsset(getNext()) is an instance of DrawableAsset.
	// * <p>
	// * If no value is found past this value, the value at
	// * index 0 will be returned.
	// *
	// * @return the next item on the list of this enumerator.
	// */
	// public AssetType getNextDrawableAsset()
	// {
	// AssetType current = this;
	// for (int i = 0; i < values().length; i++)
	// if (Assets.getAsset(current = current.getNext()) instanceof DrawableAsset) return current;
	// return null;
	// }
	
	/**
	 * Gets the enum value of this past this value.
	 * <p>
	 * If no value is found past this value, the value at
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
	 * Gets the enum value of this past this value, where the
	 * value of Assets.getAsset(getNext()) is an instance of DrawableAsset,
	 * and is also set to be used in Build mode.
	 * <p>
	 * If no value is found past this value, the value at
	 * index 0 will be returned.
	 * 
	 * @return the next item on the list of this enumerator.
	 */
	public AssetType getNextBuildModeAsset()
	{
		AssetType current = this;
		for (int i = 0; i < values().length; i++)
		{
			current = current.getNext();
			if (current.isPlacableInBuildMode && Assets.getAsset(current) instanceof DrawableAsset) return current;
		}
		return null;
	}
}
