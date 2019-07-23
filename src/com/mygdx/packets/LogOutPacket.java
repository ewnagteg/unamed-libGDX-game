package com.mygdx.packets;

import com.mygdx.networking.Protocal;

public class LogOutPacket extends Packet {
	public LogOutPacket() {
		super();
		header = Protocal.LOG_OUT;
		len = 3;
	}
	
	public void make() {
		data = new byte[] { header, 0, 0 };
	}
}