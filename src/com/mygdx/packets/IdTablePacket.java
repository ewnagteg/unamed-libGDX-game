package com.mygdx.packets;

import com.mygdx.networking.Protocal;

public class IdTablePacket extends Packet {
	public IdTablePacket() {
		super();
		header = Protocal.ID_TABLE;
	}
	
	public IdTablePacket make() {
		return this;
	}
}
