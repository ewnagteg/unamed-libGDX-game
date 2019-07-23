package com.mygdx.gen;

import com.mygdx.deprecated.NewSimplex;

public class OreGen {
	// add conf
	private HashFunctions hash;
	private NewSimplex noise2d;
	private float[] freqs = { 1f };
	private float[] amps = { 1f };

	public OreGen(int seed) {
		this.noise2d = new NewSimplex();
		// init stuff
		this.hash = new HashFunctions();

	}

	public int[][] gen(float x, float y, int[][] chk, int[] surf) {
		float[][] noise = noise2d.gen(x, y, freqs, amps, 16);
		for (int i = 0; i < 256; i++) {
			chk[i % 16][i / 16] = thresh(noise[i % 16][i / 16], surf[i] - (int) y + i / 16);
		}
		return chk;
	}

	public int thresh(float noise, int y) {
		if (y < 0) {
			return 0;
		} else if (noise > 0.6f) {
			return 1;
		} else {
			return 0;
		}
	}
}
