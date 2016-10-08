package ca.afroman.util;

import java.io.File;
import java.io.FileNotFoundException;
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
	public static final String RAW_LOCATION = "https://raw.githubusercontent.com/qwertysam/afroman-client/master/version.txt";
	public static final String RAW_BUILD = "https://github.com/qwertysam/afroman-client/releases/download";
	public static final String JAR_FILENAME = "AfroMan-mp3.jar";
	public static final String JAR_NEWNAME = "AfroMan-new.jar";
	public static final String EXE_FILENAME = "AfroMan-mp3.exe";
	public static final String EXE_NEWNAME = "AfroMan-new.exe";
	public static final String NEW_UPDATE = "/new/";
	
	public static long serverVersion = 0L;
	
	public static String selfName = "";
	public static FileType runningFile = FileType.INVALID;
	
	public static void applyUpdate()
	{
		applyUpdate(false);
	}
	
	public static void applyUpdate(boolean commandLine)
	{
		try
		{
			// Stops the client properly when being closed
			boolean successful = UpdateUtil.update();
			
			UpdateUtil.purgeOld();
			
			if (successful)
			{
				System.out.println("Successfully updated");
				// Relaunches the game
				if (!commandLine)
				{
					switch (UpdateUtil.runningFile)
					{
						case EXE:// TODO test on windows
							Runtime.getRuntime().exec(UpdateUtil.selfName);
							break;
						case JAR: // TODO test on mac
							Runtime.getRuntime().exec("java -jar " + UpdateUtil.selfName);
							break;
						default:
							break;
					}
				}
			}
			else
			{
				System.exit(1);
			}
			
			System.exit(0);
		}
		catch (Exception e)
		{
			System.out.println("Failed to update");
			System.exit(2);
		}
	}
	
	/**
	 * Downloads a file from a URL.
	 * 
	 * @param location the string version of the URL to download from.
	 * @param fileName the name that the download should take.
	 * @return the downloaded file.
	 */
	private static File download(String location, String fileName)
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
			ALogger.logA(ALogType.WARNING, "Failed to download from URL: " + location, e);
			return null;
		}
		
		return new File(fileName);
	}
	
	/**
	 * Downloads the text file indicating the latest release version.
	 */
	private static void grabVersion()
	{
		try
		{
			download(RAW_LOCATION, SERVER_VERSION);
			
			File file = new File(SERVER_VERSION);
			if (file.exists())
			{
				List<String> lines = FileUtil.readAllLines(file);
				
				for (String line : lines)
				{
					try
					{
						serverVersion = Long.parseLong(line);
					}
					catch (Exception e)
					{
						ALogger.logA(ALogType.WARNING, "Failed to read line: " + line, e);
					}
				}
			}
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to capture file at: " + RAW_LOCATION, e);
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
			download(RAW_BUILD + "/" + displayVersion + "/" + EXE_FILENAME, EXE_NEWNAME);
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to create repository URL: " + buildLocation, e);
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
			download(RAW_BUILD + "/" + displayVersion + "/" + JAR_FILENAME, JAR_NEWNAME);
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to create repository URL: " + buildLocation, e);
			return null;
		}
		
		return new File(JAR_FILENAME);
	}
	
	/**
	 * Deletes any files that are involved with updating, if they exist.
	 */
	public static void purgeOld()
	{
		File version = new File(SERVER_VERSION);
		if (version.exists())
		{
			try
			{
				FileUtil.delete(version);
			}
			catch (IOException e)
			{
				ALogger.logA(ALogType.WARNING, "Failed to delete " + SERVER_VERSION, e);
			}
		}
		
		File jar = new File(JAR_NEWNAME);
		if (jar.exists())
		{
			try
			{
				FileUtil.delete(jar);
			}
			catch (IOException e)
			{
				ALogger.logA(ALogType.WARNING, "Failed to delete" + JAR_NEWNAME, e);
			}
		}
		
		File exe = new File(EXE_NEWNAME);
		if (exe.exists())
		{
			try
			{
				FileUtil.delete(exe);
			}
			catch (IOException e)
			{
				ALogger.logA(ALogType.WARNING, "Failed to delete" + EXE_NEWNAME, e);
			}
		}
	}
	
	/**
	 * Replaces one file with another.
	 * 
	 * @param from source file to move.
	 * @param to destination file to remove.
	 */
	private static void replace(String from, String to) // Heckign wicked kill file and put replacement laad
	{
		try
		{
			FileUtil.copyFile(from, to);
		}
		catch (IOException e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to copy " + from + " to " + to, e);
		}
	}
	
	private static boolean update()
	{
		try
		{
			File self = FileUtil.getRunningJar();
			
			runningFile = FileUtil.getFileType(self);
			
			selfName = self.getName();
			switch (runningFile)
			{
				case INVALID:
					ALogger.logA(ALogType.DEBUG, "Program is not run from file, refusing to update.");
					return false;
				case EXE:
					newExe();
					replace(EXE_NEWNAME, selfName);
					return true;
				case JAR:
					newJar();
					replace(JAR_NEWNAME, selfName);
					return true;
			}
		}
		catch (FileNotFoundException e)
		{
			ALogger.logA(ALogType.WARNING, "Cannot find self!", e);
		}
		
		return false;
	}
	
	/**
	 * Checks for, and if there are, updates the game.
	 */
	public static boolean updateQuery()
	{
		purgeOld();
		grabVersion();
		purgeOld();
		return versionCheck(VersionUtil.FULL_VERSION);
	}
	
	/**
	 * Tests if the server version is greater than this program's version.
	 */
	private static boolean versionCheck(long currentVersion)
	{
		return serverVersion > currentVersion;
	}
}
