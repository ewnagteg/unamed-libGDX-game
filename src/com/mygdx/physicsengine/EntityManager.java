package com.mygdx.physicsengine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Settings;
import com.mygdx.gameservers.GameServer;

public class EntityManager {
	private Array<Entity> ents;
	private Array<Entity> npcs; // Probably should use a concurrent hash map
	private GameServer server;
	private boolean lock;
	private Rectangle block;

	public EntityManager(GameServer server) {
		this.server = server;
		ents = new Array<Entity>(); // these are all the ents that the client does physics for
		lock = false;
		block = new Rectangle();

	}

	public void addEntity(Entity ent) {
		ents.add(ent);
	}

	public void update(float dt) {
		if (!lock) {
			for (Entity ent : ents) {
				updateEnt(dt, ent);
			}
		}
	}

	private void updateEnt(float dt, Entity ent) {
		int cx = ((int) ent.cord.x + ent.width / 2) / Settings.BSIZE - 1;
		int cy = ((int) ent.cord.y + ent.height / 2) / Settings.BSIZE + 1;
		int[][] map = new int[3][3];
		ent.updateX(dt);
		for (int i = 0; i < 9; i++) {
			int b = server.getBlock(cx + i % 3, cy - i / 3);
			map[i%3][i/3] = b;
			if (b != 0) {
				int x = (cx + i % 3) * Settings.BSIZE;
				int y = (cy - i / 3) * Settings.BSIZE;
				block.set(x, y, Settings.BSIZE, Settings.BSIZE);
				ent.collideX(block, dt);
			}
		}
		ent.updateY(dt);
		for (int i = 0; i < 9; i++) {
			if (map[i%3][i/3] != 0) {
				int x = (cx + i % 3) * Settings.BSIZE;
				int y = (cy - i / 3) * Settings.BSIZE;
				block.set(x, y, Settings.BSIZE, Settings.BSIZE);
				ent.collideY(block, dt);
			}
		}
	}
	
	/**
	 * called by ClientServer to update server entitys.
	 */
	public void updateNPCS(float dt) {
		for (Entity ob : npcs) {
			ob.updateX(dt);
			ob.updateY(dt);
		}
	}
	
	public void draw(SpriteBatch sp) {
		for (Entity ob : ents) {
			ob.draw(sp);
		}
	}

	public void lock() {
		lock = true;
	}

	public void start() {
		lock = false;
	}
	
	/**
	 * returns true if world locked else false
	 * @return
	 */
	public boolean getLock() {
		return lock;
	}
	
	public void setLock(boolean l) {
		lock = l;
	}
}
