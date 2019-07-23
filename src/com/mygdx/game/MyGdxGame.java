package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mygdx.helpers.AssetLoader;
import com.mygdx.screens.MainMenuScreen;

public class MyGdxGame extends Game {
	
	@Override
	public void create() {
		Gdx.app.log("Game: ", "Loading Assets");
		AssetLoader.load();
		Gdx.app.log("Game: ", "Starting MainMenuScreen");
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		this.screen.dispose();
		AssetLoader.dispose();
		Gdx.app.exit();
	}

}
