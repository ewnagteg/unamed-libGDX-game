package com.mygdx.gen;

public class HashFunctions {
	public static int seed; // this is ugly

	public int hash32shift(int key) {
		key = ~key + (key << 15); // key = (key << 15) - key - 1;
		key = key ^ (key >>> 12);
		key = key + (key << 2);
		key = key ^ (key >>> 4);
		key = key * 2057; // key = (key + (key << 3)) + (key << 11);
		key = key ^ (key >>> 16);
		return key;
	}

	public int wraphash(int a, int s) {
		return hash32shiftmult(hash32shiftmult(a) + s);
	}

	public int wraphash(int a) {
		return hash32shiftmult(hash32shiftmult(a) + seed);
	}

	/*
	 * public int hash32shiftmult(int a) { a = (a+0x7ed55d16) + (a<<12); a =
	 * (a^0xc761c23c) ^ (a>>19); a = (a+0x165667b1) + (a<<5); a = (a+0xd3a2646c) ^
	 * (a<<9); a = (a+0xfd7046c5) + (a<<3); a = (a^0xb55a4f09) ^ (a>>16); return a;
	 * }
	 */
	public int hash32shiftmult(int key) {
		// https://gist.github.com/badboy/6267743
		int c2 = 0x27d4eb2d; // a prime or an odd constant

		key = (key ^ 61) ^ (key >>> 16);
		key = key + (key << 3);
		key = key ^ (key >>> 4);
		key = key * c2;
		key = key ^ (key >>> 15);
		return key;
	}
}
