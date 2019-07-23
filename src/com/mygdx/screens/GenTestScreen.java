package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.gen.GenChk;

public class GenTestScreen implements Screen {
	private final SpriteBatch batch;
	private GenChk gen;
	private Pixmap pixmap;
	private Texture img;

	public GenTestScreen() {
		super();
		batch = new SpriteBatch();

		gen = new GenChk(22);
		create();
	}

	public void create() {
		pixmap = new Pixmap(800, 600, Pixmap.Format.RGB888);
		pixmap.setBlending(Pixmap.Blending.None);
		int y = -2000;
		int x = 0;
		for (int j = 0; j < 600 / 16; j++) {
			for (int i = 0; i < 800 / 16; i++) {
				int cx = x + 16 * i;
				int cy = y + 16 * j;
				int[][] map = gen.gen(cx, cy);
				for (int x1 = 0; x1 < 16; x1++) {
					for (int y1 = 0; y1 < 16; y1++) {
						if (map[x1][y1] == 0) {
							pixmap.setColor(Color.BLUE);
						} else if (map[x1][y1] == 1) {
							pixmap.setColor(Color.BROWN);
						} else if (map[x1][y1] == 2) {
							pixmap.setColor(Color.GREEN);
						} else if (map[x1][y1] == 3) {
							pixmap.setColor(Color.LIGHT_GRAY);
						} else {
							Gdx.app.log("error", "");
						}
						// pixmap.drawPixel(cx+x1, 600-(cy+y1));
						pixmap.drawPixel(i * 16 + x1, (j * 16 + y1));
					}
				}
			}
		}
		img = new Texture(pixmap);
		Gdx.app.log("Gen", "done");
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
