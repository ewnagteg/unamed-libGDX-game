package com.mygdx.gen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Settings;
import com.mygdx.helpers.AssetLoader;

public class Chunk {
	public int[][] map;
	/**
	 * offset is a block coordinate
	 */
	private int[] offset;
	private final int CHKSIZE = Settings.CHKSIZE;

	public Chunk() {
		this.map = new int[Settings.CHKSIZE][Settings.CHKSIZE];
		map = new int[CHKSIZE][CHKSIZE];
		offset = new int[] { 0, 0 };
	}

	/**
	 * takes block cords
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInChunk(int x, int y) {
		if (offset[0] + 16 < x && x > offset[0] && offset[1] + 16 < y && y > offset[1]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * uses its own internal coordinates, ie setCord({15,15},4) sets the top right block to type 4
	 * @param cord
	 * @param type
	 */
	public void setCord(int[] cord, int type) {
		map[cord[0]][cord[1]] = type;
	}

	/**
	 * overloaded method. takes int[] or int x, int y or Vector2, does not convert
	 * coordinate expects a block coordinate. Old chunk use real world coordinates
	 * Note: Chunks are saved in db using chunk coordinates, ie not offset.x/16
	 * offset.y/16
	 * 
	 * @param off
	 */
	public void setOffset(int[] off) {
		offset = off;
	}

	public void setOffset(int x, int y) {
		offset = new int[] { x, y };
	}

	public void setOffset(Vector2 off) {
		int[] ioff = { (int) off.x, (int) off.y };
		setOffset(ioff);
	}

	/**
	 * returns the chunk offset, which is a block coordinate
	 * 
	 * @return int[2] = {x, y}
	 */
	public int[] getOffset() {
		return offset;
	}

	/**
	 * draws to the given spritebatch
	 */
	public void draw(SpriteBatch sp) {
		for (int i = 0; i < 256; i++) {
			int x;
			int y;
			x = i % 16;
			y = i / 16;
			try {
				if (map[x][y] != 0) {
					AssetLoader.spriteDict[map[x][y] - 1].setPosition((x + offset[0]) * Settings.BSIZE,
							(y + offset[1]) * Settings.BSIZE);
					AssetLoader.spriteDict[map[x][y] - 1].draw(sp);
				}
			} catch (Exception e) {
				Gdx.app.log("Chunk", "Illigal block type");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * uses internal block cords now
	 * @param x
	 * @param y
	 * @param type
	 */
	public void setBlock(int x, int y, int type) {
		int xc = x / 16;
		int yc = y / 16;
		int nx;
		int ny;
		if (x < 0) {
			nx = magic(x);
		} else {
			nx = Math.abs(Math.abs(x) - Math.abs(xc)*16);
		}
		if (y < 0) {
			ny = magic(y);
		} else {
			ny = Math.abs(Math.abs(y) - Math.abs(yc)*16);
		}
		try {
			map[nx][ny] = type;
		} catch (Exception e) {
			Gdx.app.log("Chunk", "Block cannot be set outside of chunk.map");
			Gdx.app.log("Chunk", "x,y" + x + ", " + y);
			e.printStackTrace();
		}
	}

	public int getBlock(int x, int y) {
		int xc = x / 16;
		int yc = y / 16;
		int nx;
		int ny;
		if (x < 0) {
			nx = magic(x);
		} else {
			nx = Math.abs(Math.abs(x) - Math.abs(xc)*16);
		}
		if (y < 0) {
			ny = magic(y);
		} else {
			ny = Math.abs(Math.abs(y) - Math.abs(yc)*16);
		}
		try {
			//map[nx][ny] = 4;
			return map[nx][ny];
		} catch (Exception e) {
			Gdx.app.log("Chunk", "block not in chunk");
			Gdx.app.log("Chunk", "x,y" + x + ", " + y);
			e.printStackTrace();
		}
		return 0;
	}

	private int magic(int x) {
		int cx = (x+1) / 16 - 1;
		return Math.abs(Math.abs(x)-Math.abs(cx*16));
	}
}
