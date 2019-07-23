package com.mygdx.screens;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.database.WorldData;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Settings;
import com.mygdx.helpers.AssetLoader;

public class MainMenuScreen implements Screen {
	private final Stage stage;
	private final Table table;
	private final MyGdxGame game;
	private final Viewport viewport;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private String selectedWorld;

	private SelectBox<Label> sb;
	private WorldData db;

	public MainMenuScreen(MyGdxGame game) {
		super();
		this.game = game;

		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(Settings.sW, Settings.sH, camera);
		viewport.apply();

		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();
		stage = new Stage(viewport, batch);

		Gdx.input.setInputProcessor(stage);
		table = new Table();
		table.setFillParent(true);
		table.setDebug(false); // This is optional, but enables debug lines for tables.

		stage.addActor(table);
		this.db = new WorldData("", "not rpi");
		create();
	}

	public void create() {
		/*
		 * this is a complete mess, but it works...
		 */

		Label createWorld = new Label("Create: ", AssetLoader.skin);
		final TextField worldName = new TextField("", AssetLoader.skin);
		Label seed = new Label("Seed: ", AssetLoader.skin);
		final TextField seedVal = new TextField("", AssetLoader.skin);

		TextButton makeWorld = new TextButton("Make", AssetLoader.skin);
		makeWorld.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent even, float x, float y) {
				try {
					String str = worldName.getText();
					if (!"".equals(str) && str != null) {
						createWorld(str, Integer.parseInt(seedVal.getText()));
					}
				} catch (NumberFormatException ex) {
					Gdx.app.log("Main menu: ", ex.getMessage());
				}
			}
		});

		// select
		Label selectLabel = new Label("Select: ", AssetLoader.skin);

		// refresh button
		TextButton refresh = new TextButton("Refresh", AssetLoader.skin);
		refresh.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent even, float x, float y) {
				refreshDropDown();
			}
		});

		// first row
		table.add(createWorld).pad(10);
		table.add(worldName);
		table.add(seed);
		table.add(seedVal);
		table.add(makeWorld).pad(10);
		table.row();

		// second row
		table.add(selectLabel);
		makeDropDown();
		table.add(refresh).pad(10);
		table.row();

		// play button
		TextButton playButton = new TextButton("Play: ", AssetLoader.skin);
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToGameScreen(selectedWorld);
			}
		});

		table.add(playButton);
		table.row();
		TextButton server = new TextButton("Server", AssetLoader.skin);
		server.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToServer(selectedWorld);
			}
		});
		table.add(server).colspan(10).center().pad(10);
		table.row();
		TextButton gentest = new TextButton("Gen Test", AssetLoader.skin);
		gentest.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToGenTest();
			}
		});
		table.add(gentest).colspan(10).center().pad(10);
		table.row();

		final Table connect = new Table();
		connect.setFillParent(true);
		connect.center();
		connect.setVisible(false);
		TextButton exit = new TextButton("X", AssetLoader.skin);
		exit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				table.setVisible(true);
				connect.setVisible(false);
			}
		});
		connect.add(exit).colspan(100).right().size(25, 25);
		connect.row();
		Label uname = new Label("user name", AssetLoader.skin);
		TextField username = new TextField("", AssetLoader.skin);
		Label pword = new Label("password", AssetLoader.skin);
		TextField password = new TextField("", AssetLoader.skin);
		Label ip = new Label("ip", AssetLoader.skin);
		TextField add = new TextField("", AssetLoader.skin);
		Label port = new Label("port", AssetLoader.skin);
		TextField p = new TextField("", AssetLoader.skin);
		TextButton start = new TextButton("Start", AssetLoader.skin);
		start.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToMultiplayer(add.getText(), p.getText(), username.getText(), password.getText());
			}
		});
		connect.add(uname).center().pad(10);
		connect.add(username).center().pad(10);
		connect.add(pword).center().pad(10);
		connect.add(password).center().pad(10);
		connect.row();
		connect.add(ip).center().pad(10);
		connect.add(add).center().pad(10);
		connect.add(port).center().pad(10);
		connect.add(p).center().pad(10);
		connect.row();
		connect.add(start).colspan(100).center().pad(10);
		connect.row();
		stage.addActor(connect);

		TextButton con = new TextButton("Connect", AssetLoader.skin);
		con.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				table.setVisible(false);
				connect.setVisible(true);
			}
		});
		table.add(con).colspan(10).center().pad(10);
		table.row();

	}

	private void setToMultiplayer(String ip, String port, String username, String password) {
		dispose();
		if (ip.contentEquals("")) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		if (port.contentEquals("")) {
			port = Settings.DEFAULT_PORT;
		}
		game.setScreen(new GameScreen(selectedWorld, "Multiplayer", ip, port, username, password));
	}

	private void setToGenTest() {
		dispose();
		game.setScreen(new GenTestScreen());
	}

	private void createWorld(String name, int seed) {
		db.setWorld(name);
		db.connect();
		db.cleanDB();
		db.conf();
		db.makeDB();
		db.addSeed(seed);
		db.close();
	}

	private void setToServer(String world) {
		dispose();
		game.setScreen(new ServerScreen(world));
	}

	private void setToGameScreen(String world) {
		dispose();
		game.setScreen(new GameScreen(world, "SinglePlayer", "singleplayer", "", "", ""));
	}

	@Override
	public void show() {
	}

	private String[] getListOfWorlds() {
		int top = 0;
		FileHandle dirHandle;
		dirHandle = Gdx.files.internal("worlds");
		String[] strList = new String[dirHandle.list().length];
		// This would not work on android because of how sqlite databases are handled.
		for (FileHandle entry : dirHandle.list()) {
			strList[top] = entry.name().replaceAll(".sql", "");
			top++;
		}
		return strList;
	}

	private void makeDropDown() {
		String[] worlds = getListOfWorlds();
		Label[] blob = new Label[worlds.length];
		for (int i = 0; i < worlds.length; i++) {
			blob[i] = new Label(worlds[i], AssetLoader.skin);
		}
		sb = new SelectBox<Label>(AssetLoader.skin);
		sb.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				selectedWorld = sb.getSelected().getText().toString();
			}
		});
		sb.setItems(blob);
		table.add(sb);
	}

	private void refreshDropDown() {
		String[] worlds = getListOfWorlds();
		Label[] blob = new Label[worlds.length];
		for (int i = 0; i < worlds.length; i++) {
			blob[i] = new Label(worlds[i], AssetLoader.skin);
		}
		sb.setItems(blob);
	}

	@Override
	public void render(float f) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(f);
		stage.draw();
	}

	@Override
	public void resize(int w, int h) {
		stage.getViewport().update(w, h, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();

	}
}
