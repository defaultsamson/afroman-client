package ca.afroman.option;

public enum OptionType
{
	VOLUME_MUSIC,
	VOLUME_SFX,
	SERVER_USERNAME,
	CLIENT_USERNAME,
	CLIENT_PASSWORD,
	CLIENT_IP,
	CLIENT_PORT,
	RENDER_OFF_FOCUS,
	TSYNC,
	FULLSCREEN,
	LIGHT_MODE,
	SCALE,
	
	// Used proprietarily by server
	SERVER_PASSWORD,
	SERVER_IP,
	SERVER_PORT,
	
	// Used only temporarily to remember if the client has done something for the first time before
	HAS_SHOWN_OPTIONS_TIP,
}
