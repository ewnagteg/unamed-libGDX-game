package com.mygdx.packets;

import com.mygdx.networking.Protocal;

public class GetSpawnPacket extends Packet {
	public GetSpawnPacket() {
		super();
		header = Protocal.GET_SPAWN;
		len = 3;
	}
	
	public void make() {
		data = new byte[] {header, 0, 0};
	}
}
