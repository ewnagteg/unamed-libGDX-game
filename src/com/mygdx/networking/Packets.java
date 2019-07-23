package com.mygdx.networking;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.mygdx.crypto.Encryption;

public class Packets {

	private static SecureRandom secureRandom = new SecureRandom();

	public static byte[] ping = new byte[] { Protocal.UN_ENCRYPTED, Protocal.PING };
	public static byte[] ack = new byte[] { Protocal.UN_ENCRYPTED, Protocal.ACK };
	public static byte[] kick = new byte[] { Protocal.UN_ENCRYPTED, Protocal.KICK };

	public static byte[] getKey(byte[] pubkey) {
		/*
		 * syntax = | crypto byte | get request | short key len | key bytes client sends
		 * its public key and username and asks for server key server should send its
		 * public key then cleint enrpts sign in info an sends it to server server
		 * authenticates client data and creates a auth key server stores auth key,
		 * username and salted and hashed password uname and pword go in the db uname
		 * and auth key and timestamp goes in some concurrent data struct from then on
		 * client uses auth key and the pubkey it was given should go: encrypt byte |
		 * post/get request | auth key lenght | auth key | data length | data
		 * 
		 */
		/*
		 * -127 | 1 | syntax UN_ENCRYPTED | GET_PUBLIC_KEY | short key.length | byte[]
		 * key
		 */
		ByteBuffer buff = ByteBuffer.allocate(6 + pubkey.length);
		buff.put(Protocal.UN_ENCRYPTED); // 0
		buff.put(Protocal.GET_KEY); // 1
		buff.putShort((short) pubkey.length); // 2 //3
		buff.put(pubkey);
		return buff.array();
	}

	public static byte[] postKey(byte[] key) {
		/*
		 * 123 | syntax RSA | short keylen | byte[] key (encrypted)
		 */
		ByteBuffer buff = ByteBuffer.allocate(4 + key.length);
		buff.put(Protocal.RSA);
		buff.putShort((short) key.length);
		buff.put(key);
		return buff.array();
	}

	public static byte[] sendAuthKey(byte[] authkey, byte[] aeskey) {
		ByteBuffer buff = ByteBuffer.allocate(5 + authkey.length + aeskey.length);
		buff.put(Protocal.AUTH_KEY);
		buff.putShort((short) authkey.length);
		buff.put(authkey);
		buff.putShort((short) aeskey.length);
		buff.put(aeskey);
		return encrypt(buff.array(), Protocal.ENCRYPTED, aeskey); // should be about 38 bytes
	}

	public static byte[] signIn(String uname, String pword, byte[] aeskey) {
		uname = truncate(uname);
		pword = truncate(pword);
		if (pword.length() > 32) {
			pword = pword.substring(0, 32);
		}
		ByteBuffer buff = ByteBuffer.allocate(3 + uname.length() + pword.length());
		buff.put(Protocal.SIGN_IN);
		buff.put((byte) uname.length());
		buff.put(uname.getBytes());
		buff.put((byte) pword.length());
		buff.put(pword.getBytes());
		byte[] packet = encrypt(buff.array(), Protocal.ENCRYPTED, aeskey);
		return packet;
	}

	public static byte[] failSignIn(byte[] sslkey) {
		// packet is meant to be about the same length as send auth key, thus being a
		// little less then dumb
		ByteBuffer buff = ByteBuffer.allocate(sslkey.length + 1 + 32);
		buff.put(Protocal.SIGN_IN_FAIL);
		byte[] bufferData = genAuthCode(32);
		buff.put(bufferData);
		return encrypt(buff.array(), Protocal.ENCRYPTED, sslkey);
	}

	public static byte[] malformedRequest() {
		ByteBuffer buff = ByteBuffer.allocate(2);
		buff.put(Protocal.UN_ENCRYPTED);
		buff.put(Protocal.MALFORMED);
		return buff.array();
	}

	public static byte[] encrypt(byte[] data, byte header, byte[] aeskey) {
		Encryption aes;
		byte[] encrypted = null;
		try {
			aes = new Encryption();
			encrypted = aes.encrypt(data, aeskey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException e) {
			System.out.println("Packets Error encrypting packet");
			e.printStackTrace();
		}
		ByteBuffer buff = ByteBuffer.allocate(encrypted.length + 3);
		buff.put(header);
		buff.putShort((short) encrypted.length);
		buff.put(encrypted);
		return buff.array();
	}

	public static byte[] salt() {
		return genAuthCode(32);
	}

	public static byte[] genAuthCode(int len) {
		byte[] code = new byte[len];
		secureRandom = new SecureRandom();
		secureRandom.nextBytes(code);
		return code;
	}

	public static byte[] hash(byte[] thing, byte[] salt) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Server error hashing password");
			e.printStackTrace();
		}
		ByteBuffer buff = ByteBuffer.allocate(thing.length + salt.length);
		buff.put(thing);
		buff.put(salt);
		byte[] hash = digest.digest(buff.array());
		return hash;
	}

	private static String truncate(String s) {
		if (s.length() > 32) {
			return s.substring(0, 31);
		}
		return s;
	}
}