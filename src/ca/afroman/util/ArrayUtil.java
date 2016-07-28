package ca.afroman.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil
{
	@SuppressWarnings("unchecked")
	public static <T> T[] concatArrays(T[]... arrays)
	{
		List<T> ret = new ArrayList<T>();
		
		for (int i = 0; i < arrays.length; i++)
		{
			for (T e : arrays[i])
			{
				ret.add(e);
			}
		}
		
		return (T[]) ret.toArray();
	}
	
	public static byte[] concatByteArrays(byte[]... arrays)
	{
		int total = 0;
		for (byte[] arr : arrays)
		{
			total += arr.length;
		}
		
		byte[] ret = new byte[total];
		
		total = 0;
		
		// TODO optimise with System.arraycopy
		for (int i = 0; i < arrays.length; i++)
		{
			for (int j = 0; j < arrays[i].length; j++)
			{
				ret[total] = arrays[i][j];
				total++;
			}
		}
		
		return ret;
	}
	
	public static boolean isEmpty(String[] arr)
	{
		return !(arr.length > 0 && (arr.length == 1 ? !arr[0].equals("") : true));
	}
}
