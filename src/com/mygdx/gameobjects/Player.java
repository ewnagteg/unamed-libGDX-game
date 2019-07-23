package com.mygdx.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Settings;
import com.mygdx.helpers.AssetLoader;
import com.mygdx.physicsengine.Entity;

public class Player extends Entity {
	private float termVy = 2000;
	private float speed = 100f;
	private boolean isAlive;
	private boolean canFly;
	private float fallmult;
	private float lowJumpMult;
	public boolean upKeyDown;
	public Player() {
		super();
		isAlive = true;
		sprite = AssetLoader.pSprite;
		width = 16;
		height = 16;
		fallmult = 2.5f;
		lowJumpMult = 2f;
		canFly = Settings.FLY;
	}
	
	public void teleport(int x, int y) {
		cord.set(x, y);
	}
	public void setJumpMult(float m) {
		lowJumpMult = m;
	}
	
	public void setFallMult(float f) {
		fallmult = f;
	}
	
	public void setSpeed(float s) {
		speed = s;
	}
	
	public void setGrav(float g) {
		a.set(0f, g);
	}
	
	public void jump() {
		if (v.y == 0 || canFly) {
			v.set(v.x, speed);
		}
	}
	
	@Override
	public void updateY(float dt) {
		if (v.y > 0 && upKeyDown) {
			v.y += a.y * lowJumpMult * dt;
		} else {
			v.y += a.y * fallmult * dt;
		}
		v.y += a.y*dt;
		if(Math.abs(v.y) > termVy) {
			v.y = termVy;
			if(v.y < 0) {
				v.y = -termVy;
			}
		}
		cord.y += v.y*dt;
	}
	
	public void moveRight() {
		v.x = speed;
	}

	public void moveLeft() {
		v.x = -speed;
	}

	public void stop() {
		v.x = 0f;
	}

	public void die() {
		isAlive = false;
		v.y = 0;
	}

	public Vector2 getV() {
		return v;
	}
	
	public void setFly(boolean v) {
		canFly = v;
	}
}
