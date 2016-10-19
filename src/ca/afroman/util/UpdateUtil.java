package ca.afroman.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import ca.afroman.assets.AudioClip;
import ca.afroman.assets.AudioFileType;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;

public class UpdateUtil
{
	private static final String SERVER_VERSION = "server.txt";
	private static final String RAW_LOCATION = "https://raw.githubusercontent.com/qwertysam/afroman-client/master/version.txt";
	private static final String RAW_BUILD = "https://github.com/qwertysam/afroman-client/releases/download";
	private static final String FILE_HEADER = "AfroMan-";
	private static final String MP3_SUBHEADER = "mp3";
	private static final String WAV_SUBHEADER = "wav";
	private static final String JAR_EXTENSION = ".jar";
	private static final String NEW_HEADER = "!";
	private static final String EXE_EXTENSION = ".exe";
	
	public static long serverVersion = 0L;
	
	private static String selfName = "";
	private static FileType runningFile = FileType.INVALID;
	private static String destFile;
	private static String newFile;
	
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
			ALogger.logA(ALogType.WARNING, "Failed to download from URL: " + location, e);
			return null;
		}
		
		return new File(fileName);
	}
	
	/**
	 * Downloads the text file indicating the latest release version.
	 */
	private static long grabVersion()
	{
		try
		{
			URL url = new URL(RAW_LOCATION);
			Scanner sc = new Scanner(url.openStream());
			if (sc.hasNextLong())
			{
				long toRet = sc.nextLong();
				sc.close();
				return toRet;
			}
			else
			{
				ALogger.logA(ALogType.WARNING, "Server file does not have a long");
				sc.close();
				return 0L;
			}
		}
		catch (IOException e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to read website file", e);
			return 0L;
		}
		
		// Old method, requires downloading of the file
		// try
		// {
		// download(RAW_LOCATION, SERVER_VERSION);
		//
		// File file = new File(SERVER_VERSION);
		// if (file.exists())
		// {
		// List<String> lines = FileUtil.readAllLines(file);
		//
		// for (String line : lines)
		// {
		// try
		// {
		// serverVersion = Long.parseLong(line);
		// }
		// catch (Exception e)
		// {
		// ALogger.logA(ALogType.WARNING, "Failed to read line: " + line, e);
		// }
		// }
		// }
		// }
		// catch (Exception e)
		// {
		// ALogger.logA(ALogType.WARNING, "Failed to capture file at: " + RAW_LOCATION, e);
		// }
	}
	
	/**
	 * Constructs a URL to grab a jar from the repo server, then downloads it into a new folder.
	 * 
	 * @return the file it has downloaded.
	 */
	private static File newVersion(AudioFileType audio, FileType type)
	{
		ALogger.logA(ALogType.DEBUG, "Newer version found on server repository, downloading...");
		URL buildLocation = null;
		destFile = FILE_HEADER;
		
		switch (audio)
		{
			case WAV:
				destFile += WAV_SUBHEADER;
				break;
			case MP3:
			default:
				destFile += MP3_SUBHEADER;
				break;
		}
		
		switch (type)
		{
			case EXE:
				destFile += EXE_EXTENSION;
				break;
			case JAR:
			default:
				destFile += JAR_EXTENSION;
				break;
		}
		
		newFile = NEW_HEADER + destFile;
		
		try
		{
			String displayVersion = VersionUtil.toString(serverVersion);
			download(RAW_BUILD + "/" + displayVersion + "/" + destFile, newFile);
		}
		catch (Exception e)
		{
			ALogger.logA(ALogType.WARNING, "Failed to create repository URL: " + buildLocation, e);
			return null;
		}
		return new File(newFile);
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
		
		File old = new File(NEW_HEADER);
		if (old.exists())
		{
			try
			{
				FileUtil.delete(old);
			}
			catch (IOException e)
			{
				ALogger.logA(ALogType.WARNING, "Failed to delete" + newFile, e);
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
			
			File newVersionFile = newVersion(AudioClip.fileType(), runningFile);
			
			if (newVersionFile != null)
			{
				replace(newFile, selfName);
				return true;
			}
			else
			{
				return false;
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
		serverVersion = grabVersion();
		ALogger.logA(ALogType.DEBUG, "Server Version: " + serverVersion);
		ALogger.logA(ALogType.DEBUG, "Client Version: " + VersionUtil.FULL_VERSION);
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
