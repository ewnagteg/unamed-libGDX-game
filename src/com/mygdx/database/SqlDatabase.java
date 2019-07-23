package com.mygdx.database;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.badlogic.gdx.Gdx;

public class SqlDatabase {
	private static String path; // path to current db
	public Connection conn;

	public SqlDatabase() {
		path = new String();
	}

	private void setPath(String newPath) {
		path = newPath;
	}
	/**
	 * gets the path to the given world
	 * assumes path ./worlds/name.sql where name = given parameter
	 * is also hacky in that if <code>Gdx.files</code> throws a <code>NullPointerException</code>, it will try
	 * with <code>java.nio.Paths</code> instead. This is because the console server doesn't start libGdx,
	 * because libGdx does not support the Rpi.
	 * @param name
	 */
	public void getPath(String name) {
		try {
			String localRoot = Gdx.files.getLocalStoragePath();
			String pathW = localRoot + "worlds\\" + name + ".sql";
			setPath(pathW);
		} catch (NullPointerException ex) {
			System.out.println("failed to get path with GDX, trying with java.nio.file.Paths..");
			String localRoot = Paths.get(".").toAbsolutePath().normalize().toString();
			System.out.println("local root is: " + localRoot);
			String pathW = localRoot + "\\assets\\worlds\\" + name + ".sql";
			setPath(pathW);
		} catch (Exception ex) {
			System.out.println("SQL: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void connect() {
		try {
			// db parameters
			String url = "jdbc:sqlite:" + path;
			// create a connection to the database
			conn = DriverManager.getConnection(url);

			System.out.println("SQL: " + "Connection opened");

		} catch (SQLException ex) {
			System.out.println("SQL: " + ex.getMessage());
		}
	}

	public void executeSQL(String sql) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException ex) {
			System.out.println("SQL: " + ex.getMessage());
		}
	}

	public ResultSet executeQuery(String sql) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException ex) {
			System.out.println("SQL: " + ex.getMessage());
			return null;
		}
	}

	public void close() {
		try {
			if (conn != null) {
				conn.close();
				System.out.println("SQL:  Connection closed");
			}
		} catch (SQLException ex) {
			System.out.println("SQL: " + ex.getMessage());
		}
	}
}
