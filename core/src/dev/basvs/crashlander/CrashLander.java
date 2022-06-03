package dev.basvs.crashlander;

import dev.basvs.lib.game.AbstractGame;

public class CrashLander extends AbstractGame {

	public CrashLander() {
		super("frontier lander preferences");
	}

	@Override
	public void onCreate() throws Exception {

		AssetList.addAssets(getAssets());

		getAssets().finishLoading();

		//DesignScreen screen = new DesignScreen(this);
		GameScreen screen = new GameScreen(this);
		activateScreen(screen);
	}

	@Override
	public void onResize(int width, int height) throws Exception {
		// Do nothing
	}

	@Override
	public void onPause() throws Exception {
		// Do nothing
	}

	@Override
	public void onResume() throws Exception {
		// Do nothing
	}

	@Override
	public void onDispose() throws Exception {
		// Do nothing
	}
}
