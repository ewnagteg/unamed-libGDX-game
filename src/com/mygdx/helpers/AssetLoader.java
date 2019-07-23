package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.game.Settings;

public class AssetLoader {
	public static Texture texture;
	public static Texture grass;
	public static TextureRegion dirt;
	public static TextureRegion dirt_grass;
	public static Texture[] textureDict;
	public static TextureRegion[] textureRegionDict;
	public static String[] textures;
	public static Sprite[] spriteDict;
	public static Sprite pSprite;
	public static Texture playerTex;
	public static Skin skin;
	public static TextureAtlas atlas;
	
	public static Sprite[] entityDict;
	public static void load() {
		// define textures to load

		// also defines texture's number ie 1 = default_dry_grass ect
		textures = new String[] { 
				"data/dirt.png", // type 1
				"data/dirt_grass.png", // type 2
				"data/stone.png", // type 3
				"data/tree.png", // type 4
				"data/savannah_dirt.png", // type 5
				"data/savannah_grass.png", // type 6
				"data/ore_iron.png", // type 7
				"data/ore_purple.png", // type 8
				"data/ore_green.png", // type 9
				"data/green.png", // type 10
				"data/savannah_green.png", // type 11
				};
		
		textureDict = new Texture[textures.length];
		textureRegionDict = new TextureRegion[textures.length];
		spriteDict = new Sprite[textures.length];
		
		// define entitys that we want to load
		String[] ents = new String[] {
				"data/player2.png",
				"data/player3.png"
		};
		
		try {
			skin = new Skin(Gdx.files.internal("skin/skin/uiskin.json"));
			atlas = new TextureAtlas("skin/skin/uiskin.atlas");
			for (int i = 0; i < textures.length; i++) {
				textureDict[i] = new Texture(Gdx.files.internal(textures[i]));
				textureRegionDict[i] = new TextureRegion(textureDict[i]);
				spriteDict[i] = new Sprite(textureRegionDict[i]);
				spriteDict[i].setSize(Settings.BSIZE, Settings.BSIZE);
			}
			playerTex = new Texture(Gdx.files.internal("data/player.png"));
			pSprite = new Sprite(playerTex);
			
			// load entity textures
			entityDict = new Sprite[ents.length];
			for (int i=0; i<ents.length; i++) {
				entityDict[i] = new Sprite(new Texture(Gdx.files.internal(ents[i])));
			}
			
		} catch (GdxRuntimeException ex) {
			Gdx.app.log("AssetLoader: ", ex.getMessage());
		}
	}

	public static void dispose() {
		for (Texture tex : textureDict) {
			tex.dispose();
		}
		atlas.dispose();
		skin.dispose();
		playerTex.dispose();
	}
}
