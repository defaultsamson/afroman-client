package ca.afroman.assets;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;
import javax.sound.sampled.FloatControl.Type;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.option.Options;

public class AudioClip extends Asset
{
	private static final String AUDIO_DIR = "/audio/";
	private static final String MP3_DIR = "mp3/";
	private static final String WAV_DIR = "wav/";
	
	private static AudioFileType FILE_TYPE = AudioFileType.UNCHECKED;
	private static final String USE_MP3_TEST = "music/menu";
	
	/**
	 * Searches through the audio directories within the running jar's resources
	 * and uses the available files to determine whether to use .wav or .mp3 files
	 * for audio playback. By default, the game will use .wav files if they are
	 * available, because they provide smoother, higher quality playback, and
	 * abide by the rules of using no external libraries.
	 * 
	 * @return which audio files this distribution is using.
	 */
	public static AudioFileType fileType()
	{
		if (FILE_TYPE == AudioFileType.UNCHECKED)
		{
			if (AudioClip.class.getResource(AUDIO_DIR + WAV_DIR + USE_MP3_TEST + ".wav") != null)
			{
				FILE_TYPE = AudioFileType.WAV;
			}
			else if (AudioClip.class.getResource(AUDIO_DIR + MP3_DIR + USE_MP3_TEST + ".mp3") != null)
			{
				FILE_TYPE = AudioFileType.MP3;
			}
			else
			{
				FILE_TYPE = AudioFileType.NULL;
			}
			
			ALogger.logA(ALogType.DEBUG, "Disribution uses " + FILE_TYPE + " audio files");
		}

		return FILE_TYPE;
	}
	
	/**
	 * Creates an AudioClip object from the resources within the running jar.
	 * 
	 * @param type the AssetType to assign to the AudioClip
	 * @param audioType the AudioType used for telling the category of audio that this is in
	 * @param path the path of the audio resource
	 * @return an AudioClip from the running jar's resources.
	 */
	public static AudioClip fromResource(AssetType type, String path, AudioType audioType)
	{
		URL url = null;
		
		switch (fileType())
		{
			default:
				return new AudioClip(type, audioType, null);
			case MP3:
				url = AudioClip.class.getResource(AUDIO_DIR + MP3_DIR + path + ".mp3");
				break;
			case WAV:
				url = AudioClip.class.getResource(AUDIO_DIR + WAV_DIR + path + ".wav");
				break;
		}
		
		if (url == null)
		{
			return new AudioClip(type, audioType, null);
		}
		
		AudioInputStream audioIn = null;
		
		try
		{
			audioIn = AudioSystem.getAudioInputStream(url);
		}
		catch (UnsupportedAudioFileException e)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Audio file is an unsupported type", e);
		}
		catch (IOException e)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "I/O Error while loading a clip", e);
		}
		
		Clip clip = null;

		// Sets up the MP3 audio decoder
		if (fileType() == AudioFileType.MP3)
		{
			if (audioIn != null)
			{
				AudioFormat baseFormat = audioIn.getFormat();
				AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

				audioIn = AudioSystem.getAudioInputStream(decodeFormat, audioIn);
				}
		}

		try
		{
			// Previous way of doing it (worked with Java 6 SE)
			// clip = AudioSystem.getClip();

			// This way doesn't crash with OpenJDK 8, but produces no sound, but
			// for some reason only works after it's been compiled into a jar??
			// This way works in OpenJDK 11.
			DataLine.Info info = new DataLine.Info(Clip.class, audioIn.getFormat());
			clip = (Clip) AudioSystem.getLine(info);
		}
		catch (LineUnavailableException e)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Unable to append audio clip", e);
		}

		if (clip != null)
		{
			try
			{
				clip.open(audioIn);
			}
			catch (LineUnavailableException e)
			{
				ClientGame.instance().logger().log(ALogType.CRITICAL, "Audio line unavailable", e);
			}
			catch (IOException e)
			{
				ClientGame.instance().logger().log(ALogType.CRITICAL, "", e);
			}
		}
		
		return new AudioClip(type, audioType, clip);
	}
	
	/**
	 * Updates the volume of all the AudioClips, whether playing or not, from the
	 * Options instance values for volume (range is 0-100).
	 */
	public static void updateVolumesFromOptions()
	{
		for (Asset as : Assets.getAssets())
		{
			if (as instanceof AudioClip)
			{
				AudioClip audio = (AudioClip) as;
				
				// Sets the volume for this based on whether it is defined as music or a sound effect
				int volume = (audio.getAudioType() == AudioType.MUSIC ? Options.instance().musicVolume : (audio.getAudioType() == AudioType.SFX ? Options.instance().sfxVolume : 0));
				
				audio.setVolume(volume / 100F);
			}
		}
	}
	
	private AudioType type;
	private Clip clip;
	
	private int pausedPosition = 0;
	
	/**
	 * An Asset that can playback audio.
	 * 
	 * @param type the AssetType that corresponds with this
	 * @param audioType the AudioType used for telling the category of audio that this is in
	 * @param clip the Clip object containing the audio data for playback
	 */
	public AudioClip(AssetType type, AudioType audioType, Clip clip)
	{
		super(type);
		
		this.type = audioType;
		this.clip = clip;
	}
	
	/**
	 * @deprecated this method will always return null.
	 */
	@Override
	@Deprecated
	public Asset clone()
	{
		// TODO There's no documentation or found information for this, so for now, assume that it will never have to be cloned
		return null;
	}
	
	@Override
	public void dispose()
	{
		if (clip != null)
		{
			clip.stop();
			clip.flush();
			clip.close();
		}
	}
	
	/**
	 * Fades in the audio over a period of time.
	 */
	public void fadeIn()
	{
		if (clip != null)
		{
			Control con = clip.getControl(Type.MASTER_GAIN);
			if (con instanceof FloatControl)
			{
				final FloatControl gain = (FloatControl) con;
				final float initGain = percentageAsDB(0);
				final float targetGain = gain.getValue();
				final float changePerStep = 1F;
				
				gain.setValue(initGain);
				
				new Thread()
				{
					@Override
					public void run()
					{
						if (targetGain > initGain)
						{
							while (gain.getValue() > targetGain)
							{
								try
								{
									Thread.sleep(100);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								
								gain.setValue(gain.getValue() - changePerStep);
							}
						}
						else if (targetGain < initGain)
						{
							while (gain.getValue() < targetGain)
							{
								try
								{
									Thread.sleep(100);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								
								gain.setValue(gain.getValue() + changePerStep);
							}
						}
						
						gain.setValue(targetGain);
					}
				}.start();
			}
		}
	}
	
	// /**
	// * Sets the volume of this immediately.
	// *
	// * @param percentage the new volume of this as a percentage in decimal form (0.0 - 1.0)
	// */
	// public void setVolume(double percentage)
	// {
	// setVolume(percentage, 0L);
	// }
	
	// /**
	// * Sets the volume of this over a period of time.
	// *
	// * @param percentage the new volume of this as a percentage in decimal form (0.0 - 1.0)
	// * @param duration the time in milliseconds that the operation should take
	// */
	// public void setVolume(double percentage, long duration)
	// {
	// if (percentage < 0.0)
	// {
	// percentage = 0.0;
	// }
	// else if (percentage > 1.0)
	// {
	// percentage = 1.0;
	// }
	//
	// setVolume(percentageAsDB(percentage), duration);
	// }
	
	/**
	 * Fades in the audio over a period of time.
	 */
	public void fadeOut()
	{
		if (clip != null)
		{
			Control con = clip.getControl(Type.MASTER_GAIN);
			if (con instanceof FloatControl)
			{
				final FloatControl gain = (FloatControl) con;
				final float initGain = gain.getValue();
				final float targetGain = percentageAsDB(0);
				final float changePerStep = 1F;
				
				new Thread()
				{
					@Override
					public void run()
					{
						if (targetGain > initGain)
						{
							while (gain.getValue() > targetGain)
							{
								try
								{
									Thread.sleep(100);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								
								gain.setValue(gain.getValue() - changePerStep);
							}
						}
						else if (targetGain < initGain)
						{
							while (gain.getValue() < targetGain)
							{
								try
								{
									Thread.sleep(100);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								
								gain.setValue(gain.getValue() + changePerStep);
							}
						}
						
						clip.stop();
						gain.setValue(initGain);
					}
				}.start();
			}
		}
	}
	
	/**
	 * Fades out the audio over a period of time.
	 * 
	 * @param percentage fade to this colume
	 */
	public void fadeToVolume(final double percentage)
	{
		if (clip != null)
		{
			Control con = clip.getControl(Type.MASTER_GAIN);
			if (con instanceof FloatControl)
			{
				final FloatControl gain = (FloatControl) con;
				final float initGain = gain.getValue();
				final float targetGain = percentageAsDB(percentage);
				final float changePerStep = 1F;
				
				new Thread()
				{
					@Override
					public void run()
					{
						if (targetGain > initGain)
						{
							while (gain.getValue() > targetGain)
							{
								try
								{
									Thread.sleep(100);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								
								gain.setValue(gain.getValue() - changePerStep);
							}
						}
						else if (targetGain < initGain)
						{
							while (gain.getValue() < targetGain)
							{
								try
								{
									Thread.sleep(100);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								
								gain.setValue(gain.getValue() + changePerStep);
							}
						}
						
						gain.setValue(targetGain);
					}
				}.start();
			}
		}
	}
	
	// /**
	// * Sets the volume of this over a period of time.
	// *
	// * @param dB the new volume of this in decibels
	// * @param duration the time in milliseconds that the operation should take
	// */
	// public void setVolume(final float dB, final long duration)
	// {
	// if (clip != null)
	// {
	// Control con = clip.getControl(Type.MASTER_GAIN);
	// if (con instanceof FloatControl)
	// {
	// final FloatControl gain = (FloatControl) con;
	//
	// if (duration > 0)
	// {
	// // How long one step in volume change takes in ms
	// final int period = 50;
	//
	// new Thread()
	// {
	// @Override
	// public void run()
	// {
	// // The linear change in dB overall
	// float netChange = dB - gain.getValue();
	// int needed = (int) Math.floor((double) duration / (double) period);
	// // The linear change in dB per step
	// float changePerStep = netChange / needed;
	// int counted = 0;
	// while (counted < needed)
	// {
	// try
	// {
	// Thread.sleep(period);
	// }
	// catch (InterruptedException e)
	// {
	// e.printStackTrace();
	// }
	//
	// gain.setValue(gain.getValue() + changePerStep);
	// counted++;
	// }
	//
	// gain.setValue(dB);
	// }
	// }.start();
	// }
	// else
	// {
	// gain.setValue(dB);
	// }
	// }
	// }
	// }
	
	/**
	 * @return the AudioType of this
	 */
	public AudioType getAudioType()
	{
		return type;
	}
	
	/**
	 * Tells whether or not this is currently being played back.
	 * 
	 * @return is this is playing.
	 */
	public boolean isRunning()
	{
		if (clip == null) return false;
		
		return clip.isActive();
	}
	
	public void pause()
	{
		if (clip == null) return;
		
		pausedPosition = clip.getFramePosition();
		clip.stop();
	}
	
	public float percentageAsDB(double percentage)
	{
		// return (float) percentage * 100;
		// return (float) Math.max(0, (Math.log(percentage) / 20.0));
		return (float) (Math.log(percentage) / Math.log(10.0) * 20.0); // TODO fix this shit
	}
	
	public void resume()
	{
		if (clip == null) return;
		
		clip.setFramePosition(pausedPosition);
		clip.start();
	}
	
	/**
	 * Sets the volume of this immediately.
	 * 
	 * @param percentage the new volume of this in a percentage
	 */
	public void setVolume(float percentage)
	{
		if (clip != null)
		{
			Control con = clip.getControl(Type.MASTER_GAIN);
			if (con instanceof FloatControl)
			{
				final FloatControl gain = (FloatControl) con;
				gain.setValue(percentageAsDB(percentage));
			}
		}
	}
	
	/**
	 * Begins playback of this. If this is already
	 * being played back, it will restart from the beginning.
	 */
	public void start()
	{
		if (clip == null) return;
		
		clip.setFramePosition(0);
		clip.start();
	}
	
	/**
	 * Begins playback of this, looping infinitely until
	 * the method <code>AudioClip.stop()</code> is used.
	 */
	public void startLoop()
	{
		if (clip == null) return;
		
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		clip.start();
	}
	
	/**
	 * Stops playback of this.
	 */
	public void stop()
	{
		if (clip == null) return;
		clip.stop();
	}
}
