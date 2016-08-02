package ca.afroman.util;

public class ArrayUtil
{
	public static byte[] concatByteArrays(byte[]... arrays)
	{
		int total = 0;
		for (byte[] arr : arrays)
		{
			total += arr.length;
		}
		
		byte[] ret = new byte[total];
		
		total = 0;
		
		for (int i = 0; i < arrays.length; i++)
		{
			System.arraycopy(arrays[i], 0, ret, total, arrays[i].length);
			total += arrays[i].length;
		}
		
		return ret;
	}
	
	public static boolean isEmpty(String[] arr)
	{
		return !(arr.length > 0 && (arr.length == 1 ? !arr[0].equals("") : true));
	}
}
