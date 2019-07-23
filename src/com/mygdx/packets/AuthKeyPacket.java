package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Protocal;

public class AuthKeyPacket extends Packet {
	private String authkey;
	public AuthKeyPacket() {
		header = Protocal.AUTH_KEY;
	}

	public void make(String key) {
		len = 1+2+key.length();
		ByteBuffer buff = ByteBuffer.allocate(len);
		buff.put(header);
		buff.putShort((short) key.length());
		buff.put(key.getBytes());
		data = buff.array();
	}
	
	public void read(byte[] data) {
		authkey = new String(data);
	}
	
	public String getAuth() {
		return authkey;
	}
}
