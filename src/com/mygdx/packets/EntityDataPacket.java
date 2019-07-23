package com.mygdx.packets;

import com.mygdx.networking.Protocal;

public class EntityDataPacket extends Packet {
	public EntityDataPacket() {
		super();
		header = Protocal.ENT_DATA;
	}
	
	public EntityDataPacket make(int entNum, int[] ids, float[][] data) {
		
		return this;
	}
}