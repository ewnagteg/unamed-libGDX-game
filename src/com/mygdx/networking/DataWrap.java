package com.mygdx.networking;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.mygdx.crypto.Encryption;

public class DataWrap {
	private ByteBuffer buff;
	private byte[] rawData;
	private byte[] data;
	private Encryption aes;
	private byte[] currentPacket;
	private byte currentHeader;
	public DataWrap(byte[] packet) {
		rawData = packet;
		buff = ByteBuffer.wrap(packet);
		try {
			aes = new Encryption();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public byte encryption() {
		return rawData[0];
	}

	public byte header() {
		return data[0];
	}
	
	public byte getHeader() {
		return buff.get();
	}

	public void decrypt(byte[] key) {
		if (buff.position() == 0) {
			buff.position(1);
		}
		short len = buff.getShort();
		byte[] d = new byte[len];
		buff.get(d, 0, len);
		try {
			data = aes.decrypt(d, key);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
		buff = ByteBuffer.wrap(data);
	}
	
	public void setPosition(int i) {
		buff.position(i);
	}
	
	public byte get() {
		return buff.get();
	}
	/**
	 * reads the data at the current position
	 * <ol>
	 * <li>short dataLength = buffer.getShort()</li>
	 * <li>iterate and get data, leaves position at end of data</li>
	 * </ol>
	 * @return
	 */
	public byte[] readData() {
		short len = buff.getShort();
		byte[] d = new byte[len];
		buff.get(d, 0, len);
		return d;
	}
	
	public void nextPacket() {
		if (! buff.hasRemaining()) {
			currentHeader = Protocal.END_PACKET;
			currentPacket = new byte[] {0};
			return;
		}
		currentHeader = buff.get();
		currentPacket = readData();
	}
	
	public byte[] currentPacket() {
		return currentPacket;
	}
	
	public byte currentHeader() {
		return currentHeader;
	}
	
	public void debug() {
		System.out.println("Packet header" + currentHeader() + "\n"
						 + "Packet len: "  + data.length     + "\n"
						 + "Start of Packet");
		for (byte b : data) {
			System.out.print(" " + (int) b);
		}
		System.out.println("End of Packet");
	}

	public boolean checkAuth(String authkey) {
		nextPacket();
		return authkey.contentEquals(new String(currentPacket));
	}
}