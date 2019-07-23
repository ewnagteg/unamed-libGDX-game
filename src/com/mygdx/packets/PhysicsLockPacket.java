package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Protocal;

public class PhysicsLockPacket extends Packet {
	private boolean lock;
	
	public PhysicsLockPacket() {
		super();
		header = Protocal.SET_WORLD_LOCK;
		len = 4;
	}
	
	public void make(boolean lock) {
		ByteBuffer buff = ByteBuffer.allocate(4);
		buff.put(Protocal.SET_WORLD_LOCK);
		buff.putShort((short) 1);
		if (lock) {
			buff.put((byte) 0);
		} else {
			buff.put((byte) 1);
		}
		data = buff.array();
	}
	
	public void read(byte[] data) {
		if (data[0] == 0) {
			lock = false;
		} else {
			lock = true;
		}
	}
	
	public boolean getLock() {
		return lock;
	}
}