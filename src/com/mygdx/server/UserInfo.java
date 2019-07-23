package com.mygdx.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
/**
 * this now supports multiple localhost clients for testing purposes
 */
public class UserInfo {
	private ConcurrentHashMap<InetAddress, UserData> map;
	
	// stores clients based of port instead of ip address
	private ConcurrentHashMap<Integer, UserData> localhost; 
	
	public UserInfo() {
		map = new ConcurrentHashMap<InetAddress, UserData>();
		localhost = new ConcurrentHashMap<Integer, UserData>();
	}
	
	public UserData getUser(InetAddress add, int port) {
		
		if (isLocalhost(add)) {
			return localhost.get(port);
		}
		return map.get(add);
	}
	
	public boolean containsUser(InetAddress add, int port) {
		if (isLocalhost(add)) {
			return localhost.contains(port);
		}
		return map.containsKey(add);
	}
	
	public UserInfo addUser(InetAddress add, UserData ud, int port) {
		if (isLocalhost(add)) {
			localhost.put(port, ud);
			return this;
		}
		map.put(add, ud);
		return this;
	}
	
	private boolean isLocalhost(InetAddress add) {
		try {
			if (add.equals(InetAddress.getLocalHost())) {
				return true;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		return false;
	}

}
