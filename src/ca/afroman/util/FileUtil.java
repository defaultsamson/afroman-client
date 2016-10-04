package ca.afroman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import ca.afroman.client.ClientGame;

public class FileUtil
{
	private static HashMap<String, String> replacements;
	static
	{
		replacements = new HashMap<String, String>();
		replacements.put("\\", "/");
		replacements.put("//", "/");
		replacements.put(":", "");
		replacements.put("*", "");
		replacements.put("?", "");
		replacements.put("\"", "");
		replacements.put("<", "");
		replacements.put(">", "");
		replacements.put("|", "");
	}
	
	public static void copyFile(File source, File dest) throws IOException
	{
		InputStream is = null;
		OutputStream os = null;
		try
		{
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0)
			{
				os.write(buffer, 0, length);
			}
		}
		finally
		{
			is.close();
			os.close();
		}
	}
	
	public static void delete(File file) throws IOException
	{
		if (!file.delete())
		{
			throw new IOException();
		}
	}
	
	/**
	 * Gets a file instance from a resource of the running program.
	 * 
	 * @param path the path to the resource. e.g. "/assets/levels/sauce.txt"
	 * @return the resource as a File.
	 */
	public static File fileFromResource(String path)
	{
		path = formatPath(path);
		String name;
		{
			String[] split = path.split("/");
			name = split[split.length - 1];
		}
		
		// Loads the file
		InputStream in = FileUtil.class.getResourceAsStream(path);
		
		File tempFile = null;
		
		try
		{
			// Puts the file's contents into a temp file
			tempFile = File.createTempFile(name, null);
			FileOutputStream out = new FileOutputStream(tempFile);
			
			byte[] buffer = new byte[1024];
			
			int size = 0;
			while ((size = in.read(buffer)) > -1)
			{
				out.write(buffer, 0, size);
			}
			
			in.close();
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		return tempFile;
	}
	
	/**
	 * Formats a path to fit the standards.
	 * 
	 * @param path
	 * @return a formatted path.
	 */
	public static String formatPath(String path)
	{
		for (Entry<String, String> entry : replacements.entrySet())
		{
			while (path.contains(entry.getKey()))
			{
				path = path.replace(entry.getKey(), entry.getValue());
			}
		}
		return path;
	}
	
	public static FileType getFileType(File file)
	{
		return getFileType(file.getName());
	}
	
	public static FileType getFileType(String fileName)
	{
		String[] spl = fileName.split(".");
		
		if (!ArrayUtil.isEmpty(spl))
		{
			String extension = spl[spl.length - 1].toLowerCase();
			
			for (FileType file : FileType.values())
			{
				if (extension.equalsIgnoreCase(file.getExtension()))
				{
					return file;
				}
			}
		}
		
		return FileType.INVALID;
	}
	
	public static File getRunningJar() throws FileNotFoundException
	{
		String path = ClientGame.class.getResource(ClientGame.class.getSimpleName() + ".class").getFile();
		if (path.startsWith("/"))
		{
			throw new FileNotFoundException("This is not a jar file: \n" + path);
		}
		path = ClassLoader.getSystemClassLoader().getResource(path).getFile();
		
		return new File(path.substring(0, path.lastIndexOf('!')));
	}
	
	/**
	 * Takes all the lines from a file as Strings.
	 * 
	 * @param file the file to read
	 * @return the lines read.
	 */
	public static List<String> readAllLines(File file)
	{
		List<String> toReturn = new ArrayList<String>();
		Scanner fileScanner;
		try
		{
			fileScanner = new Scanner(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		
		while (fileScanner.hasNextLine())
		{
			toReturn.add(fileScanner.nextLine());
		}
		
		fileScanner.close();
		
		return toReturn;
	}
	
	public static void writeLines(List<String> lines, File file)
	{
		if (!file.exists()) try
		{
			file.createNewFile();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		PrintWriter pw;
		
		try
		{
			pw = new PrintWriter(file);
			
			for (String s : lines)
			{
				pw.println(s);
			}
			
			pw.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
