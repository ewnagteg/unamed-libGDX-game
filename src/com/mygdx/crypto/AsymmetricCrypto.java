package com.mygdx.crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AsymmetricCrypto {
	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private Cipher cipher;
	private KeyFactory kf;

	/**
	 * not going to lie, mostly from some blog
	 * https://www.mkyong.com/java/java-asymmetric-cryptography-example/
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public AsymmetricCrypto() throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(1024);
		this.cipher = Cipher.getInstance("RSA");
		kf = KeyFactory.getInstance("RSA");
	}

	public void createKeys() {
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public void setPrivateKey(PrivateKey key) {
		this.privateKey = key;
	}

	public void setPublicKey(byte[] key) throws InvalidKeySpecException {
		this.publicKey = kf.generatePublic(new X509EncodedKeySpec(key));
	}

	public byte[] encrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}

	public byte[] decrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}

}