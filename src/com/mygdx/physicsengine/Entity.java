package com.mygdx.physicsengine;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Entity {

	public Vector2 cord;
	protected Vector2 v;
	protected Vector2 a;
	protected Sprite sprite;
	protected int width = 16;
	protected int height = 16;

	public Entity() {
		cord = new Vector2();
		v = new Vector2(0, 0);
		a = new Vector2(0, -26);
	}

	public void setCord(Vector2 newPos) {
		cord.set(newPos);
	}

	public void setCord(float x, float y) {
		cord.set(x, y);
	}

	public void setTo(float x, float y) {
		setCord(x, y);
	}

	public void updateX(float dt) {
		cord.x += v.x*dt;
	}
	
	public void updateY(float dt) {
		v.y += a.y*dt;
		cord.y += v.y*dt;
	}
	
	public void draw(SpriteBatch sp) {
		sprite.setPosition(cord.x, cord.y);
		sprite.draw(sp);
	}

	public void collideX(Rectangle b, float dt) {
		sprite.setPosition(cord.x, cord.y);
		Rectangle rect = sprite.getBoundingRectangle();
		if (rect.overlaps(b)) { 
			if (v.x > 0f) {
				cord.x = b.x - width-0.1f;
				return;
			} else if (v.x < 0f) { 
				cord.x = b.x + b.width;
				return;
			} 
		}
		sprite.setPosition(cord.x, cord.y);
	}
	
	public void collideY(Rectangle b, float dt) {
		sprite.setPosition(cord.x, cord.y);
		Rectangle rect = sprite.getBoundingRectangle();
		if (rect.overlaps(b)) {
			if (v.y<0) { // above object
				rect.setY(b.y + b.height);
				v.y = 0;
				cord.y = rect.y;
			} else if (v.y>0) { // bellow object
				rect.setY(b.y - rect.height);
				v.y = 0;
				cord.y = rect.y;
			}
		}
	}
}
