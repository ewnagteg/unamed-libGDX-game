package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.database.WorldData;
import com.mygdx.game.Settings;
import com.mygdx.gui.ServerCommands;

public class ServerScreen implements Screen {
	public int port;
	private final Stage stage;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private final WorldData db;
	private final Table container;
	private Viewport viewport;
	private ServerCommands console;
	
	/**
	 * not really used any more rpi server is used instead
	 * @param worldName
	 */
	public ServerScreen(String worldName) {
		super();
		Gdx.graphics.setTitle("Server");
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(Settings.sW, Settings.sH, camera);
		viewport.apply();

		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();
		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);

		this.db = new WorldData(worldName, "not rpi");

		container = new Table();
		container.setFillParent(true);
		stage.addActor(container);
		console = new ServerCommands();
		console.create(container);
		// stage.addActor(table);
		// create();
	}

	public void create() {
		db.connect();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float f) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(f);
		stage.draw();

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
		stage.dispose();
		batch.dispose();

	}

}
