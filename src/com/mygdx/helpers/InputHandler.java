package com.mygdx.helpers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntSet;
import com.mygdx.gameobjects.Player;
import com.mygdx.gameworld.GameWorld;

public class InputHandler implements InputProcessor {
	private GameWorld myWorld;
	private Player player;
	private final IntSet downKeys = new IntSet(20);

	public InputHandler(GameWorld myWorld) {
		player = myWorld.getPlayer();
		this.myWorld = myWorld;
	}

	@Override
	public boolean keyDown(int keycode) {
		downKeys.add(keycode);
		if (downKeys.size >= 1) {
			onMultipleKeysDown(keycode);
		} else if (downKeys.size == 0) {
			player.stop();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		downKeys.remove(keycode);
		return true;
	}

	private void onMultipleKeysDown(int mostRecentKeycode) {
		if (downKeys.contains(Keys.RIGHT)) {
			player.moveRight();
		}
		if (downKeys.contains(Keys.LEFT)) {
			player.moveLeft();
		}
		if (downKeys.contains(Keys.UP)) {
			player.jump();
		}
		if (downKeys.contains(Keys.DOWN)) {
			player.stop();
		}
		downKeys.clear();
	}

	@Override
	public boolean keyTyped(char c) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		myWorld.mouseAction(screenX, screenY, button);
		return true;
	}

	@Override
	public boolean touchUp(int i, int i1, int i2, int i3) {
		return false;
	}

	@Override
	public boolean touchDragged(int i, int i1, int i2) {
		return false;
	}

	@Override
	public boolean mouseMoved(int i, int i1) {
		return false;
	}

	@Override
	public boolean scrolled(int i) {
		return false;
	}

}
