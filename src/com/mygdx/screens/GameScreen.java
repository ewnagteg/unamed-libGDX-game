package com.mygdx.screens;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mygdx.gameworld.GameRenderer;
import com.mygdx.gameworld.GameWorld;

public class GameScreen implements Screen {
	private GameWorld world;
	private GameRenderer renderer;

	public GameScreen(String worldName, String serverType, String ip, String port, String username, String password) {
		InetAddress add = null;
		if (ip != "singleplayer") {
			try {
				add = InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				Gdx.app.log("GameScreen", "Failed to create InetAddress, invalid hostname: " + ip);
			}
		}
		if (port.contentEquals("")) {
			port = "8000";
		}
		world = new GameWorld(worldName, serverType, add, Integer.parseInt(port), username, password);
		renderer = new GameRenderer(world);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		world.update(delta);
		renderer.render(delta);
	}

	@Override
	public void resize(int i, int i1) {
		renderer.resize(i, i1);
	}

	@Override
	public void pause() {
		renderer.pause();
	}

	@Override
	public void resume() {
		renderer.resume();
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		world.dispose();
		renderer.dispose();
	}
}
