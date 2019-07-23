package com.mygdx.gui;

import com.badlogic.gdx.Gdx;
import com.mygdx.networking.Packets;
import com.mygdx.server.Server;

public class ServerCommands extends Console {
	private int port = 8000;
	private Thread serverThread;
	private String worldName;
	public ServerCommands() {
		super();
		worldName = "world";
	}

	@Override
	public void excecute(String text) {
		try {
			String[] comms = getComms();
			if (comms[0].contentEquals("setport")) {
				port = Integer.parseInt(comms[1]);
				print("set port to: " + port);
			} else if (comms[0].contentEquals("run")) {
				serverThread = new Thread(new Server(port, worldName));
				serverThread.start();
				print("starting server");

			} else if (comms[0].contentEquals("close")) {
				serverThread.interrupt();
				print("interrupted server");
			} else if (comms[0].contentEquals("exit")) {
				serverThread.interrupt();
				print("exiting...");
				Gdx.app.exit();
			} else if (comms[0].contentEquals("set")) {
				if (comms[1].contentEquals("world")) {
					worldName = comms[2];
					print("Set world to: " + worldName);
				}
			} else if (comms[0].contentEquals("insertuser")) {
				String uname = comms[1];
				String pword = comms[2];
				byte[] bsalt = Packets.salt();
				String salt = new String(bsalt);
				String hash = new String(Packets.hash(pword.getBytes(), bsalt));
				print("inserting:   uname=" + uname + ",   hash=" + hash + ",   salt=" + salt);
			} else {
				print("unknown command: " + comms[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Gdx.app.log("Server Console", e.getMessage());
			print("Error: " + e.getMessage());
		}

	}
}
