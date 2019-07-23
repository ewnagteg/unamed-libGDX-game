package com.mygdx.server;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptThread extends Thread {
	
	// js engine
	private ScriptEngineManager mgr;
	private Bindings bindings;
	private ScriptEngine engine;
	public ScriptThread(ConcurrentLinkedQueue<String> chatQueue,
						EntityData entitys, UserInfo users, String main) {
		
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("nashorn");
		bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("users", users);
		bindings.put("entitys", entitys);
		bindings.put("chatQueue", chatQueue);
		
		try {
			engine.eval("load(\""+main+".js\");");
		} catch (ScriptException e) {
			System.out.println("failed to load main script: " + e.getMessage());
		}
	}
	
	@Override
	public void run() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// run script(s)
	}
}
