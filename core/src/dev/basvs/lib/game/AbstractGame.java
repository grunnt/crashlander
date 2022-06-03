package dev.basvs.lib.game;

import java.util.Stack;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractGame implements ApplicationListener {

	public static final long ONE_SECOND_NS = 1000000000;
	public static final int MAX_FPS = 60;
	public static final float FADE_DURATION_S = 0.15f;

	public enum ScreenState {
		FadeIn, Active, FadeOut
	};

	// Stack of current screens
	private Stack<AbstractScreen> screens = new Stack<AbstractScreen>();

	// This screen is activated after the topmost screen faded out
	private AbstractScreen newScreen = null;
	// State of the current screen: fading in, out, or active
	private ScreenState screenState = ScreenState.FadeIn;
	// Progress when fading in or out
	private float fadeProgress = 0f;

	// Timing stuff
	private int maxUpdates = 10;
	private long lastTime = System.nanoTime();
	private boolean paused = false;
	private boolean useYield = false;

	// Asset loading
	private AssetManager assets = new AssetManager();

	// Rendering stuff
	public SpriteBatch batch;
	public OrthographicCamera camera, guiCamera;
	private Texture overlayTexture;

	// Global gui style
	public Skin guiSkin;

	private String preferencesName;
	public Preferences prefs;

	public float effectsVolume, guiVolume, musicVolume;

	private static AbstractGame instance;

	public static AbstractGame getInstance() {
		return instance;
	}

	public AbstractGame(String preferencesName) {
		this.preferencesName = preferencesName;
		instance = this;
	}

	/**
	 * Get the asset manager.
	 * 
	 * @return
	 */
	public AssetManager getAssets() {
		return assets;
	}

	/**
	 * Activate a new screen on top of the current screen.
	 * 
	 * @param screen
	 */
	public void activateScreen(AbstractScreen screen) {
		if (screens.isEmpty()) {
			// Fade in a new screen
			screen.onActivate();
			screens.push(screen);
			screen = null;
			screenState = ScreenState.FadeIn;
			fadeProgress = 0f;
		} else {
			// Fade out the current screen
			deactivateScreen();
			newScreen = screen;
		}
	}

	/**
	 * Deactivate current screen without activating a new one.
	 */
	public void deactivateScreen() {
		if (!screens.isEmpty()) {
			screens.peek().onDeactivate();
			screenState = ScreenState.FadeOut;
			fadeProgress = 1f;
			newScreen = null;
			// Stop giving input to this screen
			Gdx.input.setInputProcessor(null);
		}
	}

	/**
	 * Remove a screen from the stack directly.
	 */
	public void removeScreen(AbstractScreen screen) {
		if (screens.contains(screen)) {
			screen.onDeactivate();
			screens.remove(screen);
			if (newScreen == screen) {
				newScreen = null;
			}
		}
	}

	@Override
	public void create() {
		try {

			// Gdx.app.setLogLevel(Application.LOG_DEBUG);

			// Setup sprite batch and camera
			batch = new SpriteBatch();
			camera = new OrthographicCamera();
			camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			guiCamera = new OrthographicCamera();
			guiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			// Setup a black overlay texture
			Pixmap overlayPixmap = new Pixmap(2, 2, Format.RGBA8888);
			overlayPixmap.setColor(Color.BLACK);
			overlayPixmap.fillRectangle(0, 0, 2, 2);
			overlayTexture = new Texture(overlayPixmap);

			try {
				prefs = Gdx.app.getPreferences(preferencesName);
			} catch (Exception e) {
				// Do nothing, use default values
				// TODO error report?
			}

			// Get the preferences, and set default values
			effectsVolume = prefs.getFloat("effectsVolume", 0.5f);
			guiVolume = prefs.getFloat("guiVolume", 0.5f);
			musicVolume = prefs.getFloat("musicVolume", 0.5f);

			// Do user initialization
			onCreate();
			// Setup timer
			lastTime = System.nanoTime();

		} catch (Exception e) {
			Gdx.app.error("AbstractGame", "Uncaught exception in main loop, shutting down...", e);
			Gdx.app.exit();
		}
	}

	public abstract void onCreate() throws Exception;

	@Override
	public void resize(int width, int height) {
		try {
			camera.setToOrtho(false, width, height);
			guiCamera.setToOrtho(false, width, height);
			for (AbstractScreen screen : screens) {
				screen.guiStage.setViewport(new FitViewport(width, height, guiCamera));
				screen.onResize(width, height);
			}
			onResize(width, height);
		} catch (Exception e) {
			Gdx.app.error("AbstractGame", "Uncaught exception in main loop, shutting down...", e);
			Gdx.app.exit();
		}
	}

	public abstract void onResize(int width, int height) throws Exception;

	@Override
	public void render() {
		try {
			long time = System.nanoTime();
			long timeDelta = time - lastTime;
			float timeDeltaSeconds = timeDelta / (float) ONE_SECOND_NS;
			lastTime = time;

			if (screens.isEmpty()) {

				// Clear the display
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

				// No screen to activate, so quit
				Gdx.app.exit();

			} else {

				if (screenState == ScreenState.FadeOut) {
					// Fade out the current screen
					fadeProgress -= timeDeltaSeconds / FADE_DURATION_S;
					if (fadeProgress <= 0f) {
						// Done fading out
						if (screens.peek().isRemoveAfterFadeOut()) {
							// Remove this screen so we do not return here
							screens.pop().onDeactivate();
						}
						if (newScreen != null) {
							// Fade in a new screen
							newScreen.onActivate();
							screens.push(newScreen);
							newScreen = null;
							screenState = ScreenState.FadeIn;
							fadeProgress = 0f;
						} else {
							// Activate topmost screen
							if (!screens.isEmpty()) {
								screens.peek().onReactivate();
								screenState = ScreenState.FadeIn;
								fadeProgress = 0f;
							} else {
								// No screen to activate, so quit
								Gdx.app.exit();
							}
						}
					}

				} else if (screenState == ScreenState.FadeIn) {
					// Fade in the current screen
					fadeProgress += timeDeltaSeconds / FADE_DURATION_S;
					if (fadeProgress >= 1f) {
						// Done fading in, current screen is active
						screenState = ScreenState.Active;
						// Give input focus to this screen
						AbstractScreen screen = screens.peek();
						GestureDetector gDec = new GestureDetector(screen);
						// Reduce interval for long press detection
						gDec.setLongPressSeconds(0.35f);
						Gdx.input.setInputProcessor(new InputMultiplexer(screen.guiStage, new InputMultiplexer(gDec,
								screen)));
					}

				}

				// Always update the topmost screen'
				if (!screens.isEmpty()) {
					if (screens.peek().isLoadingScreen()) {
						// Update the loading screen without using a fixed timestep
						screens.peek().update(timeDeltaSeconds);

					} else {
						// Update the game state in capped time steps (in case we're running too slow)
						int updateCount = 0;
						while (timeDelta > 0 && (maxUpdates <= 0 || updateCount < maxUpdates) && !paused) {
							// Update using a time step in seconds
							long updateTimeStep = Math.min(timeDelta, ONE_SECOND_NS / MAX_FPS);
							float updateTimeStepSeconds = updateTimeStep / (float) ONE_SECOND_NS;

							screens.peek().update(updateTimeStepSeconds);
							screens.peek().guiStage.act(Gdx.graphics.getDeltaTime());

							timeDelta -= updateTimeStep;
							updateCount++;
						}
					}
				}
			}

			camera.update();
			guiCamera.update();

			// Render the topmost screen
			if (!screens.isEmpty()) {
				batch.setProjectionMatrix(camera.combined);
				screens.peek().render();
				// Render the GUI on top of everything
				// TODO: guicam stil required?
				batch.setProjectionMatrix(guiCamera.combined);
				screens.peek().guiStage.draw();
				// Render fade in/out overlay if needed
				if (screenState == ScreenState.FadeIn || screenState == ScreenState.FadeOut) {
					batch.begin();
					batch.setColor(1f, 1f, 1f, 1f - fadeProgress);
					batch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					batch.setColor(Color.WHITE);
					batch.end();
				}
			}

			if (screens.isEmpty() || !screens.peek().isLoadingScreen()) {
				// Limit the maximum FPS if this is not a loading screen
				long sleepTime = Math.round((ONE_SECOND_NS / MAX_FPS) - (System.nanoTime() - lastTime));
				if (sleepTime <= 0)
					return;
				long prevTime = System.nanoTime();
				while (System.nanoTime() - prevTime <= sleepTime) {
					if (useYield)
						Thread.yield(); // More smooth, high CPU usage
					else
						Thread.sleep(1); // Less smooth, lower CPU usage
				}
			}

		} catch (Exception e) {
			Gdx.app.error("AbstractGame.render()", "Uncaught exception in main loop, shutting down...", e);
			Gdx.app.exit();
		}
	}

	@Override
	public void pause() {
		try {
			for (AbstractScreen screen : screens) {
				screen.onPause();
			}
			onPause();
		} catch (Exception e) {
			Gdx.app.error("AbstractGame", "Uncaught exception in main loop, shutting down...", e);
			Gdx.app.exit();
		}
	}

	public abstract void onPause() throws Exception;

	@Override
	public void resume() {
		try {
			// Setup a black overlay texture
			Pixmap overlayPixmap = new Pixmap(2, 2, Format.RGBA8888);
			overlayPixmap.setColor(Color.BLACK);
			overlayPixmap.fillRectangle(0, 0, 2, 2);
			overlayTexture = new Texture(overlayPixmap);
			onResume();
		} catch (Exception e) {
			Gdx.app.error("AbstractGame", "Uncaught exception in main loop, shutting down...", e);
			Gdx.app.exit();
		}
	}

	public abstract void onResume() throws Exception;

	@Override
	public void dispose() {
		try {
			for (AbstractScreen screen : screens) {
				screen.guiStage.dispose();
				screen.onDispose();
			}
			onDispose();
			// Dispose of all assets
			assets.dispose();
		} catch (Exception e) {
			Gdx.app.error("AbstractGame", "Uncaught exception in main loop, shutting down...", e);
			Gdx.app.exit();
		}
	}

	public abstract void onDispose() throws Exception;

	/**
	 * Reset camera position, angle and zoom to default values.
	 */
	public void resetCamera() {
		camera.direction.set(0, 0, -1);
		camera.zoom = 1f;
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		camera.update();
	}
}
