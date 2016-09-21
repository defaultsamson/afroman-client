package ca.afroman.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

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
