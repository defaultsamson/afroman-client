package ca.afroman.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileIO
{
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
		
		return toReturn;
	}
}
