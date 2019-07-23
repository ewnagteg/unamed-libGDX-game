package com.mygdx.game;

public class Settings {

// world settings
	public static final int CHKSIZE = 16;
	public static final int BSIZE = 16;

// screen
	public static final int sH = 600;
	public static final int sW = 800;

// player settings
	public static final boolean ALLOW_PLAYER_ROTATION = true;
	public static final boolean FLY = true;
	public static final float CHUNK_SCROLL_X = 256; // number of world units player can
													// move before scrolling L or R
	public static final float CHUNK_SCROLL_Y = 256;
	public static final float PLAYER_SPAWN_HEIGHT = 64f;
	public static final int VIEW = 6; // the size of the view screen we get

// gen settings

	// cave gen settings
	public static final float CAVE_THRESH = 1.5f;
	public static final float[] CAVE_AMPS = { 1f, .25f, 0.0625f };
	public static final float[] CAVE_FREQS = { 128f, 32f, 2f };
	public static final int CAVE_NORM = 512;

	// surf gen settings
	public static final float SURF_NORM = 64f;
	public static final float[] SURF_AMPS = { 0.5f, 16f, 1024f };
	public static final float[] SURF_FREQS = { 2f, 32f, 1024f * 1024f };

	public static int STONE_DEPTH = 10;
	public static final float[][] STONE = { { 1f }, { 1.5f } }; // Stone : { {freqs}, {amps} }

// DB settings
	public static final int DB_VERSION = 2;
	public static final int GEN_VERSION = 2;

// networking settings
	public static final int UDP_BUFF_SIZE = 4096;

	public static final String DEFAULT_PORT = "8000";
}
