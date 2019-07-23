package com.mygdx.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Settings;
import com.mygdx.gameworld.GameWorld;
import com.mygdx.helpers.AssetLoader;

public class gameUI {
	private final Stage stage;
	private FitViewport viewport;
	private final SpriteBatch sb;
	private OrthographicCamera cam;
	private Table invTable;
	private Table invUI;
	private Table container;
	private Table hudbar;
	private Table layout;
	private GameCommands console;
	private GameWorld world;
	public int[] inv;
	public boolean consoleVis = true;
	private PlayerInput pinput;
	private GameInput ginput;
	private Camera gameCamera;
	public gameUI(GameWorld world, Camera gameCamera) {
		this.world = world;
		this.sb = new SpriteBatch();
		
		// set up camera
		this.cam = new OrthographicCamera();
		cam.setToOrtho(false, Settings.sW, Settings.sH);
		cam.update();
		
		// set up viewport to fit screen
		viewport = new FitViewport(Settings.sW, Settings.sH, cam);
		viewport.apply();
		
		// create stage
		stage = new Stage(viewport, sb);
		Gdx.input.setInputProcessor(stage);
		this.gameCamera = gameCamera;
		create();
	}
	
	public void create() {
		// make console
		layout = new Table(); // lays out inventory selection ui thing and hud bars
		invUI = new Table();
		container = new Table(); // contains console
		layout.setFillParent(true);
		stage.addActor(layout);
		console = new GameCommands(world);
		console.create(container);
		stage.addActor(container);
		container.setVisible(true);
		container.setFillParent(true);
		container.addCaptureListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ALT_LEFT) {
					if (consoleVis) {
						hideConsole();
					} else {
						showConsole();
					}
				}
				return false;
			}
		});
		// make inventory table
		invTable = new Table();
		invTable.setFillParent(true);
		invTable.center();
		int i = 0;

		for (TextureRegion reg : AssetLoader.textureRegionDict) {
			ImageButton button = new ImageButton(new TextureRegionDrawable(reg), new TextureRegionDrawable(reg));
			invTable.add(button).uniform().pad(10);
			i++;
			if (i % 4 == 0) {
				invTable.row();
			}
		}

		// layout the invTable
		TextButton exit = new TextButton("X", AssetLoader.skin);
		exit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				invUI.setVisible(false);
			}
		});
		invUI.add(exit).colspan(100).right().size(25, 25);
		invUI.row();
		ScrollPane scroll = new ScrollPane(invTable);
		invUI.add(scroll).expand().center();
		invUI.row();
		stage.addCaptureListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.TAB) {
					if (invUI.isVisible()) {
						invUI.setVisible(false);
					} else {
						invUI.setVisible(true);
					}
				}
				return false;
			}
		});
		
		// set background of inv Ui
		Pixmap pixmap =new Pixmap(800, 600, Pixmap.Format.RGB888);
		pixmap.setColor(Color.BLACK);
		pixmap.drawRectangle(0, 0, 800, 600);
		invUI.setBackground(new SpriteDrawable(new Sprite(new TextureRegion(new Texture(pixmap))))); // wow
		
		// add inv ui to layout table
		layout.add(invUI).size(400);
		layout.row();
		
		
		// this handles player input
		pinput = new PlayerInput(world.getPlayer());
		stage.addActor(pinput);
		
		// this handles game input
		ginput = new GameInput(world, gameCamera);
		stage.addListener(ginput);
		
		// prototype hud bar
		hudbar = new Table();
		for (int j=0; j<8; j++) {
			Image img = new Image();
			hudbar.add(img).size(16,16).pad(2);
		}
		hudbar.row();
		layout.add(hudbar);
	}
	
	public void hideConsole() {
		consoleVis = false;
		pinput.act = true;
		console.hide();
		console.clearComm();
	}

	public void showConsole() {
		consoleVis = true;
		pinput.act = false;
		console.show();
		console.clearComm();
	}

	public void updateInv(int[] newInv) {
	}

	public void draw(float delta) {
		stage.act(delta);
		stage.draw();
	}

	public void dispose() {
		stage.dispose();
	}
	
	/**
	 * used to pass console to server, so that chat can be printed to it.
	 * passes it to GameRender => GameWorld => ClientServer
	 * ie plays hot potato with it
	 * @return
	 */
	public Console getConsole() {
		return this.console;
	}

	public void resize(int w, int h) {
		stage.getViewport().update(w, h, true);
	}
}
