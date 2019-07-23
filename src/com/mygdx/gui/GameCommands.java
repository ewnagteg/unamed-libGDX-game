package com.mygdx.gui;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Settings;
import com.mygdx.gameobjects.Player;
import com.mygdx.gameworld.GameWorld;

public class GameCommands extends Console {
	private GameWorld world;
	private Player player;
	public GameCommands(GameWorld world) {
		super();
		this.world = world;
		player = world.getPlayer();
	}

	@Override
	public void excecute(String text) {
		try {
			String[] comms = getComms();
			if (comms[0].contains("setport")) {
				print("unsupported command");
			} else if (comms[0].contentEquals("set")) {
				if (comms[1].contentEquals("blockp")) {
					int x = this.getInt(comms[2]);
					int y = this.getInt(comms[3]);
					int type = this.getInt(comms[4]);
					world.setBlockRelToPlayer(x, y, type);
					print("setting block...");
				} else if (comms[1].contentEquals("speed")) {
					float speed = Float.parseFloat(comms[2]);
					player.setSpeed(speed);
					print("setting player speed to: "+speed);
				} else if (comms[1].contentEquals("jmult")) {
					float mult = Float.parseFloat(comms[2]);
					player.setJumpMult(mult);
					print("setting jump mult...");
				} else if (comms[1].contentEquals("fmult")) {
					float mult = Float.parseFloat(comms[2]);
					player.setFallMult(mult);
					print("setting fall mult...");
				} else if (comms[1].contentEquals("g") || comms[1].contentEquals("grav")) {
					float mult = Float.parseFloat(comms[2]);
					player.setFallMult(mult);
					print("setting grav...");
				}
			} else if (comms[0].contentEquals("exit")) {
				print("exiting...");
				Gdx.app.exit();
			} else if (comms[0].contentEquals("teleport")) {
				print("teleporting...");
				int x = getInt(comms[1]);
				int y = getInt(comms[2]);
				player.teleport(x, y);
			} else if(comms[0].contentEquals("fly")) {
				player.setFly(true);
				print("set fly = true");
			} else if(comms[0].contentEquals("revokefly")) {
				player.setFly(false);
				print("set fly = false");
			} else if (comms[0].contentEquals("lock")) {
				//stop the physics engine
				world.lockserver();
				print("locked world");
			} else if(comms[0].contentEquals("start")) {
				//start the physics engine
				world.start();
				print("started physics engine");
			} else if (comms[0].contentEquals("/") || comms[0].contentEquals(";")) {
				String msg = "";
				for (int i = 1; i < comms.length; i++) {
					msg += " ";
					msg += comms[i];
				}
				world.sendChat(msg);
				print("sending message: " + msg);
			} else if (comms[0].contentEquals("getchk")) {
				int x = getInt(comms[1]);
				int y = getInt(comms[2]);
				print("loading chunk: " + comms[1] + "  " + comms[2]);
				world.server.loadChunk(x, y);
			} else if (comms[0].contentEquals("ldap")) {
				int[] cord = world.getPlayerChunk();
				print("current chunk that player is in: " + cord[0] + "  " + cord[1]);
			} else if (comms[0].contentEquals("setchk")) {
				int x = getInt(comms[1]);
				int y = getInt(comms[2]);
				world.server.setChk(x, y);
				print("setting chunk");
			} else if (comms[0].contentEquals("pcord")) {
				print("player block cord x: "+(int)world.getPlayer().cord.x/Settings.BSIZE+ " y: "+(int)world.getPlayer().cord.y/Settings.BSIZE);
			} else {
				print("unknown command: " + comms[0]);
			}
		} catch (Exception e) {
			Gdx.app.log("Client", e.getMessage());
			print("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
