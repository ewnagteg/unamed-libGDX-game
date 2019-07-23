package com.mygdx.packets;
import com.mygdx.networking.Protocal;

public class NotAuthPacket extends Packet {
	
	public NotAuthPacket() {
		super();len = 2;
		header = Protocal.NOT_AUTHED;
	}
	
	public void make(byte h) {
		data = new byte[] {header, 0, 0};}
	}