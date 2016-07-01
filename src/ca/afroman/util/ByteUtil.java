package ca.afroman.util;

import java.nio.ByteBuffer;

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
}
