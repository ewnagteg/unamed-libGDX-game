package com.mygdx.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mygdx.database.WorldData;
import com.mygdx.game.Settings;

public class Server extends Thread {

	private int serverPort;
	private boolean isStopped = false;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private WorldData db;
	private byte[] buffer;
	private int seed;
	private UserInfo users = new UserInfo();
	private static String os = "not rpi"; // should not exist
	private int buffsize = 125829120;
	private final ExecutorService pool;
	private final ScheduledExecutorService schedule;
	private ConcurrentLinkedQueue<String> chatQueue;
	private EntityData entitys;
	
	// js engine
	private ScriptEngineManager mgr;
	private Bindings bindings;
	private ScriptEngine engine;
	public Server(int port, String worldName) {
		this.serverPort = port;
		this.buffer = new byte[Settings.UDP_BUFF_SIZE];
		packet = new DatagramPacket(buffer, buffer.length);
		System.out.println("Server Starting server using world " + worldName);
		db = new WorldData(worldName, os);
		db.connect();
		db.conf();
		db.makeDB();
		seed = db.getSeed();
		pool = Executors.newFixedThreadPool(256);
		schedule = Executors.newScheduledThreadPool(1);
		chatQueue = new ConcurrentLinkedQueue<String>();
		entitys = new EntityData();
		
		// add javascript support
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("nashorn");
		bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("users", users);
		bindings.put("entitys", entitys);
		bindings.put("chatQueue", chatQueue);
		
		// eval main script
		try {
			engine.eval(new FileReader(new File("./assets/scripts/main.js")));
		} catch (FileNotFoundException | ScriptException e) {
			System.out.println("Error excecuting main script");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("starting server on port: " + serverPort + "\n------------------------------\n\n");
		openServerSocket();
		schedule.scheduleAtFixedRate(new SendUpdatesThread(socket, users, chatQueue, entitys), 100, 20, TimeUnit.MILLISECONDS);
		while (!isStopped()) {
			try {
				socket.receive(packet);
			} catch (IOException e) {
				System.out.println("failed to recieve packet");
				System.out.println(e.getMessage());
			}
			//new Thread(new NewClientThread(socket, users, packet.getData(), packet.getAddress(), packet.getPort(), db, seed, chatQueue)).start();;
			pool.execute(
					new NewClientThread(socket, users, packet.getData(), packet.getAddress(), packet.getPort(), db, seed, chatQueue, entitys));
		}
		System.out.println("Server Stopped.");
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	private void openServerSocket() {
		try {
			socket = new DatagramSocket(serverPort);
			// socket.setReceiveBufferSize(buffsize);
			// socket.setSendBufferSize(buffsize);
		} catch (SocketException e) {
			System.out.println("failed to create socket");
			System.out.println(e.getMessage());
		}
	}
	
	public void dispose() {
		pool.shutdown();
		schedule.shutdown();
		socket.close();
	}

}
