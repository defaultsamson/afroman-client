package ca.afroman.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	public static final String RAW_BUILD = "https://github.com/qwertysam/afroman-client/releases/download";
	public static final String JAR_FILENAME = "AfroMan-mp3-o.jar";
	public static final String EXE_FILENAME = "AfroMan-mp3.exe";
	public static final String NEW_UPDATE = "/new/";
	
	public static long currentVersion;
	public static long serverVersion;
	public static URL serverLocation;
	
	private static File self;
	private static FileType runningFile = FileType.INVALID;
	
	/**
	 * Downloads a file from a URL.
	 * 
	 * @param location the string version of the URL to download from.
	 * @param fileName the name that the download should take.
	 * @return the downloaded file.
	 */
	public static File download(String location, String fileName)
	{
		URL downloadLocation = null;
		try
		{
			downloadLocation = new URL(location);
			ReadableByteChannel rbc = Channels.newChannel(downloadLocation.openStream());
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to download from URL: " + location);
			return null;
		}
		
		return new File(fileName);
	}
	
	/**
	 * Downloads the text file indicating the latest release version.
	 */
	public static void grabVersion()
	{
		try
		{
			download(RAW_LOCATION, SERVER_VERSION);
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to capture file at: " + RAW_LOCATION);
		}
	}
	
	/**
	 * Constructs a URL to grab a executable from the repo server, then downloads it into a new folder.
	 * 
	 * @return the file it has downloaded.
	 */
	private static File newExe()
	{
		ALogger.logA(ALogType.DEBUG, "Newer version found on server repository, downloading...");
		URL buildLocation = null;
		
		try
		{
			String displayVersion = VersionUtil.toString(serverVersion);
			download(RAW_BUILD + "/" + displayVersion + "/" + EXE_FILENAME, NEW_UPDATE + EXE_FILENAME);
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to create repository URL: " + buildLocation);
			return null;
		}
		
		return new File(EXE_FILENAME);
	}
	
	/**
	 * Constructs a URL to grab a jar from the repo server, then downloads it into a new folder.
	 * 
	 * @return the file it has downloaded.
	 */
	private static File newJar()
	{
		ALogger.logA(ALogType.DEBUG, "Newer version found on server repository, downloading...");
		URL buildLocation = null;
		try
		{
			String displayVersion = VersionUtil.toString(serverVersion);
			download(RAW_BUILD + "/" + displayVersion + "/" + JAR_FILENAME, NEW_UPDATE + JAR_FILENAME);
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to create repository URL: " + buildLocation);
			return null;
		}
		
		return new File(JAR_FILENAME);
	}
	
	/**
	 * Replaces one file with another.
	 * 
	 * @param from source file to move.
	 * @param to destination file to remove.
	 */
	public static void replace(String from, String to) // Heckign wicked kill file and put replacement laad
	{
		try
		{
			FileUtil.copyFile(new File(from), new File(to));
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to copy " + from + " to " + to, e);
		}
		// Not available using Java 6
		// Path source = Paths.get(from);
		// Path target = Paths.get(to);
		//
		// try
		// {
		// Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		// }
		// catch (Exception e)
		// {
		// ALogger.logA(ALogType.WARNING, "Failed to copy " + source + " to " + target);
		// }
	}
	
	/**
	 * Checks for, and if there are, updates the game.
	 */
	public static void update()
	{
		currentVersion = VersionUtil.SERVER_TEST_VERSION;
		grabVersion();
		
		try
		{
			self = FileUtil.getRunningJar();
		}
		catch (FileNotFoundException e)
		{
			ALogger.logA(ALogType.WARNING, "Cannot find self!");
		}
		
		runningFile = FileUtil.getFileType(self);
		versionCheck();
	}
	
	/**
	 * Checks all lines in the server version file (should be only one),
	 * and tests if any are greater than this program's version.
	 */
	public static void versionCheck()
	{
		File file = new File(SERVER_VERSION);
		if (file.exists())
		{
			List<String> lines = FileUtil.readAllLines(file);
			
			for (String line : lines)
			{
				try
				{
					Long subject = Long.parseLong(line);
					if (subject > currentVersion)
					{
						serverVersion = subject;
						
						switch (runningFile)
						{
							case INVALID:
								break;
							case EXE:
								newExe();
								replace(NEW_UPDATE + EXE_FILENAME, self.getName());
								break;
							case JAR:
								newJar();
								replace(NEW_UPDATE + JAR_FILENAME, self.getName());
								break;
						}
					}
					else if (subject.equals(currentVersion))
					{
						ALogger.logA(ALogType.DEBUG, "Current version is same as server's, stopping...");
					}
					else
					{
						ALogger.logA(ALogType.DEBUG, "Current version is newer than server's, server update recommended.");
					}
				}
				catch (Exception e)
				{
					ALogger.logA(ALogType.WARNING, "Failed to read line: " + line);
				}
			}
		}
	}
}
