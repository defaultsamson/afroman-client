package ca.afroman.assets;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;
import ca.afroman.option.Options;

public class AudioClip extends Asset
{
	private static boolean initUseMp3 = true;
	private static boolean USE_MP3;
	private static final String AUDIO_DIR = "/audio/";
	private static final String MP3_DIR = "mp3/";
	private static final String WAV_DIR = "wav/";
	
	public static AudioClip fromResource(AssetType type, String path)
	{
		if (initUseMp3)
		{
			USE_MP3 = AudioClip.class.getResource(AUDIO_DIR + WAV_DIR + path + ".wav") == null;
			initUseMp3 = false;
			ClientGame.instance().logger().log(ALogType.DEBUG, "Loading audio using " + (USE_MP3 ? "MP3" : "WAV") + " files");
		}
		
		URL url = AudioClip.class.getResource(AUDIO_DIR + (USE_MP3 ? MP3_DIR : WAV_DIR) + path + (USE_MP3 ? ".mp3" : ".wav"));
		
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
		
		if (USE_MP3)
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
		
		return new AudioClip(type, clip);
	}
	
	private Clip clip;
	
	public AudioClip(AssetType type, Clip clip)
	{
		super(type);
		
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
	
	public void start()
	{
		if (Options.instance().enableMusic)
		{
			if (clip == null) return;
			
			clip.setFramePosition(0);
			
			clip.start();
		}
	}
	
	public void startLoop()
	{
		if (Options.instance().enableMusic)
		{
			if (clip == null) return;
			
			clip.setFramePosition(0);
			clip.loop(200);
			clip.start();
		}
	}
	
	public void stop()
	{
		if (clip == null) return;
		clip.stop();
	}
}
