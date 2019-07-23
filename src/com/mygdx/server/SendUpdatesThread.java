package com.mygdx.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SendUpdatesThread implements Runnable {
	private ConcurrentLinkedQueue<String> chatQueue;
	private EntityData entdata;
	private UserInfo users;
	public SendUpdatesThread(DatagramSocket socket, UserInfo users, ConcurrentLinkedQueue<String> chatQueue, EntityData ents) {
		this.chatQueue = chatQueue;
		entdata = ents;
		this.users = users;
	}
	
	@Override
	public void run() {
		
		// get all entity positions and velocities
		
		int num = entdata.getSize();
		float[][] entArray = new float[num][];
		short[] ids = new short[num];
		int i = 0;
		int buffSize = num*5+1;
		ByteBuffer buff = ByteBuffer.allocate(buffSize);
		buff.putShort((short)num);
		for (int i1=0; i1<num; i1++) {
			buff.putShort(ids[i1]);
			for (float f : entArray[i]) {
				buff.putFloat(f);
			}
		}
		
		// should consume chat queue
	}

}
