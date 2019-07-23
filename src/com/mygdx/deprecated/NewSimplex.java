package com.mygdx.deprecated;

import com.mygdx.game.Settings;
import com.mygdx.gen.HashFunctions;

/**
 * Based on public domain code by Stefan Gustavson (stegu@itn.liu.se)
 * optimizations by Peter Eastman (peastman@drizzle.stanford.edu). Better rank
 * ordering method by Stefan Gustavson in 2012.
 */
@Deprecated
public class NewSimplex {

	// Skewing and unskewing factors for 2 dimensions
	private static final float F2 = (float) (0.5 * (Math.sqrt(3.0) - 1.0));// to skew
	private static final float G2 = (float) ((3.0 - Math.sqrt(3.0)) / 6.0);// to unskew
	private int NORM = Settings.CAVE_NORM;
	private static int CHKSIZE = Settings.CHKSIZE;
	private HashFunctions hash;
	private Simplex simp = new Simplex();
	public NewSimplex() {
		this.hash = new HashFunctions();
	}

	/**
	 * Algorithm doesn't really care about cords,
	 * 
	 * @param xcord
	 * @param ycord
	 * @param freqs
	 * @param amps
	 * @param norm
	 * @return
	 */
	public float[][] gen(float xcord, float ycord, float[] freqs, float[] amps, int norm) {
		NORM = norm;
		float[][] ret = new float[CHKSIZE][CHKSIZE];
		int currentSeed = hash.seed;
		for (int o = 0; o < freqs.length; o++) {
			currentSeed = hash.wraphash(currentSeed);
			for (int x = 0; x < (CHKSIZE); x++) {
				for (int y = 0; y < (CHKSIZE); y++) {
					float ii = (float) x;
					float jj = (float) y;
					ret[x][y] += amps[o] * (eval((xcord + ii) / freqs[o], (ycord + jj) / freqs[o], currentSeed) + 1f);
				}
			}
		}
		return ret;
	}
	
	/**
	 * <i>currently broken, does not create good looking noise</i>
	 * @param x
	 * @param y
	 * @param ss
	 * @return
	 */
	public float eval(float x, float y, int ss) {
		// mostly copied from cited source

		float n0, n1, n2; // Noise contributions from the three corners
		// Skew the input space to determine which simplex cell we're in
		float s = (x + y) * F2; // Hairy factor for 2D
		int i = fastfloor(x + s);
		int j = fastfloor(y + s);
		float t = (i + j) * G2;
		float X0 = i - t; // Unskew the cell origin back to (x,y) space
		float Y0 = j - t;
		float x0 = x - X0; // The x,y distances from the cell origin
		float y0 = y - Y0;
		int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
		if (x0 > y0) {
			i1 = 1;
			j1 = 0;
		} // lower triangle, XY order: (0,0)->(1,0)->(1,1)
		else {
			i1 = 0;
			j1 = 1;
		} // upper triangle, YX order: (0,0)->(0,1)->(1,1)
		// A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
		// a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
		// c = (3-sqrt(3))/6
		float x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
		float y1 = y0 - j1 + G2;
		float x2 = x0 - 1.0f + 2.0f * G2; // Offsets for last corner in (x,y) unskewed coords
		float y2 = y0 - 1.0f + 2.0f * G2;

		// Calculate the contribution from the three corners
		float t0 = 0.5f - x0 * x0 - y0 * y0;
		if (t0 < 0)
			n0 = 0.0f;
		else {
			t0 *= t0;
			// instead of a perm table I use hashing
			n0 = t0 * t0 * dot((float) norm(hash.wraphash((int) i, ss), NORM),
					(float) norm(hash.wraphash((int) j, ss), NORM), x0, y0);
		}
		float t1 = 0.5f - x1 * x1 - y1 * y1;
		if (t1 < 0)
			n1 = 0.0f;
		else {
			t1 *= t1;
			n1 = t1 * t1 * dot((float) norm(hash.wraphash((int) i + i1, ss), NORM),
					(float) norm(hash.wraphash((int) j + j1, ss), NORM), x1, y1);
		}
		float t2 = 0.5f - x2 * x2 - y2 * y2;
		if (t2 < 0)
			n2 = 0.0f;
		else {
			t2 *= t2;
			n2 = t2 * t2 * dot((float) norm(hash.wraphash((int) i + 1, ss), NORM),
					(float) norm(hash.wraphash((int) j + 1, ss), NORM), x2, y2);
		}
		return 70.0f * (n0 + n1 + n2);
	}
	
	private float dot(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	private static int fastfloor(float x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}

	private float norm(int a, float n) {
		a = a % (int) n;
		return (float) a / n;
	}
}
