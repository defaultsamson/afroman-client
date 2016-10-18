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
	
	private static boolean initUseMp3 = true;
	private static boolean USE_MP3;
	private static final String USE_MP3_TEST = "music/menu";
	
	public static AudioClip fromResource(AssetType type, AudioType audioType, String path)
	{
		URL url = AudioClip.class.getResource(AUDIO_DIR + (useMp3() ? MP3_DIR : WAV_DIR) + path + (useMp3() ? ".mp3" : ".wav"));
		
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
		
		if (useMp3())
		{
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
		}
		else
		{
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
	
	/**
	 * @return <b>true</b> if using MP3 files, <b>false</b> if using WAV files.
	 */
	public static boolean useMp3()
	{
		if (initUseMp3)
		{
			USE_MP3 = AudioClip.class.getResource(AUDIO_DIR + WAV_DIR + USE_MP3_TEST + ".wav") == null;
			initUseMp3 = false;
			ALogger.logA(ALogType.DEBUG, "Disribution uses " + (USE_MP3 ? "MP3" : "WAV") + " files");
		}
		
		return USE_MP3;
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
		clip.stop();
		clip.flush();
		clip.close();
	}
	
	public AudioType getAudioType()
	{
		return type;
	}
	
	public boolean isRunning()
	{
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
