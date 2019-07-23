package com.mygdx.server;

public class UserData {
	public String uname;
	public String authkey;
	public byte[] sslkey; // this is a bad idea but i don't care
	public long sit; // sign in time
	public int port;
	public boolean loggedIn;
}
