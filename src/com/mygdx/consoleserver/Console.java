package com.mygdx.consoleserver;

import java.io.File;
import java.io.FileReader;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Console {
	protected Scanner in;
	protected ScriptEngineManager mgr;
	protected ScriptEngine engine;
	protected Bindings bindings;
	protected String scriptRoot = "./assets/scripts/server/";
	public Console() {
		in = new Scanner(System.in);
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("nashorn");
		bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
	}

	public void run() {
		while (true) {
			System.out.print(">>> ");
			runCommand();
		}
	}

	public void runCommand() {
		String script = in.next();
		String[] args = in.nextLine().split("\\s+");
		bindings.put("args", args);
		runScript(script);
		bindings.remove("args");
	}
	
	public void runScript(String script) {
		try {
			engine.eval(new FileReader(new File(scriptRoot+script+".js")));
		} catch (Exception e) {
			System.out.println("Could not find script: " + script);
		}
	}
	
	public String getStr() {
		try {
			String r = in.next();
			return r;
		} catch (InputMismatchException e) {
			System.out.println("Invalid command");
			System.out.println(e.getMessage());
			return "";
		}
	}

	public int getInt() {
		try {
			int r = in.nextInt();
			return r;
		} catch (InputMismatchException e) {
			System.out.println("Invalid command");
			System.out.println(e.getMessage());
			return 0;
		}
	}
}
