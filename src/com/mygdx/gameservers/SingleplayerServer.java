package com.mygdx.gameservers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.database.WorldData;
import com.mygdx.game.Settings;
import com.mygdx.gameobjects.Player;
import com.mygdx.gen.Chunk;
import com.mygdx.gen.GenChk;
import com.mygdx.gui.Console;

public class SingleplayerServer extends GameServer {
	private WorldData db;
	private GenChk gen;
	private int seed;

	public SingleplayerServer(Console console, String name, String worldName, Player player) {
		super(console, name, worldName, player);
		db = new WorldData(worldName, "not rpi");
		db.setWorld(worldName);
		db.connect();
		db.makeDB();
		db.conf();
		seed = db.getSeed();
		Gdx.app.log("Seed", "" + seed);
		gen = new GenChk(seed);
		player.setTo(0f, gen.getSpawnHeight());
		
		// should load js
	}

	@Override
	public void connect() {
		teleport(new Vector2(player.cord));
		this.entManager.start();
		connected = true;
	}

	@Override
	public void setBlock(int x, int y, int type) {
		int xc = x / Settings.BSIZE / 16;
		xc = x < 0 ? xc - 1 : xc;
		int yc = y / Settings.BSIZE / 16;
		yc = y < 0 ? yc - 1 : yc;
		long id = makeChkId(xc, yc);
		if (chunkss.containsKey(id)) {
			Chunk c = chunkss.get(id);
			System.out.println("good");
			int nx = Math.abs(Math.abs(x/Settings.BSIZE)-Math.abs(xc)*16)%16;
			int ny = Math.abs(Math.abs(y/Settings.BSIZE)-Math.abs(yc)*16)%16;
			System.out.println("nx: "+nx+ "  ny: "+ny+"  x: "+x+"  y: "+y);
			c.setBlock(x, y, type);
		}
	}

	@Override
	public void close() {
		db.close();
	}

	@Override
	public void updatePlayerPosition(Vector2 pos) {
	}

	@Override
	public void loadChunk(int x, int y) {
		if (db.isChunkSaved(16 * x, 16 * y)) {
			addChunk(x, y, db.loadChunk(16 * x, 16 * y));
		} else {
			int[][] map = gen.gen(x * 16, y * 16);
			createChunk(x, y, map);
		}
	}

	private void addChunk(int x, int y, int[][] map) {
		Chunk c = chunks.obtain();
		c.map = map;
		c.setOffset(x * 16, y * 16);
		chunkss.put(makeChkId(x, y), c);
	}

	private void createChunk(int x, int y, int[][] map) {
		Chunk c = chunks.obtain();
		c.map = map;
		c.setOffset(x * 16, y * 16);
		db.createChunk(16 * x, 16 * y, map);
		chunkss.put(makeChkId(x, y), c);
	}

	@Override
	public float getPlayerSpawn() {
		return gen.getSpawnHeight() + Settings.PLAYER_SPAWN_HEIGHT;
	}

}
