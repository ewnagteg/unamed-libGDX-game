package com.mygdx.gameservers;

import java.util.concurrent.ConcurrentHashMap;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Settings;
import com.mygdx.gameobjects.Player;
import com.mygdx.gen.Chunk;
import com.mygdx.gui.Console;
import com.mygdx.physicsengine.EntityManager;

public class GameServer {
	protected Array<Chunk> magicArray;
	protected Pool<Chunk> chunks;
	protected Vector2 orgin = new Vector2(0, 0);
	protected int[] veiwOffset;
	protected int[] veiw;
	protected String worldName;
	protected ConcurrentHashMap<Long, Chunk> chunkss;
	protected EntityManager entManager;
	public boolean connected;
	protected Player player;
	private int xBoxSize;
	private float yBoxSize;
	protected Console console;
	
	protected ScriptEngineManager mgr;
	protected ScriptEngine engine;
	protected Bindings bindings;
	public GameServer(Console console, String name, String worldName, Player player) {
		this.player = player;
		this.entManager = new EntityManager(this);
		entManager.addEntity(player);
		veiwOffset = new int[] { 0, 0 };
		veiw = new int[] { 0, 0, Settings.VIEW, Settings.VIEW };
		xBoxSize = 256;
		yBoxSize = 256;
		chunkss = new ConcurrentHashMap<Long, Chunk>(64);
		connected = true;
		this.console = console;
		this.worldName = worldName;
		chunks = new Pool<Chunk>() {
			@Override
			protected Chunk newObject() {
				Chunk c = new Chunk();
				return c;
			}

			@Override
			protected void reset(Chunk c) {
			}
		};
		
		// load nashorn
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("nashorn");
		bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
	}

	public float[] center() {
		return new float[] { veiw[0] + (float) veiw[2] / 2f, (float) veiw[1] + (float) veiw[3] / 2f };
	}

	public void printToConsole(String str) {
		console.print(str);
	}

	public void chat(String chat) {
		// handle chat data/command
		Gdx.app.log("Error:", " unimplemented method called");
	}

	public boolean isInRect(int x, int y, int[] rect) {
		if (x >= rect[0] && x <= rect[0] + rect[2] && y >= rect[1] && y <= rect[1] + rect[3]) {
			return true;
		}
		return false;
	}

	/**
	 * uses a screen cord
	 * 
	 * @param ppos
	 */
	public void teleport(Vector2 ppos) {
		int[] rect = new int[4];
		rect[2] = veiw[2];
		rect[3] = veiw[3];
		rect[0] = (int) ((ppos.x - rect[2] * Settings.BSIZE * 16f / 2f) / Settings.BSIZE) / 16;
		rect[1] = (int) ((ppos.y - rect[3] * Settings.BSIZE * 16f / 2f) / Settings.BSIZE) / 16;
		setPlayerVeiw(rect);
	}

	protected void setPlayerVeiw(int[] rect) {
		veiw = rect;
		veiwOffset[0] = rect[0];
		veiwOffset[1] = rect[1];
		for (int i = 0; i < rect[3]; i++) {
			for (int j = 0; j < rect[2]; j++) {
				long id = makeChkId(rect[0] + j, rect[1] + i);
				if (!chunkss.containsKey(id)) {
					loadChunk(rect[0] + j, rect[1] + i);
				}
			}
		}
		for (long id : chunkss.keySet()) {
			int[] cord = chkIdToCord(id);
			if (!isInRect(cord[0], cord[1], rect)) {
				freeChunk(chunkss.get(id));
				chunks.free(chunkss.get(id));
				chunkss.remove(id);
			}
		}
	}

	/**
	 * takes block cords
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getBlock(int x, int y) {
		int xc = x / 16;
		int yc = y / 16;
		xc = x < 0 ? xc - 1 : xc;
		yc = y < 0 ? yc - 1 : yc;
		long id = makeChkId(xc, yc);
		if (chunkss.containsKey(id)) {
			Chunk c = chunkss.get(id);
			return c.getBlock(x, y);
		}
		return 0;
	}

	public void connect() {
	}

	public void close() {
	}

	/**
	 * 
	 * @param x    block coordinate
	 * @param y
	 * @param type
	 */
	public void setBlock(int x, int y, int type) {
		//
	}

	public void updatePlayerPosition(Vector2 pos) {

	}

	protected long makeChkId(int x, int y) {
		long ret = (((long) x) << 32) | (y & 0xffffffffL);
		return ret;
	}

	protected int[] chkIdToCord(long l) {
		int[] x = { (int) (l >> 32), (int) l };
		return x;
	}

	/**
	 * Should use chunk cords
	 * 
	 * @param x
	 * @param y
	 */
	public void loadChunk(int x, int y) {
		Gdx.app.log("Error:", " unimplemented method called");
	}

	public void freeChunk(Chunk chk) {
	}

	public float getPlayerSpawn() {
		return 0f;
	}

	public void draw(SpriteBatch sp) {
		for (long id : chunkss.keySet()) {
			chunkss.get(id).draw(sp);
		}
		entManager.draw(sp);
	}

	public void dispose() {
		// close sockets ect
	}

	public void update(float dt) {
		entManager.update(dt);
	}

	public void addPlayer(Player player) {

	}

	public boolean checkSpawned() {
		return true;
	}

	public void scroll(Vector2 ppos) {
		if (Math.abs(center()[0] * Settings.BSIZE * 16 - ppos.x) > xBoxSize) {
			teleport(ppos);
		} else if (Math.abs(center()[1] * Settings.BSIZE * 16 - ppos.y) > yBoxSize) {
			teleport(ppos);
		}
	}

	public void lock() {
		entManager.lock();
	}
	
	public void start() {
		entManager.start();
	}

	/**
	 * this sets every block in a chunk, is for debugging
	 * @param x
	 * @param y
	 */
	public void setChk(int x, int y) {
		long id = this.makeChkId(x, y);
		Chunk chk = chunkss.get(id);
		for (int i=0; i<256; i++) {
			chk.setCord(new int[] {i%16,  i/16}, 4);
		}
	}
}
