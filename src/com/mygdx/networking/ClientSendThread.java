package com.mygdx.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.mygdx.packets.AuthKeyPacket;
import com.mygdx.packets.Packet;
import com.mygdx.packets.PacketBuilder;

public class ClientSendThread implements Runnable {
	private DatagramSocket socket;
	private ConcurrentLinkedQueue<Packet> packets;
	private InetAddress ip;
	private int port;
	private byte[] key;
	private Array<Packet> buffer;
	private int len;
	private AuthKeyPacket auth;
	public ClientSendThread(DatagramSocket socket, ConcurrentLinkedQueue<Packet> packets, InetAddress ip, int port, byte[] aeskey, String authkey) {
		this.socket = socket;
		this.packets = packets;
		this.ip = ip;
		this.port = port;
		this.key = aeskey;
		buffer = new Array<Packet>();
		auth = new AuthKeyPacket();
		auth.make(authkey);
	}
	
	@Override
	public void run() {
		resetState();
		
		while (!packets.isEmpty()) {
			Packet p = packets.poll();
			if (p == null) {
				Gdx.app.log("Client Send Thread", "can not send a null packet");
				consumeBuffer();
				return;
			}
			writePacket(p);
		}
		consumeBuffer();
			
	}
	
	private void writePacket(Packet packet) {
		buffer.add(packet);
		len += packet.length();
		if (len >= 1000) {
			consumeBuffer();
		}
	}
	
	private void consumeBuffer() {
		if (buffer.size > 0) {
			PacketBuilder reply = new PacketBuilder();
			reply.allocate(len+auth.length());
			// add auth key
			reply.putPacket(auth.getData());
			for (Packet p : buffer) {
				reply.putPacket(p.getData());
			}
			resetState();
			reply.encrypt(key);
			send(reply.getPacket());
		}
	}
	
	private void send(byte[] data) {
		DatagramPacket out = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void resetState() {
		len = 0;
		buffer.clear();
	}

}