package com.mygdx.gui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.gameworld.GameWorld;

public class GameInput extends InputListener {
	private GameWorld world;
	private Camera cam;
	public GameInput(GameWorld world, Camera cam) {
		this.world = world;
		this.cam = cam;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		Vector3 v = new Vector3();
		v.set(x,y,1);
		v.set(cam.unproject(v));
		System.out.println(""+v.x+"  "+v.y);
		world.mouseAction((int)v.x, (int)(v.y), button);
		return true;
	}
}
