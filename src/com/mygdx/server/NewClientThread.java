package com.mygdx.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.badlogic.gdx.utils.Array;
import com.mygdx.crypto.AsymmetricCrypto;
import com.mygdx.crypto.Encryption;
import com.mygdx.database.WorldData;
import com.mygdx.game.Settings;
import com.mygdx.gen.GenChk;
import com.mygdx.networking.DataWrap;
import com.mygdx.networking.Packets;
import com.mygdx.networking.Protocal;
import com.mygdx.packets.AuthKeyPacket;
import com.mygdx.packets.GetChunkPacket;
import com.mygdx.packets.Packet;
import com.mygdx.packets.PacketBuilder;
import com.mygdx.packets.PhysicsLockPacket;
import com.mygdx.packets.PostChatPacket;
import com.mygdx.packets.PostChunkPacket;
import com.mygdx.packets.PostSpawnPacket;
import com.mygdx.packets.SignInPacket;

public class NewClientThread extends Thread {
	public DatagramPacket packet;
	private int port;
	private InetAddress client;
	private byte[] data;
	private Encryption aes;
	private AsymmetricCrypto rsa;
	public DatagramSocket out;
	private WorldData db;
	private DataWrap wrap;
	private GenChk gen;
	private ConcurrentLinkedQueue<String> chatQueue;
	private UserInfo users;
	private EntityData ents;
	private Array<Packet> packetBuffer;
	private int packetLen;
	private final String chatDelim = ":  ";
	
	public NewClientThread(DatagramSocket out, UserInfo users, byte[] data, 
							InetAddress add, int port, WorldData db, int seed, 
							ConcurrentLinkedQueue<String>  chatQueue, EntityData entitys
						  ) {
		
		
		this.data = data;
		this.client = add;
		this.port = port;
		this.out = out;
		this.db = db;
		this.wrap = new DataWrap(data);
		this.gen = new GenChk(seed);
		this.chatQueue = chatQueue;
		this.users = users;
		this.ents = entitys;
		packetBuffer = new Array<Packet>();
		packetLen = 0;
		try {
			this.aes = new Encryption();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Server crypto" + e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			if (wrap.encryption() == Protocal.UN_ENCRYPTED) {

				
				if (data[1] == Protocal.GET_KEY) {
					// this needs to be refactored, but it works
					// get rsa key
					// note: if a user sends a getkey request and they exist in the hash map,
					// server will send them the aes key in the hash map
					
					/*
					 * int keylen = 0; ByteBuffer buff = ByteBuffer.allocate(2); buff.put(data[2]);
					 * buff.put(data[3]); buff.position(0);
					 */
					wrap.setPosition(2);
					// keylen = (int)buff.getShort();
					byte[] key = wrap.readData();
					System.out.println("Server recieved client key");

					// get aes key or generate it
					byte[] aesKey = null;
					if (users.containsUser(client, port)) {
						aesKey = users.getUser(client,  port).sslkey;
					} else {
						UserData user = new UserData();
						aesKey = aes.makeKey();
						user.authkey = null;
						user.sslkey = aesKey;
						user.port = port;
						user.loggedIn = false;
						users.addUser(client, user, port);
					}

					// encrypt aes key
					byte[] encrypted = null;
					try {
						rsa = new AsymmetricCrypto();
						rsa.setPublicKey(key);
						encrypted = rsa.encrypt(aesKey);
						// send encrypted key
						send(Packets.postKey(encrypted));
					} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
							| NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
						System.out.println("Server Cryto " + e.getMessage());
					}
					return;

					
				} else if (data[1] == Protocal.PING) {
					send(Packets.ack);
					return;

					
				} else {
					send(Packets.malformedRequest());
					System.out.println("malformed request");
					return;
				}

				// this handles aes encrypted requests
			} else if (wrap.encryption() == Protocal.ENCRYPTED) {
				if (!users.containsUser(client, port)) { // if user has been kicked, make sure they know it.
					System.out.println("Server: kicking client");
					send(Packets.kick);
					return;
				}

				wrap.decrypt(users.getUser(client, port).sslkey);
				consumePackets(wrap); // consume packet

			} else {
				send(Packets.malformedRequest());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendAuth(String uname) {
		byte[] auth = Packets.genAuthCode(16);
		UserData u = users.getUser(client, port);
		if (u == null) {
			u = new UserData();
		}
		u.uname = uname;
		u.authkey = new String(Base64.getEncoder().encode(auth));
		u.sit = System.currentTimeMillis();
		u.loggedIn = true;
		users.addUser(client, u, port);
		
		AuthKeyPacket pk = new AuthKeyPacket();
		pk.make(u.authkey);
		writePacket(pk);
	}

	private void send(byte[] data) {
		DatagramPacket reply = new DatagramPacket(data, data.length, client, port);
		try {
			out.send(reply);
		} catch (IOException e) {
			System.out.println("Client: failed to send packet");
			e.printStackTrace();
		}
	}
	
	private void consumePackets(DataWrap wrap) {
		// check auth packet
		wrap.nextPacket();
		if (users.getUser(client, port).loggedIn) {
			if (wrap.currentHeader() == Protocal.LOG_OUT) {
				users.getUser(client, port).loggedIn = false;
				users.getUser(client, port).authkey = null;
			} else if (wrap.currentHeader() != Protocal.AUTH_KEY) {
				System.out.println("Client packet missing auth key");
				// send log out packet
				return;
			} else if (wrap.currentHeader() == Protocal.AUTH_KEY) {
				String auth = new String(wrap.currentPacket());
				if (!users.getUser(client, port).authkey.contentEquals(auth)) {
					// send not authed packet
					System.out.println("not authed");
					return;
				}
			}
		} else {
			handlePacket(wrap);
		}
		while (wrap.currentHeader() != Protocal.END_PACKET) {
			wrap.nextPacket();
			handlePacket(wrap);
		}
		consumeBuffer(users.getUser(client, port).sslkey); // clear any remaining packets
	}
	
	/**
	 * handle DataWrap's current packet
	 * @param wrap
	 */
	private void handlePacket(DataWrap wrap) {
		byte header = wrap.currentHeader();
		
		
		if (header == Protocal.GET_CHK) {
			GetChunkPacket packed = new GetChunkPacket();
			packed.read(wrap.currentPacket());
			int[][] map = null;
			synchronized (db) {
				if ( db.isChunkSaved(16 * packed.getX(), 16 * packed.getY()) ) {
					map = db.loadChunk(16 * packed.getX(), 16 * packed.getY());
				} else {
					map = gen.gen(16 * packed.getX(), 16 * packed.getY());
					db.createChunk(16 * packed.getX(), 16 * packed.getY(), map);
				}
			}
			
			PostChunkPacket pack = new PostChunkPacket();
			pack.make(map, packed.getX(), packed.getY());
			writePacket(pack);
			
			
		} else if (header == Protocal.GET_SPAWN) {
			int y = (int) gen.getSpawnHeight();
			System.out.println("user get spawn");
			//ents.addEntity("", data); // needs fixing
			
			// send spawn packet
			PostSpawnPacket pk = new PostSpawnPacket();
			ents.addEntity(users.getUser(client, port).uname, new float[] {0, y, 0, 0});
			pk.make(y, 0);
		    writePacket(pk);
		    
		    // send chunk that player spawns in
		    PostChunkPacket pk2 = new PostChunkPacket();
		    int[][] map = null;
		    int yb = (y / Settings.BSIZE / 16) * 16;
		    synchronized (db) {
				if (db.isChunkSaved(0, yb)) {
					map = db.loadChunk(0, yb);
				} else {
					map = gen.gen(0, yb);
					db.createChunk(0, yb, map);
				}
			}
		    pk2.make(map, 0, yb / 16);
		    writePacket(pk2);
		    
		    // tell client to unlock physics engine
		    PhysicsLockPacket phys = new PhysicsLockPacket();
		    phys.make(false);
		    writePacket(phys);
		    consumeBuffer(users.getUser(client, port).sslkey);
		    
		    
		} else if (header == Protocal.POST_CHAT) {
			PostChatPacket pk = new PostChatPacket();
			pk.read(wrap.currentPacket());
			// add to broadcast
			System.out.println("user: " + users.getUser(client, port).uname + " says: "+pk.getMessage());
			chatQueue.add(users.getUser(client, port).uname+chatDelim+pk.getMessage());
			
		} else if (header == Protocal.LOG_OUT) {
			System.out.println("Logging user: " + users.getUser(client, port).uname + " out...");
			users.getUser(client, port).loggedIn = false;
			
			
		} else if (header == Protocal.SIGN_IN) {
			SignInPacket pk = new SignInPacket();
			pk.read(wrap.currentPacket());
			boolean exist = false;
			String hash = "";
			String salt = "";
			System.out.println("recieved signin data...");
			// fetch user data
			synchronized (db) {
				if (db.userExist(pk.getUsername())) {
					// these should be base64 encoded
					hash += db.getHash(pk.getUsername());
					salt += db.getSalt(pk.getUsername());
					exist = true;
				}
			}
			
			// compare hashes
			if (exist) {
				// hash password using Base64 salt
				byte[] userHash = Packets.hash(pk.getPassword().getBytes(), salt.getBytes());

				String testhash = new String(Base64.getEncoder().encode(userHash));
				if (testhash.contentEquals(hash)) {
					System.out.println("sending auth to user: " + pk.getUsername());
					sendAuth(pk.getUsername());
				} else {
					// send fail sign in packet
					return;
				}

			} else {
				byte[] bsalt = Packets.salt();
				salt = new String(Base64.getEncoder().encode(bsalt)); // encode salt
				String str = new String(
							Base64.getEncoder().encode(
								Packets.hash(pk.getPassword().getBytes(), salt.getBytes())
							)
						); // hash and encode password
				
				System.out.println("adding user: " + pk.getUsername());
				synchronized (db) {
					db.addUser(pk.getUsername(), str, salt);
				}
				System.out.println("added user");
				sendAuth(pk.getUsername());
				return;
			}
		}
	}
	
	private void writePacket(Packet packet) {
		packetBuffer.add(packet);
		packetLen += packet.length();
		if (packetLen > 1000) {
			System.out.println("sending packet");
			consumeBuffer(users.getUser(client, port).sslkey);
		}
	}
	
	private void consumeBuffer(byte[] key) {
		PacketBuilder reply = new PacketBuilder();
		reply.allocate(packetLen);
		if (packetBuffer.size != 0) {
			for (Packet p : packetBuffer) {
				reply.putPacket(p.getData());
			}
			packetLen = 0;
			packetBuffer.clear();
			reply.encrypt(key);
			send(reply.getPacket());
		}
	}
}