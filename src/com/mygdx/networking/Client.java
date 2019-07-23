package com.mygdx.networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.NoSuchPaddingException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.crypto.AsymmetricCrypto;
import com.mygdx.crypto.Encryption;
import com.mygdx.gameservers.ClientServer;
import com.mygdx.packets.GetChunkPacket;
import com.mygdx.packets.GetSpawnPacket;
import com.mygdx.packets.LogOutPacket;
import com.mygdx.packets.Packet;
import com.mygdx.packets.PacketBuilder;
import com.mygdx.packets.PostChatPacket;
import com.mygdx.packets.SignInPacket;

public class Client {
	public int port = 8000; // server's port, not the clients port
	private String uname;
	private String pword;
	private static DatagramSocket socket;
	private static InetAddress serverIp;
	private AsymmetricCrypto rsa;
	private byte[] key; // rsa key
	private byte[] ssl; // aes key
	private String authkey;
	private ClientServer server;
	public float spawn = 0f;
	public boolean spawned = false;
	protected int buffsize = 12582912;
	private final ExecutorService pool;
	private ConcurrentLinkedQueue<Packet> packets;
	public enum State {
		CONNECTING, // ping server to see if it is active
		CONNECTED,
		LOG_OUT,
		SHUTDOWN
	};

	private State state;
	private boolean lock;
	private boolean loggedIn;

	/**
	 * @param server   = ClientServer
	 * @param add      = server's InetAddress
	 * @param port     = server's port
	 * @param username = user's username
	 * @param password = user's password
	 */
	public Client(ClientServer server, InetAddress add, int port, String username, String password) {
		super();
		this.server = server;
		try {
			rsa = new AsymmetricCrypto();
			rsa.createKeys();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e1) {
			e1.printStackTrace();
		}
		serverIp = add;
		System.out.println("Client port set to: " + port);
		this.port = port;
		try {
			socket = new DatagramSocket();
			//socket.setReceiveBufferSize(buffsize);
			//socket.setSendBufferSize(buffsize);
		} catch (SocketException e) {
			System.out.println("failed to create socket");
			System.out.println(e.getMessage());
		}
		pword = password;
		uname = username;
		packets = new ConcurrentLinkedQueue<Packet>();
		pool = Executors.newFixedThreadPool(10);
	}

	public void connect() {
		ssl = null;
		authkey = null;
		state = State.CONNECTING;
		lock = true;
		getkey();
	}

	public void kicked() {
	}

	public void run() {
		Gdx.app.log("Client", "running listiner");
		new Thread(new CientListener(socket, port, serverIp, this, this.rsa.getPrivateKey())).start();
	}

	public void getkey() {
		System.out.println("sending key");
		key = rsa.getPublicKey().getEncoded();
		byte[] data = Packets.getKey(key);
		ssl = null;
		send(data);
	}

	public void ping() {
		send(Packets.ping);
	}

	private void send(byte[] data) {
		try {
			System.out.println("sending data..");
			DatagramPacket packet = new DatagramPacket(data, data.length, serverIp, port);
			socket.send(packet);
		} catch (Exception e) {
			System.out.println("failed to send packet");
			System.out.println(e.getMessage());
		}
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public void setPword(String pword) {
		// this should not be stored at all
		this.pword = pword;
	}
	
	/**
	 * sends the following packets to the server:
	 * <br>
	 * <code>
	 * 	<ol>
 	 * 		<li>new LogOutPacket();</li>
	 * 		<li>new SignInPacket();</li>
	 * 		<li>new GetSpawnPacket();</li>
	 * 	</ol>
	 * </code>
	 */
	public void signin() {
		System.out.println("Attempting to sign in..");
		SignInPacket signin = new SignInPacket();
		LogOutPacket logout = new LogOutPacket();
		GetSpawnPacket spawn = new GetSpawnPacket();
		spawn.make();
		logout.make();
		signin.make(uname, pword);
		PacketBuilder b = new PacketBuilder();
		b.allocate(signin.length() + logout.length() + spawn.length());
		
		// build packet
		b.putPacket(logout.getData());
		b.putPacket(signin.getData());
		b.putPacket(spawn.getData());
		b.encrypt(ssl);
		loggedIn = false;
		send(b.getPacket());
	}
	
	/**
	 * sets the value of the auth key
	 * @param key
	 */
	public void setAuthkey(String key) {
		this.authkey = key;
	}

	/**
	 * ask server for given chunk
	 * @param x
	 * @param y
	 */
	public void loadChunk(int x, int y) {
		Gdx.app.log("Client", "Loading chunk: " + x * 16 + " " + y * 16);
		if (loggedIn) {
			GetChunkPacket p = new GetChunkPacket();
			p.make(x, y);
			packets.add(p);
		}
	}

	/**
	 * add the chunk to the game server when it has been received
	 * uses block cords
	 * @param x
	 * @param y
	 * @param map
	 */
	public void addChunk(int x, int y, int[][] map) {
		server.addChunk(x, y, map);
	}


	/**
	 * set the player coordinate to given parameters
	 * @param i = screen cord
	 * @param y
	 */
	public synchronized void setPlayerCord(int i, int y) {
		this.spawned = true;
		this.spawn = y;
		server.setPlayerPos(i, y);
		pword = null;
		server.teleport(new Vector2(i, y));
	}

	/**
	 * send chat message to server
	 * @param msg
	 */
	public synchronized void chat(String msg) {
		PostChatPacket p = new PostChatPacket();
		p.make(msg);
		packets.add(p);
	}

	/**
	 * print chat message to console
	 * @param msg
	 */
	public void printChat(String msg) {
		server.printToConsole(msg);
	}
	
	/**
	 * check connection to server
	 */
	public void checkState() {
		
	}
	
	/**
	 * clear all packets from the packet buffer
	 */
	public void clearBuffer() {
		if (authkey != null && ssl != null) {
			pool.execute(new ClientSendThread(socket, packets, serverIp, port, ssl, authkey));
		}
	}
	
	/**
	 * locks or unlocks the physics engine
	 * @param lock
	 */
	public synchronized void setLock(boolean lock) {
		this.lock = lock;
	}
	
	public boolean getLock() {
		return lock;
	}
	
	/**
	 * sets the clients current state
	 * @param state
	 */
	public synchronized void setState(State state) {
		this.state = state;
	}
	
	/**
	 * sets the ssl key to the given value
	 * @param ssl2
	 */
	public synchronized void setSsl(byte[] ssl2) {
		ssl = ssl2;
	}
	
	public synchronized void setLogin(boolean isLoggedIn) {
		this.loggedIn = isLoggedIn;
	}
}
