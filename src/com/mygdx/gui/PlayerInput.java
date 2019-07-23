package com.mygdx.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.gameobjects.Player;

public class PlayerInput extends Actor {
	private Player player;
	public boolean act = true;

	public PlayerInput(Player player) {
		super();
		this.player = player;
	}

	@Override
	public void act(float delta) {
		if (act) {
			if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.UP)) {
				player.upKeyDown = true;
				player.jump();
			} else {
				player.upKeyDown = false;
			}
			if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.LEFT)) {
				player.stop();
			} else if (player.getV().x > 0) {
				player.stop();
			}
			if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
				player.moveLeft();
			} else if (player.getV().x < 0) {
				player.stop();
			}
			if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.DOWN)) {
				player.moveRight();
			}
			if (Gdx.input.isKeyPressed(Keys.NUM_1)) {
				// player.select(1);
			}
			// ect...
		}
	}
}
