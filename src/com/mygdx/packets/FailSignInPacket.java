package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Packets;
import com.mygdx.networking.Protocal;

public class FailSignInPacket extends Packet {

	public FailSignInPacket() {
		super();
		header = Protocal.SIGN_IN_FAIL;
	}
	
	public void make() {
		ByteBuffer buff = ByteBuffer.allocate(1+2+37);
		len = buff.capacity();
		buff.put(Protocal.SIGN_IN_FAIL);
		buff.putShort((short) 0);
		byte[] bufferData = Packets.genAuthCode(37);
		buff.put(bufferData);
		data = buff.array();
	}
}
