package com.mygdx.gameworld;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.mygdx.game.Settings;
import com.mygdx.gameobjects.Player;
import com.mygdx.gui.gameUI;

public class GameRenderer implements Screen {
	private OrthographicCamera camera;
	private GameWorld myWorld;
	private SpriteBatch batcher;
	private Player player;
	private int[] playerInv;
	protected ScriptEngineManager mgr;
	protected ScriptEngine engine;
	protected Bindings bindings;
	protected Invocable inv;

	private gameUI ui;

	public GameRenderer(GameWorld world) {
		super();
		// renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
		// renderer = new OrthogonalTiledMapRenderer(map, 1 / BSIZE);
		camera = new OrthographicCamera();

		camera.setToOrtho(false, Settings.sW, Settings.sH);
		camera.update();
		myWorld = world;
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(camera.combined);
		
		// load nashorn
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("nashorn");
		bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		inv = (Invocable) engine;
		// create ui
		try {
			engine.eval("load(\"scripts/default/init.js\");");
			inv.invokeMethod(engine.get("gameui"), "test");
		} catch (ScriptException e) {
			Gdx.app.log("Javascript", "could not load init.js"+e.getMessage());
		} catch (NoSuchMethodException e) {
			Gdx.app.log("Javascript", "error"+e.getMessage());
		}
		
		ui = new gameUI(world, camera);
		world.passConsole(ui.getConsole()); // this is pretty ugly
		playerInv = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		ui.updateInv(playerInv);
		
		initGameObjects();
	}

	private void initGameObjects() {
		player = myWorld.getPlayer();
	}

	@Override
	public void render(float delta) {
		myWorld.update(delta);
		// clear the screen
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.position.x = player.cord.x; // follow player
		camera.position.y = player.cord.y;
		camera.update();

		batcher.setProjectionMatrix(camera.combined);
		batcher.enableBlending();

		Rectangle scissors = new Rectangle();
		Rectangle clipBounds = new Rectangle(camera.position.x - Settings.sW / 2, camera.position.y - Settings.sH / 2,
				Settings.sW, Settings.sW);

		ScissorStack.calculateScissors(camera, batcher.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);

		batcher.begin();
		myWorld.draw(batcher);
		Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());
		batcher.end();
		batcher.flush();
		ScissorStack.popScissors();

		// draw ui
		ui.draw(delta);

	}

	@Override
	public void show() {
		//
	}

	@Override
	public void resize(int w, int h) {
		camera.setToOrtho(false, w, h);
		camera.update();
		batcher.setProjectionMatrix(camera.combined);
		myWorld.resize(w, h);
		ui.resize(w, h);
	}

	@Override
	public void pause() {
		myWorld.pause();
	}

	@Override
	public void resume() {
		myWorld.resume();
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		batcher.dispose();
	}
}
