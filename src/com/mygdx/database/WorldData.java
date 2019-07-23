package com.mygdx.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mygdx.game.Settings;

public class WorldData {
	private SqlDatabase sql;
	private SQLstatements sqlCode;

	public WorldData(String name, String os) {
		sql = new SqlDatabase();
		sql.getPath(name);
		sqlCode = new SQLstatements();
	}

	/**
	 * sets the name of the world and gets the path to that world
	 * 
	 * @param name
	 */
	public void setWorld(String name) {
		System.out.println("Server" + name);
		sql.getPath(name);
	}

	/**
	 * connects to the sql database, the world name has to be set before this
	 */
	public void connect() {
		sql.connect();
	}

	/**
	 * closes sql database
	 */
	public void close() {
		sql.close();
	}
	
	public String getHash(String uname) {
		try {
			PreparedStatement pstmt = sql.conn.prepareStatement(sqlCode.GET_HASH);
			pstmt.setString(1, uname);
			ResultSet results = pstmt.executeQuery();
			return results.getString("hash");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean userExist(String uname) {
		try {
			PreparedStatement pstmt = sql.conn.prepareStatement(sqlCode.USER_EXIST);
			pstmt.setString(1, uname);
			ResultSet results = pstmt.executeQuery();
			return results.getBoolean(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void addUser(String uname, String hash, String salt) {
		try {
			PreparedStatement pstmt = sql.conn.prepareStatement(sqlCode.ADD_USER);
			pstmt.setString(1, uname);
			pstmt.setString(2, hash);
			pstmt.setString(3, salt);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getSalt(String uname) {
		try {
			PreparedStatement pstmt = sql.conn.prepareStatement(sqlCode.GET_SALT);
			pstmt.setString(1, uname);
			ResultSet results = pstmt.executeQuery();
			String salt = results.getString("salt");
			return salt;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * sets the seed of the world, will create data table if it does not exist
	 * 
	 * @param Seed
	 * @return
	 */
	public boolean addSeed(int Seed) {
		String code = sqlCode.DATA_TABLE_ENTRY_EXISTS;
		ResultSet set = sql.executeQuery(code);
		try {
			if (!set.getBoolean(1)) {
				code = String.format(sqlCode.ADD_DATA_TABLE_ENTRY, Settings.DB_VERSION, Seed, Settings.GEN_VERSION);
				sql.executeSQL(code);
			}
		} catch (SQLException ex) {
			System.out.println("SQL Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		return false;
	}

	public int getSeed() {
		String code = sqlCode.GET_SEED;
		ResultSet set = sql.executeQuery(code);
		try {
			return set.getInt(1);
		} catch (SQLException ex) {
			System.out.println("SQL Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		return 0;
	}

	/**
	 * x,y are chunk cords
	 * 
	 * @param x chunk cord
	 * @param y chunk cord
	 * @return boolean
	 */
	public boolean isChunkSaved(int x, int y) {
		long id = makeChkId(x, y);
		String code = String.format(sqlCode.CHUNK_EXISTS, id);
		ResultSet set = sql.executeQuery(code);
		try {
			return set.getBoolean(1);
		} catch (SQLException ex) {
			System.out.println("SQL Error: " + ex.getMessage());
		}
		return false;
	}

	/**
	 * x,y are chunk cords
	 * 
	 * @param x    chunk cord
	 * @param y    chunk cord
	 * @param data
	 */
	public void createChunk(int x, int y, int[][] data) {
		long chunkid = makeChkId(x, y);

		String code = sqlCode.makeInsertCommand(chunkid, data);
		sql.executeSQL(code);
	}

	/**
	 * x,y are chunk cords
	 * 
	 * @param x chunk cord
	 * @param y chunk cord
	 * @return
	 */
	public int[][] loadChunk(int x, int y) {
		long id = makeChkId(x, y);
		String cmd = String.format(sqlCode.GET_CHUNK, id);
		int[][] map = new int[16][16];
		ResultSet set = sql.executeQuery(cmd);
		try {
			for (int i = 0; i < 256; i++) {
				map[i % 16][i / 16] = (int) set.getInt(String.format("%d", i));
			}
		} catch (SQLException ex) {
			System.out.println("SQL Error: " + ex.getMessage());
			return null;
		}
		return map;
	}

	/**
	 * maps the two chunk cord ints to a long used internally
	 * 
	 * @param x chunk cord
	 * @param y chunk cord
	 * @return long chunk id
	 */
	private long makeChkId(int x, int y) {
		long ret = (((long) x) << 32) | (y & 0xffffffffL);
		return ret;
	}

	private int[] chkIdToCord(long l) {
		int[] x = { (int) (l >> 32), (int) l };
		return x;
	}

	/**
	 * configures the sql database
	 */
	public void conf() {
		sql.executeSQL("PRAGMA synchronous = OFF");
		sql.executeSQL("PRAGMA journal_mode = MEMORY");
	}

	/**
	 * sets up the sql database
	 */
	public void makeDB() {
		sql.executeSQL(sqlCode.CREATE_CHUNK_TABLE);
		String code = String.format(sqlCode.CREATE_DATA_TABLE); // ??
		sql.executeSQL(code);
		sql.executeSQL(sqlCode.CREATE_USER_TABLE);
		conf();
	}

	private void deleteCHUNKS() {
		sql.executeSQL("DROP TABLE CHUNKS;");
	}

	/**
	 * deletes all tables and then recreates them
	 */
	public void resetDB() {
		connect();
		deleteCHUNKS();
		makeDB();
		close();
	}

	/**
	 * executes the VAVUUM command
	 */
	public void cleanDB() {
		sql.executeSQL("VACUUM;");
	}

	public void changeBlock(int x, int y, int type) {

	}

}
