package com.mygdx.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.helpers.AssetLoader;

public class Console {
	private final Table output;
	private final Table layout;
	private ScrollPane scroll;
	private TextField command;

	public Console() {
		output = new Table();
		layout = new Table();
		command = null;
	}

	public void create(Table container) {
		output.center();
		for (int i = 0; i < 22; i++) {
			Label text1 = new Label("", AssetLoader.skin);
			output.add(text1).left().expandX().fill();
			output.row();
		}
		scroll = new ScrollPane(output, AssetLoader.skin);
		scroll.layout();
		scroll.scrollTo(0, 0, 0, 0);
		layout.add(scroll).left().expandX().fill().pad(10);
		layout.row();
		command = new TextField("", AssetLoader.skin);
		command.addCaptureListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER) {
					excecute(command.getText());
				}
				return false;
			}
		});
		layout.add(command).center().fill().pad(10);
		layout.row();
		layout.setDebug(false);
		layout.layout();
		container.add(layout).center().expand().fill();
	}

	public void excecute(String comm) {

	}

	public void hide() {
		layout.setVisible(false);
	}

	public void show() {
		layout.setVisible(true);
	}

	public String[] getComms() {
		return command.getText().split("\\s+");
	}

	public void log(String str) {
		print(">>> " + str);
	}

	public void print(String str) {
		Label text = new Label(str, AssetLoader.skin);
		Gdx.app.log("Console log", command.getText());
		Gdx.app.log("Console log result: "+str,"");
		command.setText("");
		output.add(text).left().expandX();
		output.row();
		scroll.layout();
		scroll.scrollTo(0, 0, 0, 0);
	}

	public void clearComm() {
		command.setText("");
	}

	public int getInt(String str) {
		return Integer.parseInt(str);
	}
}
