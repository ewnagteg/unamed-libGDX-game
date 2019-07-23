package com.mygdx.packets;

import java.util.Arrays;

public class Packet {
	protected byte[] data;
	protected int len;
	protected byte header;
	
	public int length() {
		return len;
	}
	
	public byte header() {
		return header;
	}

	/**
	 * read and interpret the packets data
	 * @param packed
	 */
	public void read(byte[] packed) {
	}
	
	/**
	 * return the packed data
	 * <code>packet.make(**params);</code> should be called before this
	 * @return
	 */
	public byte[] getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return new String(header + ": " + Arrays.toString(data));
	}
}