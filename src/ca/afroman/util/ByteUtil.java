package ca.afroman.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;

public class ByteUtil
{
	public static final int SHORT_BYTE_COUNT = 2;
	public static final int INT_BYTE_COUNT = 4;
	public static final int DOUBLE_BYTE_COUNT = 8;
	
	public static byte[] shortAsBytes(short num)
	{
		return ByteBuffer.allocate(SHORT_BYTE_COUNT).putShort(num).array();
	}
	
	public static short shortFromBytes(byte[] bytes)
	{
		if (bytes.length != SHORT_BYTE_COUNT)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Converting a byte array to a short requires " + SHORT_BYTE_COUNT + " bytes (was provided with " + bytes.length + ")");
			return 0;
		}
		
		return ByteBuffer.wrap(bytes).getShort();
	}
	
	/**
	 * Converts a double to a byte array for sending over a network
	 * 
	 * @param num the double (must be within bounds of LARGEST_DOUBLE)
	 * @return the double as sendable bytes
	 */
	public static byte[] doubleAsBytes(double num)
	{
		return ByteBuffer.allocate(DOUBLE_BYTE_COUNT).putDouble(num).array();
	}
	
	public static double doubleFromBytes(byte[] bytes)
	{
		if (bytes.length != DOUBLE_BYTE_COUNT)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Converting a byte array to a double requires " + DOUBLE_BYTE_COUNT + " bytes (was provided with " + bytes.length + ")");
			return 0.0;
		}
		
		return ByteBuffer.wrap(bytes).getDouble();
	}
	
	/**
	 * Converts a double to a byte array for sending over a network
	 * 
	 * @param num the double (must be within bounds of LARGEST_DOUBLE)
	 * @return the double as sendable bytes
	 */
	public static byte[] intAsBytes(int num)
	{
		return ByteBuffer.allocate(INT_BYTE_COUNT).putInt(num).array();
	}
	
	public static int intFromBytes(byte[] bytes)
	{
		if (bytes.length != INT_BYTE_COUNT)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Converting a byte array to an integer requires " + INT_BYTE_COUNT + " bytes (was provided with " + bytes.length + ")");
			return 0;
		}
		
		return ByteBuffer.wrap(bytes).getInt();
	}
	
	/**
	 * Extracts bytes from a ByteBuffer until it comes across the signal.
	 * 
	 * @param buf
	 * @param signal an array of bytes which will tell this to stop searching.
	 * @return
	 */
	public static byte[] extractBytes(ByteBuffer buf, byte... signal)
	{
		byte[] ret = null;
		
		// Finds how many bytes there are from the current position until the signal
		// Then creates an object array based on how many bytes there are per obejct
		for (int i = 0; buf.position() + i < buf.limit(); i++)
		{
			if (isSignal(buf.array(), buf.position() + i, signal))
			{
				ret = new byte[i]; // i / 1
				break;
			}
		}
		
		// Populates the byte array
		if (ret != null)
		{
			for (int i = 0; i < ret.length; i++)
			{
				ret[i] = buf.get();
			}
		}
		
		buf.position(buf.position() + signal.length);
		
		return ret;
	}
	
	/**
	 * Extracts integers from a ByteBuffer until it comes across the signal.
	 * 
	 * @param buf
	 * @param signal an array of bytes which will tell this to stop searching.
	 * @return
	 */
	public static int[] extractIntArray(ByteBuffer buf, byte... signal)
	{
		int[] ret = null;
		
		// Finds how many bytes there are from the current position until the signal
		// Then creates an object array based on how many bytes there are per obejct
		for (int i = 0; buf.position() + i < buf.limit(); i++)
		{
			if (isSignal(buf.array(), buf.position() + i, signal))
			{
				ret = new int[i / INT_BYTE_COUNT];
				break;
			}
		}
		
		// Populates the byte array
		if (ret != null)
		{
			for (int i = 0; i < ret.length; i++)
			{
				ret[i] = buf.getInt();
			}
		}
		
		buf.position(buf.position() + signal.length);
		
		return ret;
	}
	
	/**
	 * Extracts integers from a ByteBuffer until it comes across the signal.
	 * 
	 * @param buf
	 * @param signal an array of bytes which will tell this to stop searching.
	 * @return
	 */
	public static List<Integer> extractIntList(ByteBuffer buf, byte... signal)
	{
		List<Integer> ret = new ArrayList<Integer>();
		int count = 0;
		
		// Finds how many bytes there are from the current position until the signal
		// Then creates an object array based on how many bytes there are per obejct
		for (int i = 0; buf.position() + i < buf.limit(); i++)
		{
			if (isSignal(buf.array(), buf.position() + i, signal))
			{
				count = i / INT_BYTE_COUNT;
				break;
			}
		}
		
		// Populates the byte array
		if (ret != null)
		{
			for (int i = 0; i < count; i++)
			{
				ret.add(buf.getInt());
			}
		}
		
		buf.position(buf.position() + signal.length);
		
		return ret;
	}
	
	/**
	 * Extracts doubles from a ByteBuffer until it comes across the signal.
	 * 
	 * @param buf
	 * @param signal an array of bytes which will tell this to stop searching.
	 * @return
	 */
	public static double[] extractDoubleArray(ByteBuffer buf, byte... signal)
	{
		double[] ret = null;
		
		// Finds how many bytes there are from the current position until the signal
		// Then creates an object array based on how many bytes there are per obejct
		for (int i = 0; buf.position() + i < buf.limit(); i++)
		{
			if (isSignal(buf.array(), buf.position() + i, signal))
			{
				ret = new double[i / DOUBLE_BYTE_COUNT];
				break;
			}
		}
		
		// Populates the byte array
		if (ret != null)
		{
			for (int i = 0; i < ret.length; i++)
			{
				ret[i] = buf.getDouble();
			}
		}
		
		buf.position(buf.position() + signal.length);
		
		return ret;
	}
	
	/**
	 * Extracts doubles from a ByteBuffer until it comes across the signal.
	 * 
	 * @param buf
	 * @param signal an array of bytes which will tell this to stop searching.
	 * @return
	 */
	public static List<Double> extractDoubleList(ByteBuffer buf, byte... signal)
	{
		List<Double> ret = new ArrayList<Double>();
		int count = 0;
		
		// Finds how many bytes there are from the current position until the signal
		// Then creates an object array based on how many bytes there are per obejct
		for (int i = 0; buf.position() + i < buf.limit(); i++)
		{
			if (isSignal(buf.array(), buf.position() + i, signal))
			{
				count = i / DOUBLE_BYTE_COUNT;
				break;
			}
		}
		
		// Populates the byte array
		if (ret != null)
		{
			for (int i = 0; i < count; i++)
			{
				ret.add(buf.getDouble());
			}
		}
		
		buf.position(buf.position() + signal.length);
		
		return ret;
	}
	
	/**
	 * Extracts shorts from a ByteBuffer until it comes across the signal.
	 * 
	 * @param buf
	 * @param signal an array of bytes which will tell this to stop searching.
	 * @return
	 */
	public static short[] extractShortArray(ByteBuffer buf, byte... signal)
	{
		short[] ret = null;
		
		// Finds how many bytes there are from the current position until the signal
		// Then creates an object array based on how many bytes there are per obejct
		for (int i = 0; buf.position() + i < buf.limit(); i++)
		{
			if (isSignal(buf.array(), buf.position() + i, signal))
			{
				ret = new short[i / SHORT_BYTE_COUNT];
				break;
			}
		}
		
		// Populates the byte array
		if (ret != null)
		{
			for (int i = 0; i < ret.length; i++)
			{
				ret[i] = buf.getShort();
			}
		}
		
		buf.position(buf.position() + signal.length);
		
		return ret;
	}
	
	/**
	 * Extracts short from a ByteBuffer until it comes across the signal.
	 * 
	 * @param buf
	 * @param signal an array of bytes which will tell this to stop searching.
	 * @return
	 */
	public static List<Short> extractShortList(ByteBuffer buf, byte... signal)
	{
		List<Short> ret = new ArrayList<Short>();
		int count = 0;
		
		// Finds how many bytes there are from the current position until the signal
		// Then creates an object array based on how many bytes there are per obejct
		for (int i = 0; buf.position() + i < buf.limit(); i++)
		{
			if (isSignal(buf.array(), buf.position() + i, signal))
			{
				count = i / SHORT_BYTE_COUNT;
				break;
			}
		}
		
		// Populates the byte array
		if (ret != null)
		{
			for (int i = 0; i < count; i++)
			{
				ret.add(buf.getShort());
			}
		}
		
		buf.position(buf.position() + signal.length);
		
		return ret;
	}
	
	public static boolean isSignal(byte[] arr, int pos, byte... signal)
	{
		// If the signal will make it go out of bounds
		if (signal.length > arr.length - pos) return false;
		
		for (int i = 0; i < signal.length; i++)
		{
			if (arr[pos + i] != signal[i]) return false;
		}
		
		return true;
	}
}
