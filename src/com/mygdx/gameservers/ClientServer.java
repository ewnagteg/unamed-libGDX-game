package com.mygdx.gameservers;

import java.net.InetAddress;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.gameobjects.Player;
import com.mygdx.gen.Chunk;
import com.mygdx.gui.Console;
import com.mygdx.networking.Client;

public class ClientServer extends GameServer {
	private Client client;
	private Player player;

	public ClientServer(Console console, String name, String worldName, Player player, InetAddress add, int port,
			String username, String password) {
		super(console, name, worldName, player);
		client = new Client(this, add, port, username, password);
		this.player = player;
	}

	@Override
	public void update(float dt) {
		if (entManager.getLock()) {
			entManager.setLock(client.getLock());
		}
		entManager.update(dt);
		// send player velocity and cord to server
		
		client.clearBuffer(); // send all packets
	}

	@Override
	public void chat(String msg) {
		client.chat(msg);
	}

	@Override
	public void connect() {
		entManager.lock();
		connected = false;
		client.run();
		client.connect();
	}

	@Override
	public void close() {
	}

	@Override
	public float getPlayerSpawn() {
		// send a get spawn packet to server
		return client.spawn;
	}

	@Override
	public void updatePlayerPosition(Vector2 pos) {
	}

	@Override
	public void loadChunk(int x, int y) {
		if (client.spawned) {
			client.loadChunk(x, y);
		}
	}

	/**
	 * should be called when a chunk is received,
	 * uses block cords
	 * @param x
	 * @param y
	 * @param map
	 */
	public void addChunk(int x, int y, int[][] map) {
		// Chunk c = chunks.obtain();
		Chunk c = new Chunk();
		c.setOffset(x, y);
		c.map = map;
		long id = makeChkId(x / 16, y / 16);
		chunkss.put(id, c);
	}

	@Override
	public void freeChunk(Chunk chk) {
		chunks.free(chk);
	}

	public void setPlayerPos(int i, int y) {
		player.cord.y = y;
	}

	public void start() {
		entManager.start();
	}

	public void printToConsole(String str) {
		console.print(str);
	}
}
