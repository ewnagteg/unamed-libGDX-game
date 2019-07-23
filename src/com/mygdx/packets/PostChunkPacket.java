package com.mygdx.packets;

import java.nio.ByteBuffer;

import com.mygdx.networking.Protocal;

public class PostChunkPacket extends Packet {
	private int[][] map;
	private int x;
	private int y;
	public PostChunkPacket() {
		super();
		header = Protocal.POST_CHUNK;
		len = 1 + 2 + 4 + 4 + 256; // byte header + short data.length + int x + int y + byte[] data
	}
	
	public void make(int[][] map, int x, int y) {
		ByteBuffer buff = ByteBuffer.allocate(len);
		buff.put(header);
		buff.putShort((short) (4 + 4 + 256));
		buff.putInt(x);
		buff.putInt(y);
		for (int i=0; i<256; i++) {
			buff.put((byte) map[i%16][i/16]);
		}
		data = buff.array();
	}
	
	@Override
	public void read(byte[] data) {
		ByteBuffer buff = ByteBuffer.wrap(data);
		x = buff.getInt();
		y = buff.getInt();
		map = new int[16][16];
		for (int i=0; i<256; i++) {
			map[i%16][i/16] = 0 | (buff.get() & 0xFF); // prevent sign extension
		}
	}
	
	public int[][] getMap() {
		return map;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
}
