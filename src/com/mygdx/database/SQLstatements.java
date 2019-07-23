package com.mygdx.database;

public class SQLstatements {
	public String MAKE_CHUNK;
	public String INSERT_CHUNK;
	public final String INSERT_CHUNK_PS = "INSERT INTO CHUNKS VALUES (chunkid=?, ?);";

	public String CREATE_CHUNK_TABLE;
	public String GET_CHUNK;
	public String CHUNK_EXISTS;
	public static final String SET_BLOCK = "UPDATE CHUNKS SET ?=? WHERE chunkid=?;";

	public String CREATE_DATA_TABLE;
	public String DATA_TABLE_ENTRY_EXISTS;
	public String ADD_DATA_TABLE_ENTRY;
	public String GET_SEED;
	public String GET_DB_VERSION;
	public String GET_GEN_VERSION;

	public String CREATE_USER_TABLE;
	public String GET_SALT;
	public String GET_HASH;
	public String ADD_USER;
	public String USER_EXIST; // maybe

	/**
	 * uses string formatting, should use prepared statements, but im lazy so...
	 */
	public SQLstatements() {
		// ---user table stuff
		CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS USERDATA (username TEXT PRIMARY KEY, hash TEXT, salt TEXT);";

		GET_SALT = "SELECT salt FROM USERDATA WHERE username=?";

		GET_HASH = "SELECT hash FROM USERDATA WHERE username=?";
		USER_EXIST = "SELECT EXISTS(SELECT 1 FROM USERDATA WHERE username=?);";

		ADD_USER = "INSERT INTO USERDATA VALUES(?, ?, ?);";

		// ---data table stuff
		CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS DATA (id INTEGER PRIMARY KEY, "
				+ "version INTEGER DEFAULT 2, seed INTEGER DEFAULT 22, gen_version INTEGER DEFAULT 2);";
		/*
		 * CREATE_DATA_TABLE = create data table TABLE DATA key | version | seed |
		 * gen_version key = 1, unless you want to break stuff version = database
		 * standerd version number seed = master seed of the game gen_version = int
		 * version of gen
		 */

		DATA_TABLE_ENTRY_EXISTS = "SELECT EXISTS(SELECT 1 FROM DATA WHERE id=1)";
		ADD_DATA_TABLE_ENTRY = "INSERT INTO DATA VALUES (1, %d, %d, %d);";
		GET_SEED = "SELECT seed FROM DATA WHERE id=1;";
		GET_DB_VERSION = "SELECT version FROM DATA WHERE id=1";
		GET_GEN_VERSION = "SELECT gen_version FROM DATA WHERE id=1";

		// ---chunk table stuff---
		this.GET_CHUNK = "SELECT * FROM CHUNKS WHERE chunkid=%d;";
		INSERT_CHUNK = "INSERT INTO CHUNKS VALUES (%d,";

		CREATE_CHUNK_TABLE = "CREATE TABLE IF NOT EXISTS CHUNKS (chunkid INTEGER PRIMARY KEY, ";
		/*
		 * CREATE_CHUNK_TABLE = creates chunk table, all chunks are saved to this table
		 * CHUNKS chunkid | int data[256]
		 * 
		 * chunkid : see WorlData makeChkId(), is a long (ie 64 bit int
		 * 
		 * data : 256 ints where index = x + 16*y, ints decoded in Chunk column indexs
		 * are ints such as "1", "2" ect eg pos (0,0) is index 0 column "0" eg pos
		 * (14,7) is index 126 column "126"
		 */

		// adds the indexs to table
		for (int i = 0; i < 256; i++) {
			if (i != 255) {
				CREATE_CHUNK_TABLE += String.format("\"%d\" INTEGER DEFAULT 0, ", i);
			} else {
				CREATE_CHUNK_TABLE += String.format("\"%d\" INTEGER DEFAULT 0 ", i);
			}
		}
		CREATE_CHUNK_TABLE += ");";

		CHUNK_EXISTS = "SELECT EXISTS(SELECT 1 FROM CHUNKS WHERE chunkid=%d)";
	}

	public String makeInsertCommand(long id, int[][] chk) {
		String ret = String.format(INSERT_CHUNK, id);
		for (int i = 0; i < 256; i++) {
			if (i != 255) {
				ret += String.format("%d, ", chk[i % 16][i / 16]);
			} else {
				ret += String.format("%d ", chk[i % 16][i / 16]);
			}
		}
		ret += ");";
		return ret;
	}

	public String makeUpdateCommand(long id, int[][] chk) {
		return null;
	}

}
