package com.mygdx.networking;

public class Protocal {
	private static byte inc = -1;
	private static byte id() {
		inc++;
		return inc;
	}
	
	/*
	 * in general data is byte[]
	 * 
	 * byte encrypt | byte header | short datalen | data
	 * 
	 * client->server packet ENRCYPTED | byte header | short auth key len | byte[]
	 * auth key | byte data header | <= byte[] data =>
	 */
	public static final byte END_PACKET = id(); // is 0
	
	public static final byte SSL_KEY = id();
	// ENRCYPTED | SSL_KEY | encrypted key

	public static final byte GET_KEY = id();
	// UN_ENCRYPTED | GET_KEY | short keylen | key

	public static final byte SIGN_IN = id(); // implemented
	// ENRCYPTED | SIGN_IN | byte unamelen | byte[] uname | byte pwordlen | byte[]
	// pword
	public static final byte GET_CHK = id(); // implemented

	public static final byte POST_CHAT = id();

	public static final byte AUTH_KEY = id();

	public static final byte SIGN_IN_FAIL = id();

	public static final byte POST_CHUNK = id();

	public static final byte GET_SPAWN = id();

	public static final byte POST_SPAWN = id();
	
	
	public static final byte KICK = id(); // server to client, tells client that server erased client's session data
											// ie aes key is deleted ect
	
	public static final byte SET_WORLD_LOCK = id();
	
	public static final byte LOG_OUT = id();

	public static final byte RSA = id();
	public static final byte ACK = id();
	public static final byte PING = id();
	// encryption and error headers
	public static final byte ENCRYPTED = id();
	public static final byte MALFORMED = id();
	// UN_ENCRYPTED | MALFORMED

	public static final byte UN_ENCRYPTED = id();

	public static final byte NOT_AUTHED = id();
	
	public static final byte ENT_DATA = id();
	public static final byte ID_TABLE = id();
	public static final byte GET_IDS = id();
	
}