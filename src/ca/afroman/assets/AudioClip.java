package ca.afroman.assets;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.option.Options;

public class AudioClip extends Asset
{
	private static final String AUDIO_DIR = "/audio/";
	private static final String MP3_DIR = "mp3/";
	private static final String WAV_DIR = "wav/";
	
	private static AudioFileType FILE_TYPE = AudioFileType.INVALID;
	private static final String USE_MP3_TEST = "music/menu";
	
	/**
	 * @return which audio files this distribustion is using.
	 */
	public static AudioFileType fileType()
	{
		if (FILE_TYPE == AudioFileType.INVALID)
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
	
	public static AudioClip fromResource(AssetType type, AudioType audioType, String path)
	{
		URL url = null;
		
		switch (fileType())
		{
			default:
				return new AudioClip(type, audioType, null);
				break;
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
		
		switch (fileType())
		{
			default:
				break;
			case MP3:
				if (audioIn != null)
				{
					AudioFormat baseFormat = audioIn.getFormat();
					AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
					
					AudioInputStream decodedAudioIn = AudioSystem.getAudioInputStream(decodeFormat, audioIn);
					
					try
					{
						clip = AudioSystem.getClip();
					}
					catch (LineUnavailableException e)
					{
						ClientGame.instance().logger().log(ALogType.CRITICAL, "Unable to append audio clip", e);
					}
					
					if (clip != null)
					{
						try
						{
							clip.open(decodedAudioIn);
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
				}
				break;
			case WAV:
				try
				{
					clip = AudioSystem.getClip();
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
				break;
		}
		
		return new AudioClip(type, audioType, clip);
	}
	
	/**
	 * Updates the volume of all the AudioClips, whether playing or not, from the
	 * Options instance values for volume (range is 0-100.
	 */
	public static void updateVolumesFromOptions()
	{
		for (Asset as : Assets.getAssets())
		{
			if (as instanceof AudioClip)
			{
				AudioClip audio = (AudioClip) as;
				
				int volume = (audio.getAudioType() == AudioType.MUSIC ? Options.instance().musicVolume : (audio.getAudioType() == AudioType.SFX ? Options.instance().sfxVolume : 0));
				
				audio.setVolume(volume / 100D);
			}
		}
	}
	
	private AudioType type;
	
	private Clip clip;
	
	public AudioClip(AssetType type, AudioType audioType, Clip clip)
	{
		super(type);
		
		this.type = audioType;
		this.clip = clip;
	}
	
	@Override
	public Asset clone()
	{
		// TODO Maybe? hHHHHHHH
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
	
	public AudioType getAudioType()
	{
		return type;
	}
	
	public boolean isRunning()
	{
		if (clip == null) return false;
		
		return clip.isActive();
	}
	
	public void setVolume(double percentage)
	{
		setVolume(percentage, 0L);
	}
	
	public void setVolume(double percentage, long duration)
	{
		if (percentage < 0.0)
		{
			percentage = 0.0;
		}
		else if (percentage > 1.0)
		{
			percentage = 1.0;
		}
		
		float dB = (float) (Math.log(percentage) / Math.log(10.0) * 20.0);
		setVolume(dB, duration);
	}
	
	public void setVolume(float dB)
	{
		setVolume(dB, 0L);
	}
	
	public void setVolume(final float dB, final long duration)
	{
		if (clip != null)
		{
			Control con = clip.getControl(Type.MASTER_GAIN);
			if (con instanceof FloatControl)
			{
				final FloatControl gain = (FloatControl) con;
				
				if (duration > 0)
				{
					// How long one step in volume change takes in ms
					final int period = 50;
					
					new Thread()
					{
						@Override
						public void run()
						{
							// The linear change in dB overall
							float netChange = dB - gain.getValue();
							int needed = (int) Math.floor((double) duration / (double) period);
							// The linear change in dB per step
							float changePerStep = netChange / needed;
							int counted = 0;
							while (counted < needed)
							{
								try
								{
									Thread.sleep(period);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								
								gain.setValue(gain.getValue() + changePerStep);
								counted++;
							}
							
							gain.setValue(dB);
						}
					}.start();
				}
				else
				{
					gain.setValue(dB);
				}
			}
		}
	}
	
	public void start()
	{
		if (clip == null) return;
		
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void startLoop()
	{
		if (clip == null) return;
		
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		clip.start();
	}
	
	public void stop()
	{
		if (clip == null) return;
		clip.stop();
	}
}
