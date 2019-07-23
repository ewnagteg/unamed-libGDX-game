package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Protocal;

public class PostSpawnPacket extends Packet {
	private int y;
	private int id;
	public PostSpawnPacket() {
		header = Protocal.POST_SPAWN;
		len = 11;
	}

	public void make(int spawn, int id) {
		ByteBuffer buff = ByteBuffer.allocate(len);
		buff.put(Protocal.POST_SPAWN);
		buff.putShort((short) 8);
		buff.putInt(spawn);
		buff.putInt(id);
		data = buff.array();
	}
	
	public void read(byte[] data) {
		ByteBuffer buff =  ByteBuffer.wrap(data);
		y = buff.getInt();
		id = buff.getInt();
	}
	
	public int getSpawn() {
		return y;
	}
	
	public int getId() {
		return id;
	}
}

