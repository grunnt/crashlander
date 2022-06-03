package dev.basvs.crashlander;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetList {
	public static void addAssets(AssetManager assets) {
		addTextures(assets);
		addFonts(assets);
		addSounds(assets);
	}

	private static void addTextures(AssetManager assets) {
		TextureParameter mipMapParam = new TextureParameter();
		mipMapParam.minFilter = TextureFilter.MipMapLinearNearest;
		mipMapParam.magFilter = TextureFilter.MipMapLinearNearest;
		mipMapParam.wrapU = TextureWrap.Repeat;
		mipMapParam.wrapV = TextureWrap.Repeat;
		mipMapParam.genMipMaps = true;
		assets.load("data/parts.atlas", TextureAtlas.class);
		assets.load("data/back.atlas", TextureAtlas.class);
		assets.load("data/particles.atlas", TextureAtlas.class);
		assets.load("data/ground.png", Texture.class, mipMapParam);
	}

	private static void addFonts(AssetManager assets) {
		assets.load("data/fonts/iceland42.fnt", BitmapFont.class);
	}

	private static void addSounds(AssetManager assets) {
		assets.load("data/audio/boom.wav", Sound.class);
		assets.load("data/audio/rocket.wav", Sound.class);
	}
}
