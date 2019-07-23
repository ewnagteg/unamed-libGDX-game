package com.mygdx.gen;

import com.mygdx.deprecated.Simplex;

/**
 * the way this is supposed to work is
 * gen 3 types of 2d noise for every block in  a chunk,
 * the biome the block belongs to is determined by figuring 
 * out which section of a quarter 3d sphere it is part of.
 * ie it passes the noise values to some method that returns 
 * ie it works like minecraft's old biome system except with another axis
 * 
 * the biome number.
 * using 3 different noise values allows you to prevent unsimaler (desert and arctic) 
 * from being beside each other since 3d gives you more space to work with.
 * 
 */
public class Biomes {
	private float seed;
	private Simplex noise2d;
	private float[][] rain = null;
	private float[][] heat = null;
	private final float[] freqs;
	private final float[] amps;
	
	public Biomes(int seed) {
		this.seed = seed;
		this.noise2d = new Simplex();
		this.freqs = new float[] { 1024 , 1};
		this.amps = new float[] { 1 , 0.125f};
	}

	public void genNoise(int x, int y) {
		// gen rain and heat data
		rain = noise2d.gen(x, y, freqs, amps, 128);
	}

	/**
	 * uses chunk offset relative cords
	 * 1 = normal biome
	 * 2 = savannah
	 * @param x
	 * @param y
	 * @return
	 */
	public int getBiome(int x, int y) {
		return rain[x][y] < 0.6f ? 2 : 1;
	}
}
