package dev.basvs.lib.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractScreen extends ChangeListener implements InputProcessor, GestureListener {

	protected AbstractGame game;
	protected Stage guiStage;
	protected Table guiTable;
	private boolean removeAfterFadeOut = false;
	private boolean loadingScreen = false;

	public boolean isRemoveAfterFadeOut() {
		return removeAfterFadeOut;
	}

	public void setRemoveAfterFadeOut(boolean removeAfterFadeOut) {
		this.removeAfterFadeOut = removeAfterFadeOut;
	}

	/**
	 * If true, do not use FPS limiter and time step cap mechanism for this state.
	 * Useful for loading states that do not require smooth animation and that have
	 * very large delays between renders.
	 * 
	 * @return
	 */
	public boolean isLoadingScreen() {
		return loadingScreen;
	}

	/**
	 * Do not use FPS limiter and time step cap mechanism for this state. Useful for
	 * loading states that do not require smooth animation and that have very large
	 * delays between renders.
	 * 
	 * @param loadingScreen
	 */
	public void setLoadingScreen(boolean loadingScreen) {
		this.loadingScreen = loadingScreen;
	}

	public AbstractScreen(AbstractGame game) {
		this.game = game;
		// Setup GUI base
		guiStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), game.guiCamera));
		guiTable = new Table();
		guiTable.setFillParent(true);
		guiStage.addActor(guiTable);
	}

	public abstract void onActivate();

	public abstract void onReactivate();

	public abstract void onDeactivate();

	public abstract void update(float delta) throws Exception;

	public abstract void render() throws Exception;

	public abstract void onResize(int width, int height) throws Exception;

	public abstract void onPause() throws Exception;

	public abstract void onDispose() throws Exception;

	// Some input handling helpers
	private Vector3 iv1 = new Vector3(), iv2 = new Vector3();

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// Ignore this and use the gestured detector event
		return false;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		game.camera.unproject(iv1.set(x, y, 0f));
		return handleTouchDown(iv1.x, iv1.y);
	}

	public abstract boolean handleTouchDown(float worldX, float worldY);

	@Override
	public boolean tap(float x, float y, int count, int button) {
		game.camera.unproject(iv1.set(x, y, 0f));
		return handleTap(iv1.x, iv1.y);
	}

	public abstract boolean handleTap(float worldX, float worldY);

	@Override
	public boolean longPress(float x, float y) {
		game.camera.unproject(iv1.set(x, y, 0f));
		return handleLongPress(iv1.x, iv1.y);
	}

	public abstract boolean handleLongPress(float worldX, float worldY);

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return handleFling(velocityX, velocityY);
	}

	public abstract boolean handleFling(float velocityX, float velocityY);

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		game.camera.unproject(iv1.set(x, y, 0f));
		game.camera.unproject(iv2.set(x, y, 0f).add(deltaX, deltaY, 0f));
		iv2.sub(iv1);
		return handlePan(iv1.x, iv1.y, iv2.x, iv2.y);
	}

	public abstract boolean handlePan(float worldX, float worldY, float deltaX, float deltaY);

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return handleZoom(initialDistance, distance);
	}

	public abstract boolean handleZoom(float initialDistance, float distance);

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// Ignore
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		game.camera.unproject(iv1.set(screenX, screenY, 0f));
		return handleTouchUp(iv1.x, iv1.y);
	}

	public abstract boolean handleTouchUp(float worldX, float worldY);

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		game.camera.unproject(iv1.set(screenX, screenY, 0f));
		return handleTouchDragged(iv1.x, iv1.y);
	}

	public abstract boolean handleTouchDragged(float worldX, float worldY);

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// Ignore
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return handleKeyDown(keycode);
	}

	public abstract boolean handleKeyDown(int keycode);

	@Override
	public boolean keyUp(int keycode) {
		return handleKeyUp(keycode);
	}

	public abstract boolean handleKeyUp(int keycode);

	@Override
	public boolean keyTyped(char character) {
		return handleKeyTyped(character);
	}

	public abstract boolean handleKeyTyped(char character);

	public abstract boolean handleScrolled(float amount);

	@Override
	public void changed(ChangeEvent event, Actor actor) {
		// Do not handle GUI events by default
	}
}
