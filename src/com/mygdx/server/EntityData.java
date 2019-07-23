package com.mygdx.server;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

public class EntityData {
	private static short uid = 0;
	private ConcurrentHashMap<Short, float[]> data;
	private HashMap<String, Short> userIds;
	public EntityData() {
		data = new ConcurrentHashMap<Short, float[]>();
		userIds = new HashMap<String, Short>();
	}
	
	/**
	 * 
	 * @param id
	 * @param data
	 */
	public void addEntity(String name, float[] data) {
		short id = genUid();
		this.data.put(id, data);
		userIds.put(name, id);
	}
	
	public float[] getData(short id) {
		return data.get(id);
	}
	
	public short getId(String name) {
		return userIds.get(name);
	}
	
	public int getSize() {
		return (int) data.mappingCount();
	}
	
	public KeySetView<Short, float[]> getKeySet() {
		return data.keySet();
	}
	
	private short genUid() {
		uid++;
		return uid;
	}
}
