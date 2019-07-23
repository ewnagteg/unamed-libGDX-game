package com.mygdx.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * should be used for ssl encryption
 * 
 *
 */
public class Encryption {
	public byte[] key;
	public Cipher cipher;
	private SecureRandom secureRandom = new SecureRandom();

	public Encryption() throws NoSuchAlgorithmException, NoSuchPaddingException {
		cipher = Cipher.getInstance("AES/GCM/NoPadding");
		byte[] key = new byte[16]; // 128 bits
		secureRandom.nextBytes(key);
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public byte[] makeKey() {
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		return key;
	}

	public byte[] encrypt(byte[] data, byte[] key) throws IllegalBlockSizeException, BadPaddingException, // should really throw less exceptions
			InvalidKeyException, InvalidAlgorithmParameterException {
		SecretKey secretKey = new SecretKeySpec(key, "AES");

		byte[] iv = new byte[12]; // NEVER REUSE THIS IV WITH SAME KEY (apparently thats bad)
		secureRandom.nextBytes(iv);
		GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
		byte[] coolShit = null;

		coolShit = cipher.doFinal(data);
		ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + coolShit.length);
		byteBuffer.putInt(iv.length);
		byteBuffer.put(iv);
		byteBuffer.put(coolShit);
		byte[] cipherMessage = byteBuffer.array();

		return cipherMessage;
	}

	public byte[] decrypt(byte[] data) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		return decrypt(data, key);
	}

	public byte[] decrypt(byte[] data, byte[] key) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		int ivLength = byteBuffer.getInt();
		if (ivLength < 12 || ivLength >= 16) { // check input parameter
			throw new IllegalArgumentException("invalid iv length");
		}
		byte[] iv = new byte[ivLength];
		byteBuffer.get(iv);
		byte[] cipherText = new byte[byteBuffer.remaining()];
		byteBuffer.get(cipherText);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
		byte[] plainText = cipher.doFinal(cipherText);
		return plainText;
	}
}
