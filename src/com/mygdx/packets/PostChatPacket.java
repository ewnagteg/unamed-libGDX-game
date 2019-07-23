package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Protocal;

public class PostChatPacket extends Packet {
	private String msg;

	public PostChatPacket() {
		super();
		header = Protocal.POST_CHAT;
	}
	
	public void make(String msg) {
		ByteBuffer buff = ByteBuffer.allocate(1+2+msg.length());
		len = buff.capacity();
		buff.put(header);
		buff.putShort((short) msg.length());
		buff.put(msg.getBytes());
		data = buff.array();
	}
	
	public void read(byte[] data) {
		msg = new String(data);
	}
	
	public String getMessage() {
		return msg;
	}
}
