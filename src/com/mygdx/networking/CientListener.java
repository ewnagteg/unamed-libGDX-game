package com.mygdx.networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.NoSuchPaddingException;

import com.badlogic.gdx.Gdx;
import com.mygdx.crypto.AsymmetricCrypto;
import com.mygdx.crypto.Encryption;
import com.mygdx.deprecated.DataParser;
import com.mygdx.game.Settings;
import com.mygdx.packets.AuthKeyPacket;
import com.mygdx.packets.EntityDataPacket;
import com.mygdx.packets.PhysicsLockPacket;
import com.mygdx.packets.PostChatPacket;
import com.mygdx.packets.PostChunkPacket;
import com.mygdx.packets.PostSpawnPacket;

public class CientListener extends Thread {
	private DatagramSocket socket;
	private DatagramPacket packet;
	private InetAddress serverIp;
	private int port; // server port
	private byte[] buff;
	public boolean isStopped = false;
	private Client client;
	private Encryption crypto;
	private AsymmetricCrypto rsa;
	private byte[] ssl;
	private int clientId; // uid from server
	public CientListener(DatagramSocket socket, int port, InetAddress serverIp, Client client, PrivateKey rsakey) {
		this.socket = socket;
		try {
			socket.setReceiveBufferSize(125829120);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		buff = new byte[Settings.UDP_BUFF_SIZE];
		packet = new DatagramPacket(buff, buff.length, serverIp, port);
		this.serverIp = serverIp;
		this.port = port;
		this.client = client;
		try {
			crypto = new Encryption();
			rsa = new AsymmetricCrypto();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} finally {
			rsa.setPrivateKey(rsakey);
		}
	}
	
	@Override
	public void run() {
		while (!isStopped) {
			try {
				// Receive data from server
				if (socket.isClosed()) {
					socket.connect(serverIp, port);
				}
				socket.receive(packet);

				// process data
				byte[] data = packet.getData();
				if (data[0] == Protocal.UN_ENCRYPTED) {
					if (data[1] == Protocal.ACK) {
						System.out.println("Server ack");

					} else if (data[1] == Protocal.MALFORMED) {
						Gdx.app.log("Client", "malformed request");
					}

				} else if (data[0] == Protocal.ENCRYPTED) {
					// should be re-written to consume packets
					DataWrap wrap = new DataWrap(data);
					wrap.decrypt(ssl);
					consumePackets(wrap);
					
				} else if (data[0] == Protocal.RSA) {
					byte[] encrypted = DataParser.readEncrypted(data); // To do: replace w/ DataWrap, then delete DataParser
					byte[] sslkey = rsa.decrypt(encrypted);
					synchronized (client) {
						ssl = sslkey;
						synchronized (client) {
							client.setSsl(ssl);
							client.signin();
						}
						crypto.setKey(sslkey);
						Gdx.app.log("sslkey set!", "");
					}
				}

			} catch (Exception e) {
				Gdx.app.log("Client Listener", e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void consumePackets(DataWrap wrap) {
		do {
			// consume packets
			wrap.nextPacket();
			byte header = wrap.currentHeader();
			
			if (header == Protocal.AUTH_KEY) {
				AuthKeyPacket pk = new AuthKeyPacket();
				pk.read(wrap.currentPacket());
				Gdx.app.log("Client", "Auth key being set to: " + pk.getAuth());
				synchronized (client) {
					client.setAuthkey(pk.getAuth());
				}
				client.setLogin(true);
				
			} else if (header == Protocal.POST_CHUNK) {
				PostChunkPacket pk = new PostChunkPacket();
				pk.read(wrap.currentPacket());
				System.out.println("got a chunk");
				// add chunk
				synchronized (client) {
					client.addChunk(pk.getX() * 16, pk.getY() * 16, pk.getMap());
				}
				
				
			} else if (header == Protocal.POST_CHAT) {
				PostChatPacket pk = new PostChatPacket();
				pk.read(wrap.currentPacket());
				synchronized (client) {
					client.chat(pk.getMessage());
				}
				
				
			} else if (header == Protocal.POST_SPAWN) {
				PostSpawnPacket pk = new PostSpawnPacket();
				pk.read(wrap.currentPacket());
				System.out.println("spawning player...");
				synchronized (client) {
					client.setPlayerCord(0, pk.getSpawn());
				}
				this.clientId = pk.getId();
				// update client's id?
				
				
			} else if (header == Protocal.SET_WORLD_LOCK) {
				PhysicsLockPacket pk = new PhysicsLockPacket();
				pk.read(wrap.currentPacket());
				System.out.println("set physics engine lock to: "+pk.getLock());
				synchronized (client) {
					client.setLock(pk.getLock());
				}
				
				
			} else if (header == Protocal.ENT_DATA) {
				EntityDataPacket entData = new EntityDataPacket();
				entData.read(wrap.currentPacket());
				// send to client
			}
			
			
		} while (wrap.currentHeader() != Protocal.END_PACKET);
	}
}
