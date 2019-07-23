package com.mygdx.gen;

import com.mygdx.game.Settings;

public class SurfGen {
	private float NORM;
	private int CHKSIZE;
	public HashFunctions hash;

	public SurfGen() {
		this.hash = new HashFunctions();
		NORM = Settings.SURF_NORM;
		CHKSIZE = Settings.CHKSIZE;
	}

	public float[] eval(int bcord, float freqs[], float amps[], int seed) {
		return eval(bcord, freqs, amps, NORM, seed);
	}

	public float[] eval(int bcord, float freqs[], float amps[], float norm, int seed) {
		/*
		 * //takes base chunk cord returns float[16] of height cords
		 */
		float[] result = new float[CHKSIZE];
		float x;
		int xMin; // loop variables, bad practice but possibly marginlly faster
		int xMax;
		float t;
		float a;
		float b;
		for (int o = 0; o < freqs.length; o++) {
			for (int i = 0; i < CHKSIZE; i++) {
				x = (float) (bcord + i) / freqs[o];
				xMin = (int) x;
				if (bcord < 0)
					xMin -= 1; // hacky, but works
				xMax = xMin + 1;
				t = (x - (float) xMin);
				t = smoothstep(t);
				a = norm(hash.wraphash(xMin), NORM);
				b = norm(hash.wraphash(xMax), NORM);
				result[i] += amps[o] * lerp(a, b, t);
			}
		}
		return result;
	}

	private float lerp(float a, float b, float t) {
		return a * (1.0f - t) + b * t;
	}

	private float smoothstep(float t) {
		return t * t * (3 - 2 * t);
	}

	private float norm(int a, float n) {
		a = a % (int) n;
		return (float) a / n;
	}
}
