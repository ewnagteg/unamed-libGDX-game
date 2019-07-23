package com.mygdx.packets;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.badlogic.gdx.Gdx;
import com.mygdx.crypto.Encryption;
import com.mygdx.networking.Protocal;

public class PacketWriter {
	private ByteBuffer buff;
	private byte[] data; // current working data
	private byte[] encrypted;

	public void encrypt(byte[] data, byte[] aeskey) {
		Encryption aes;
		try {
			aes = new Encryption();
			encrypted = aes.encrypt(data, aeskey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException e) {
			Gdx.app.log("Packets", "Error encrypting packet");
			e.printStackTrace();
		}
	}

	public void encrypt(byte[] aeskey) {
		encrypt(data, aeskey);
	}

	private void writeData(byte header, byte[] data) {
		buff = ByteBuffer.allocate(data.length + 3);
		buff.put(header);
		buff.putShort((short) data.length);
		buff.put(data);
		this.data = buff.array();
	}

	public byte[] pack() {
		writeData(Protocal.ENCRYPTED, encrypted);
		return data;
	}
}
