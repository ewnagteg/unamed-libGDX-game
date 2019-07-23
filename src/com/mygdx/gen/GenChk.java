package com.mygdx.gen;

import com.mygdx.deprecated.Simplex;
import com.mygdx.game.Settings;

public class GenChk {

	//private NewSimplex noise2d;
	private Simplex noise2d;
	private SurfGen noise1d;
	public int seed;
	private int hashSeed;
	private HashFunctions hash;
	private Biomes biomes;

	public GenChk(int seed) {
		this.seed = seed;
		HashFunctions.seed = seed; // ugly
		this.noise1d = new SurfGen();
		//this.noise2d = new NewSimplex();
		this.noise2d = new Simplex();
		this.hash = new HashFunctions();
		this.hashSeed = hash.hash32shiftmult(seed);
		this.biomes = new Biomes(hash.hash32shiftmult(seed));
	}

	public float getSpawnHeight() {
		return ((float) Settings.BSIZE) * noise1d.eval(0, Settings.SURF_FREQS, Settings.SURF_AMPS, seed)[0]+Settings.PLAYER_SPAWN_HEIGHT;
	}

	/**
	 * uses block cords
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int[][] gen(int x, int y) { // world block cords
		int[][] chk = new int[Settings.CHKSIZE][Settings.CHKSIZE];
		biomes.genNoise(x, y);
		// noise gen
		float[][] gen = noise2d.gen(x, y, Settings.CAVE_FREQS, Settings.CAVE_AMPS, Settings.CAVE_NORM);
		float[] surf = noise1d.eval(x, Settings.SURF_FREQS, Settings.SURF_AMPS, seed);
		float[] stone = noise1d.eval(x, Settings.STONE[0], Settings.STONE[1], 8f, hashSeed);
		for (int i = 0; i < Settings.CHKSIZE; i++) {
			if ((int) surf[i] < y + Settings.CHKSIZE && (int) surf[i] > y) {
				int type = 2;
				if (biomes.getBiome(i, (int) surf[i] - y) == 2) {
					type = 6;
				} else if (biomes.getBiome(i, (int) surf[i] - y) == 1) {
					type = 2;
				}
				chk[i][(int) surf[i] - y] = type;
			}
		}

		for (int i = 0; i < Settings.CHKSIZE; i++) { // gen caves
			for (int j = 0; j < Settings.CHKSIZE; j++) {
				float t = getThresh((int) surf[i], j + y);
				if (gen[i][j] >= t && j + y < (int) surf[i]) {
					chk[i][j] = 0;
				} else if (j + y < (int) surf[i]) { // fill chunk
					if (j + y < (int) surf[i] - Settings.STONE_DEPTH + (int) stone[i]) {
						// add stone
						chk[i][j] = 3;
					} else {
						// add dirt
						int type = 1;
						if (biomes.getBiome(i, j) == 2) {
							type = 5;
						} else if (biomes.getBiome(i, j) == 1) {
							type = 1;
						}
						chk[i][j] = type;
					}
				}
			}
		}
		return chk;
	}

	private float getThresh(int surfH, int y) {
		if (y - surfH != 0)
			return Settings.CAVE_THRESH + 2f / (Math.abs(y - surfH));
		return 200;
	}
}
