package com.mygdx.consoleserver;
/**
 * methods can be called by javascript scripts
 * this exists to prevent bad stuff from happening
 */
public class JavascriptWrapper {
	private ServerConsole serverConsole;
	public static boolean serverRunning = false;
	public JavascriptWrapper(ServerConsole server) {
		this.serverConsole = server;
	}
	
	public void setToServer() {
		if (!serverRunning) {
			serverConsole.setToServer();
		}
	}
	
	public void setPort(int port) {
		if (!serverRunning) {
			serverConsole.setPort(port);
		}
	}
	
	public void setWorld(String n) {
		if (!serverRunning) {
			serverConsole.setWorld(n);
		}
	}
}
