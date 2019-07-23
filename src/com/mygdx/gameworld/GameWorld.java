package com.mygdx.gameworld;

import java.net.InetAddress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Settings;
import com.mygdx.gameobjects.Player;
import com.mygdx.gameservers.ClientServer;
import com.mygdx.gameservers.GameServer;
import com.mygdx.gameservers.SingleplayerServer;
import com.mygdx.gui.Console;

public class GameWorld {
	private Player player;
	public GameServer server; // should be private, just made it public for debugging
	private String worldName;
	private InetAddress add;
	private int port;
	private String uname;
	private String password;
	private String serverType;

	public GameWorld(String worldName, String serverType, InetAddress add, int port, String username, String password) {
		this.worldName = worldName;
		player = new Player();
		this.port = port;
		this.add = add;
		this.uname = username;
		this.password = password;
		this.serverType = serverType;
	}

	public void passConsole(Console console) {
		// pass console to game server
		setServer(console, serverType, add, port, uname, password);
		password = null;
	}

	public void setServer(Console console, String mode, InetAddress add, int port, String username, String password) {
		if (mode.equals("SinglePlayer")) {
			server = new SingleplayerServer(console, "server", worldName, player);
			player.setCord(0f, server.getPlayerSpawn());
			server.connect();
			Gdx.app.log("player cord: ", "" + player.cord.y + " " + player.cord.x);
		} else if (mode.contentEquals("Multiplayer")) {
			server = new ClientServer(console, "server", worldName, player, add, port, username, password);
			server.connect();
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void update(float dt) {
		scroll();
		server.update(dt);
	}

	private void scroll() {
		server.scroll(player.cord);
	}

	public void draw(SpriteBatch sp) {
		server.draw(sp);
	}

	public void dispose() {
		server.dispose();
	}

	public void mouseAction(int x, int y, int button) {
		// To do: check if in range of player
		// set block
		server.setBlock(x, y, 4);
	}

	public void setBlockRelToPlayer(int x, int y, int type) {
		server.setBlock(x + (int) player.cord.x, y + (int) player.cord.y, type);
	}

	public void lockserver() {
		server.lock();
	}

	public void sendChat(String msg) {
		server.chat(msg);
	}

	public int[] getPlayerChunk() {
		int[] cord = new int[2];
		cord[0] = ((int) player.cord.x) / Settings.BSIZE / 16;
		cord[0] = player.cord.x < 0 ? cord[0] - 1 : cord[0];
		cord[1] = ((int) player.cord.y) / Settings.BSIZE / 16;
		cord[1] = player.cord.y < 0 ? cord[1] - 1 : cord[1];
		return cord;
	}

	public void start() {
		server.start();
	}

	public void pause() {
		server.lock();
	}

	public void resume() {
		server.start();
	}

	public void resize(int w, int h) {
		// TO DO
	}
}
