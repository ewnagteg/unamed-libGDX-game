package com.mygdx.consoleserver;


import com.mygdx.database.WorldData;
import com.mygdx.server.Server;

public class ServerConsole extends Console {
	private String worldName;
	private int port = 8000;
	private WorldData db;
	private Server server;
	public ServerConsole(String[] args) {
		super();
		db = new WorldData(worldName, "not rpi");
		bindings.put("db", db);
		bindings.put("wrapper", new JavascriptWrapper(this));
		bindings.put("args", args);
		runScript("startup");
		bindings.remove("args");
	}

	public static void main(String[] args) {
		ServerConsole console = new ServerConsole(args);
		console.run();
	}
	
	public void setToServer() {
		bindings.remove("db");
		server = new Server(port, worldName);
		new Thread(server).run();
		// server.run();
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setWorld(String n) {
		this.worldName = n;
	}
}
