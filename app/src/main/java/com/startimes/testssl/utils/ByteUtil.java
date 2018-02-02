package com.startimes.testssl.utils;

public class ByteUtil
{	
	public static byte[] getBytes(short data)
	{
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}
	
	public static byte[] getNetBytes(short data)
	{
		byte[] bytes = new byte[2];
		bytes[1] = (byte) (data & 0xff);
		bytes[0] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getBytes(char data)
	{
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data);
		bytes[1] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getBytes(int data)
	{
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}
	
	public static byte[] getNetBytes(int data)
	{
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (data & 0xff);
		bytes[2] = (byte) ((data & 0xff00) >> 8);
		bytes[1] = (byte) ((data & 0xff0000) >> 16);
		bytes[0] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	public static byte[] getNetBytes(long data)
	{
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}
	
	public static byte[] getBytes(long data)
	{
		byte[] bytes = new byte[8];
		bytes[7] = (byte) (data & 0xff);
		bytes[6] = (byte) ((data >> 8) & 0xff);
		bytes[5] = (byte) ((data >> 16) & 0xff);
		bytes[4] = (byte) ((data >> 24) & 0xff);
		bytes[3] = (byte) ((data >> 32) & 0xff);
		bytes[2] = (byte) ((data >> 40) & 0xff);
		bytes[1] = (byte) ((data >> 48) & 0xff);
		bytes[0] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getBytes(float data)
	{
		int intBits = Float.floatToIntBits(data);
		return getBytes(intBits);
	}
	
	public static byte[] getNetBytes(float data)
	{
		int intBits = Float.floatToIntBits(data);
		return getNetBytes(intBits);
	}

	public static byte[] getBytes(double data)
	{
		long intBits = Double.doubleToLongBits(data);
		return getBytes(intBits);
	}
	
	public static byte[] getNetBytes(double data)
	{
		long intBits = Double.doubleToLongBits(data);
		return getNetBytes(intBits);
	}

	public static byte[] getBytes(String data)
	{
		if (data == null)
			return null;
		
		return data.getBytes();
	}

	public static byte[] getBytes(byte[] data, int pos, int size) {
		byte[] bytes = new byte[size];
		for (int i=0;i<size;i++) {
			bytes[i] = data[pos+i];
		}
		return bytes;
	}
	// ---------------------------------------------------------------------------------//
	public static byte HexToByte(byte[] str)
	{
		return (byte) (((str[0] << 4) & 0xFF) | ((str[1]) & 0xFF)); 
	}
	
	public static short getShort(byte[] bytes)
	{
		return (short) ( (0xff & bytes[0])| (0xff00 & (bytes[1] << 8)));
	}
	
	public static short getNetShort(byte[] bytes)
	{
		return (short) ( (0xff & bytes[1])| (0xff00 & (bytes[0] << 8)));
	}

	public static short getShort(byte[] bytes, int pos)
	{
		return (short) ( (0xff & bytes[0+pos])| (0xff00 & (bytes[1+pos] << 8)));
	}
	
	public static short getNetShort(byte[] bytes, int pos)
	{
		return (short) ( (0xff & bytes[1+pos])| (0xff00 & (bytes[0+pos] << 8)));
	}

	public static char getChar(byte[] bytes)
	{
		return (char) ((0xff00 & (bytes[1] << 8)) | (0xff & bytes[0]));
	}

	public static int getInt(byte[] bytes)
	{
		return  ((0xff & bytes[0])| (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24)));
	}

	public static int getInt(byte[] bytes, int pos)
	{
		return  ((0xff & bytes[pos+0])| (0xff00 & (bytes[pos+1] << 8)) | (0xff0000 & (bytes[pos+2] << 16)) | (0xff000000 & (bytes[pos+3] << 24)));
	}

	public static int getNetInt(byte[] bytes)
	{
		return  ((0xff000000 & bytes[0]<< 24)| (0xff0000 & (bytes[1] << 16)) | (0xff00 & (bytes[2] << 8)) | (0xff & (bytes[3])));
	}

	public static int getNetInt(byte[] bytes, int pos)
	{
		return  ((0xff000000 & bytes[pos+0]<< 24)| (0xff0000 & (bytes[pos+1] << 16)) | (0xff00 & (bytes[pos+2] << 8)) | (0xff & (bytes[pos+3])));
	}

	public static  long getLong(byte[] bytes)
	{
		return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16)) | (0xff000000L & ((long) bytes[3] << 24)) | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
	}

	public static  long getLong(byte[] bytes, int pos) 	{
		return (0xffL & (long) bytes[pos+0]) | (0xff00L & ((long) bytes[pos+1] << 8)) | (0xff0000L & ((long) bytes[pos+2] << 16)) | (0xff000000L & ((long) bytes[pos+3] << 24)) | (0xff00000000L & ((long) bytes[pos+4] << 32)) | (0xff0000000000L & ((long) bytes[pos+5] << 40)) | (0xff000000000000L & ((long) bytes[pos+6] << 48)) | (0xff00000000000000L & ((long) bytes[pos+7] << 56));
	}

	public static  long getNetLong(byte[] bytes)
	{
		return (0xffL & (long) bytes[7]) | (0xff00L & ((long) bytes[6] << 8)) | (0xff0000L & ((long) bytes[5] << 16)) | (0xff000000L & ((long) bytes[4] << 24)) | (0xff00000000L & ((long) bytes[3] << 32)) | (0xff0000000000L & ((long) bytes[2] << 40)) | (0xff000000000000L & ((long) bytes[1] << 48)) | (0xff00000000000000L & ((long) bytes[0] << 56));
	}

	public static  long getNetLong(byte[] bytes, int pos) 	{
		return (0xffL & (long) bytes[pos+7]) | (0xff00L & ((long) bytes[pos+6] << 8)) | (0xff0000L & ((long) bytes[pos+5] << 16)) | (0xff000000L & ((long) bytes[pos+4] << 24)) | (0xff00000000L & ((long) bytes[pos+3] << 32)) | (0xff0000000000L & ((long) bytes[pos+2] << 40)) | (0xff000000000000L & ((long) bytes[pos+1] << 48)) | (0xff00000000000000L & ((long) bytes[pos+0] << 56));
	}
	
	public static long getMac(byte[] bytes)
	{
		return (0xffL & (long) bytes[7]) | (0xff00L & ((long) bytes[6] << 8)) | (0xff0000L & ((long) bytes[5] << 16)) | (0xff000000L & ((long) bytes[4] << 24)) | (0xff00000000L & ((long) bytes[3] << 32)) | (0xff0000000000L & ((long) bytes[2] << 40)) | (0xff000000000000L & ((long) bytes[1] << 48)) | (0xff00000000000000L & ((long) bytes[0] << 56));
	}

	public static float getFloat(byte[] bytes)
	{
		return Float.intBitsToFloat(getInt(bytes));
	}

	public static double getDouble(byte[] bytes)
	{
		long l = getLong(bytes);
		System.out.println(l);
		return Double.longBitsToDouble(l);
	}

	public static String getString(byte[] bytes)
	{
		return new String(bytes);
	}

	public static String getStringNum(byte[] bytes, int offset, int length) throws Exception {
		return  new String(bytes, offset, length, "GBK");
	}
	     
	public static String bytesToHexString(byte[] src) {
	    StringBuilder stringBuilder = new StringBuilder("");
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  
	
	public static String bytesToHexString(byte src){
	    StringBuilder stringBuilder = new StringBuilder("");
	    for (int i = 0; i < 1; i++) {  
	        int v = src & 0xFF;  
	        String hv = Integer.toHexString(v);
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}
}
