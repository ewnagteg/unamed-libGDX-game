package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Protocal;

public class GetChunkPacket extends Packet {
	private int x;
	private int y;
	public GetChunkPacket() {
		super();
		header = Protocal.GET_CHK;
	}

	public void make(int x, int y) {
		ByteBuffer buff = ByteBuffer.allocate(11);
		len = buff.capacity();
		buff.put(Protocal.GET_CHK);
		buff.putShort((short) 8);
		buff.putInt(x);
		buff.putInt(y);
		data =  buff.array();
	}
	
	@Override
	public void read(byte[] packed) {
		ByteBuffer buff = ByteBuffer.wrap(packed);
		x = buff.getInt();
		y = buff.getInt();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
