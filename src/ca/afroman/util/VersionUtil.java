package ca.afroman.util;

public class VersionUtil
{
	// Sub versions are backwards compatible with each other
	public static final int SUB_VERSION = 0;
	
	public static final byte DAY = 24;
	public static final byte MONTH = 9;
	public static final short YEAR = 2016;
	
	private static final byte[] YEAR_SPLIT = ByteUtil.shortAsBytes(YEAR);
	private static final byte[] SUB_SPLIT = ByteUtil.intAsBytes(SUB_VERSION);
	public static final int VERSION = ByteUtil.intFromBytes(new byte[] { YEAR_SPLIT[0], YEAR_SPLIT[1], MONTH, DAY });
	private static final long DISPLAY_VERSION = ByteUtil.longFromBytes(new byte[] { YEAR_SPLIT[0], YEAR_SPLIT[1], MONTH, DAY, SUB_SPLIT[0], SUB_SPLIT[1], SUB_SPLIT[2], SUB_SPLIT[3] });
	public static final String VERSION_STRING = toString(DISPLAY_VERSION);
	
	public static String toString(long version)
	{
		byte[] in = ByteUtil.longAsBytes(version);
		
		StringBuilder sb = new StringBuilder();
		sb.append(ByteUtil.shortFromBytes(new byte[] { in[0], in[1] }));
		sb.append(".");
		sb.append(in[2]);
		sb.append(".");
		sb.append(in[3]);
		int subVersion = ByteUtil.intFromBytes(new byte[] { in[4], in[5], in[6], in[7] });
		
		if (subVersion > 0)
		{
			sb.append(".");
			sb.append(subVersion);
		}
		
		return sb.toString();
	}
}
