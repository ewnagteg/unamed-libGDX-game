package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Protocal;

public class SignInPacket extends Packet {
	private String uname;
	private String pword;
	public SignInPacket() {
		super();
		header = Protocal.SIGN_IN;
	}
	
	public void make(String u, String p) {
		ByteBuffer buff = ByteBuffer.allocate(1+2+1+u.length()+1+p.length());
		len = buff.capacity();
		buff.put(header);
		buff.putShort((short) (1+u.length()+1+p.length()));
		buff.put((byte) u.length());
		buff.put(u.getBytes());
		buff.put((byte) p.length());
		buff.put(p.getBytes());
		data = buff.array();
	}
	
	@Override
	public void read(byte[] data) {
		ByteBuffer buff = ByteBuffer.wrap(data);
		int i = (int) buff.get();
		byte[] arr = new byte[i];
		buff.get(arr, 0, i);
		uname = new String(arr);
		i = (int) buff.get();
		arr = new byte[i];
		buff.get(arr, 0, i);
		pword = new String(arr);
	}
	
	public String getPassword() {
		return pword;
	}
	
	public String getUsername() {
		return uname;
	}
	
}
