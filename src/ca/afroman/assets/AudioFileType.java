package ca.afroman.assets;

public enum AudioFileType
{
	/** Signifies that the program still needs to validate the AudioFileType to use. */
	UNCHECKED,
	/** Signifies that insufficient audio files have been found, or have not been found at all. */
	NULL,
	/** Signifies that the program found .mp3 files and should use these files for playback. */
	MP3,
	/** Signifies that the program found .wav files and should use these files for playback. */
	WAV
}
