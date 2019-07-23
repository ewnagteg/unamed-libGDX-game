package com.mygdx.packets;

import java.nio.ByteBuffer;

/**
 * <i>use:</i>
 * <br>
 * <code>
 * packetBuilder.allocate(packet.length(), 1); <br>
 * packetBuilder.put(packet.getData()); <br>
 * packetBuilder.encrypt(aesKey); <br>
 * send(packetBuilder.getPacket()); <br>
 * </code>
 */
public class PacketBuilder {
	private ByteBuffer buff;
	private byte[] data;
	
	/**
	 * allocates the given amount off space to a ByteBuffer
	 * length = total length of all the packets
	 * @param length
	 */
	public void allocate(int length) {
		buff  = ByteBuffer.allocate(length);
	}
	
	/**
	 * puts a packet into the buffer
	 * @param header
	 * @param packet
	 */
	public void putPacket(byte[] packet) {
		buff.put(packet);
	}
	
	/**
	 * encrypts the packet and formats it<br>
	 * format:<br>
	 * <code>
	 * byte Encryption header | short data.length | byte[] data
	 * </code>
	 * @param key
	 */
	public void encrypt(byte[] key) {
		data = buff.array();
		PacketWriter write = new PacketWriter();
		write.encrypt(data, key);
		data = write.pack();
	}
	
	public byte[] getPacket() {
		return data;
	}
}