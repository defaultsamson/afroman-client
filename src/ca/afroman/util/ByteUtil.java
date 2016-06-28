package ca.afroman.util;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;

public class ByteUtil
{
	private static final int BYTE_MODDER = 128;
	private static final int BYTE_MODDER_SQUARED = 128 * 128;
	private static final int BYTE_MODDER_CUBED = 128 * 128 * 128;
	private static final int BYTE_MODDER_QUAD = 128 * 128 * 128 * 128;
	private static final int BYTE_MODDER_QUINT = 128 * 128 * 128 * 128 * 128;
	
	public static final double LARGEST_DOUBLE = 268435455; // 128^4 - 1
	public static final double SMALLEST_DOUBLE = -268435456; // - 128^4
	
	public static final double LARGEST_INT = Integer.MAX_VALUE;
	public static final double SMALLEST_INT = Integer.MIN_VALUE;
	
	public static byte[] shortAsBytes(short num)
	{
		byte[] toRet = new byte[] { 0, 0 };
		
		toRet[1] = (byte) (num % 256);
		toRet[0] = (byte) ((num - toRet[1]) / 256);
		
		return toRet;
	}
	
	public static short shortFromBytes(byte[] bytes)
	{
		if (bytes.length != 2)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Converting a byte array to a short requires 2 bytes (was only provided with " + bytes.length + ")");
			return 0;
		}
		
		return (short) ((bytes[0] * 256) + (bytes[1]));
	}
	
	/**
	 * Converts a double to a byte array for sending over a network
	 * 
	 * @param num the double (must be within bounds of LARGEST_DOUBLE)
	 * @return the double as sendable bytes
	 */
	public static byte[] doubleAsBytes(double num)
	{
		if (num > LARGEST_DOUBLE || num < SMALLEST_DOUBLE)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Double " + num + " cannot be parsed as a byte array because it is out of bounds");
			return null;
		}
		
		int mainNum = (int) Math.floor(num);
		int decimal = (int) Math.round((num - mainNum) * 10000);
		
		byte[] toRet = new byte[] { 0, 0, 0, 0, 0, 0 };
		
		// Calculates main bytes
		if (mainNum != 0)
		{
			toRet[3] = (byte) (mainNum % BYTE_MODDER);
			mainNum -= toRet[3];
		}
		
		if (mainNum != 0)
		{
			toRet[2] = (byte) ((mainNum % BYTE_MODDER_SQUARED) / BYTE_MODDER);
			mainNum -= (toRet[2] * BYTE_MODDER);
		}
		
		if (mainNum != 0)
		{
			toRet[1] = (byte) ((mainNum % BYTE_MODDER_CUBED) / BYTE_MODDER_SQUARED);
			mainNum -= (toRet[1] * BYTE_MODDER_SQUARED);
		}
		
		if (mainNum != 0)
		{
			toRet[0] = (byte) ((mainNum % BYTE_MODDER_QUAD) / BYTE_MODDER_CUBED);
		}
		
		// Calculates the decimal points
		toRet[5] = (byte) (decimal % BYTE_MODDER);
		decimal -= toRet[3];
		
		if (decimal != 0)
		{
			toRet[4] = (byte) ((decimal % BYTE_MODDER_SQUARED) / BYTE_MODDER);
		}
		
		return toRet;
	}
	
	public static double doubleFromBytes(byte[] bytes)
	{
		if (bytes.length != 6)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Converting a byte array to a double requires 6 bytes (was only provided with " + bytes.length + ")");
			return 0.0;
		}
		
		double toReturn = bytes[0] * BYTE_MODDER_CUBED;
		toReturn += (bytes[1] * BYTE_MODDER_SQUARED);
		toReturn += (bytes[2] * BYTE_MODDER);
		toReturn += bytes[3];
		
		// Decimal points
		toReturn += (double) ((bytes[4] * BYTE_MODDER) + bytes[5]) / 10000;
		return toReturn;
	}
	
	/**
	 * Converts a double to a byte array for sending over a network
	 * 
	 * @param num the double (must be within bounds of LARGEST_DOUBLE)
	 * @return the double as sendable bytes
	 */
	public static byte[] intAsBytes(int mainNum)
	{
		if (mainNum > LARGEST_INT || mainNum < SMALLEST_INT)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Integer " + mainNum + " cannot be parsed as a byte array because it is out of bounds");
			return null;
		}
		
		byte[] toRet = new byte[] { 0, 0, 0, 0, 0 };
		
		// Calculates main bytes
		if (mainNum != 0)
		{
			toRet[4] = (byte) (mainNum % BYTE_MODDER);
			mainNum -= toRet[4];
		}
		
		if (mainNum != 0)
		{
			toRet[3] = (byte) ((mainNum % BYTE_MODDER_SQUARED) / BYTE_MODDER);
			mainNum -= (toRet[3] * BYTE_MODDER);
		}
		
		if (mainNum != 0)
		{
			toRet[2] = (byte) ((mainNum % BYTE_MODDER_CUBED) / BYTE_MODDER_SQUARED);
			mainNum -= (toRet[2] * BYTE_MODDER_SQUARED);
		}
		
		if (mainNum != 0)
		{
			toRet[1] = (byte) ((mainNum % BYTE_MODDER_QUAD) / BYTE_MODDER_CUBED);
			mainNum -= (toRet[1] * BYTE_MODDER_CUBED);
		}
		
		if (mainNum != 0)
		{
			toRet[0] = (byte) ((mainNum % BYTE_MODDER_QUINT) / BYTE_MODDER_QUAD);
		}
		
		return toRet;
	}
	
	public static int intFromBytes(byte[] bytes)
	{
		if (bytes.length != 5)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Converting a byte array to an int requires 5 bytes (was only provided with " + bytes.length + ")");
			return 0;
		}
		
		int toReturn = bytes[0] * BYTE_MODDER_QUAD;
		toReturn += bytes[1] * BYTE_MODDER_CUBED;
		toReturn += bytes[2] * BYTE_MODDER_SQUARED;
		toReturn += bytes[3] * BYTE_MODDER;
		toReturn += bytes[4];
		
		return toReturn;
	}
}
