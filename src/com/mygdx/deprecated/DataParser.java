package com.mygdx.deprecated;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * reads data from packets should be thread safe and only use static methods
 * this is deprecated and should be removed
 */
@Deprecated
public class DataParser {
	
	@Deprecated
	public static byte header(byte[] data) {
		return data[0];
	}
	
	@Deprecated
	public static byte[] readEncrypted(byte[] data) {
		return read(data, 1);
	}

	/**
	 * reads data from packet packet syntax byte[] header | short data length |
	 * byte[] data
	 * 
	 * @param packet
	 * @param offset location of start of data length
	 * @return
	 */
	@Deprecated
	public static byte[] read(byte[] packet, int offset) throws BufferUnderflowException {
		ByteBuffer buff = ByteBuffer.wrap(packet);
		buff.position(offset);
		int len = buff.getShort();
		byte[] data = new byte[len];
		buff.get(data, 0, len);
		return data;
	}
	
	@Deprecated
	public static byte[] readString(byte[] data, int offset) {
		ByteBuffer buff = ByteBuffer.wrap(data);
		buff.position(offset);
		byte len = buff.get();
		byte[] string = new byte[len];
		buff.get(string, 0, len);
		return string;
	}
	
	@Deprecated
	public static int nextString(byte[] data) {
		byte len = (byte) (data[1] + 2);
		return len;
	}
	
	@Deprecated
	public static byte[] write(byte header, byte[] data) {
		ByteBuffer buff = ByteBuffer.allocate(data.length + 3);
		buff.put(header);
		buff.putShort((short) data.length);
		buff.put(data);
		return buff.array();
	}
	
	@Deprecated
	public static byte encrypted(byte[] data) {
		return data[0];
	}

	@Deprecated
	public static byte[] put(byte[] data) {
		ByteBuffer buff = ByteBuffer.allocate(data.length + 3);
		buff.putShort((short) data.length);
		buff.put(data);
		return buff.array();
	}
}
