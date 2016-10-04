package ca.afroman.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;

public class UpdateUtil
{
	public static final String SERVER_VERSION = "server.txt";
	public static final String RAW_LOCATION = "https://github.com/qwertysam/afroman-client/version.txt";
	
	public static long currentVersion;
	public static URL serverLocation;
	
	public UpdateUtil ()
	{
		currentVersion = VersionUtil.SERVER_TEST_VERSION;
		
		try 
		{
			serverLocation = new URL(RAW_LOCATION);
			try
			{
				ReadableByteChannel rbc = Channels.newChannel(serverLocation.openStream());
				FileOutputStream fos = new FileOutputStream("server.txt");
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
			}
			catch (IOException e)
			{
				ALogger.logA(ALogType.WARNING, "Failed to capture file at: " + serverLocation);
			}
		} 
		catch (IOException e) 
		{
			ALogger.logA(ALogType.WARNING, "Failed to create URL from provided string: " + RAW_LOCATION);
		}
		
		File file = new File(SERVER_VERSION);
		if (file.exists())
		{
			List<String> lines = FileUtil.readAllLines(file);
			
			for (String line : lines)
			{
				try
				{
					Long subject = Long.parseLong(line);
					if (subject.equals(currentVersion))
					{
						newVersion();
					}
					else if (subject > currentVersion)
					{
						futureVersion();
					}
					else
					{
						sameVersion();
					}
				}
				catch (Exception e)
				{
					ALogger.logA(ALogType.WARNING, "Failed to read line: " + line);
				}
			}
		}
		
	}
	
	public static void newVersion ()
	{
		
	}
	
	public static void sameVersion ()
	{
		
	}
	
	public static void futureVersion ()
	{
		
	}
}
