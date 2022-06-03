package dev.basvs.crashlander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import dev.basvs.crashlander.lander.Lander;
import dev.basvs.crashlander.lander.LanderBuilder;
import dev.basvs.crashlander.lander.part.Part;
import dev.basvs.crashlander.particle.ParticleManager;
import dev.basvs.lib.game.AbstractGame;
import dev.basvs.lib.game.AbstractScreen;

public class DesignScreen extends AbstractScreen {

	public static final float RENDER_TO_BOX2D = 0.0078125f;
	public static final float BOX2D_TO_RENDER = 128f;
	public static final float WORLD_TO_BOX2D = 0.3125f;
	public static final float BOX2D_TO_WORLD = 3.2f;
	public static final float RENDER_TO_WORLD = 0.025f;
	public static final float WORLD_TO_RENDER = 40f;

	public static final Color GAUGE_COLOR_BACK = new Color(0f, 0.5f, 0f, 1f);
	public static final Color GAUGE_COLOR_FRONT = new Color(0f, 1f, 0f, 1f);

	public static final float LANDSCAPE_WIDTH = 2000;
	public static final float LANDER_START_X = LANDSCAPE_WIDTH / 2;
	public static final float LANDER_START_Y = 200;

	// Box2D stuff
	private World world;

	// Game data
	private LanderBuilder landerAssembly;
	private Lander lander;

	private ParticleManager particleManager;

	private TextureRegion gaugeBarTexture, gaugeCoverTexture;

	private Array<Body> tempBodyList = new Array<Body>();

	public DesignScreen(AbstractGame game) throws Exception {
		super(game);

		TextureAtlas partTextures = CrashLander.getInstance().getAssets().get("data/parts.atlas",
				TextureAtlas.class);
		gaugeBarTexture = partTextures.findRegion("gauge-small-bar");
		gaugeCoverTexture = partTextures.findRegion("gauge-small-cover");

		particleManager = new ParticleManager();

		world = new World(new Vector2(0, 0), true);

		setupLander();
	}

	private void setupLander() throws Exception {
		landerAssembly = new LanderBuilder(world, particleManager);

		lander = new Lander();

		float xPos = LANDER_START_X * WORLD_TO_BOX2D;
		float yPos = LANDER_START_Y * WORLD_TO_BOX2D;

		/*Part cockpit = landerAssembly.buildPart("Cockpit", new Vector2(xPos, yPos));
		lander.core = cockpit;

		Part largeTank = landerAssembly.buildPart("Large fuel tank");
		landerAssembly.attach(cockpit, 1, largeTank, 0, Angle.Down);

		Part largeThruster = landerAssembly.buildPart("Large engine");
		largeThruster.control = Control.Up;
		largeThruster.attachedTanks.add(largeTank);
		landerAssembly.attach(largeTank, 2, largeThruster, 0, Angle.Down);

		Part leftTank = landerAssembly.buildPart("Small fuel tank");
		landerAssembly.attach(largeTank, 3, leftTank, 1, Angle.Down);

		Part leftTank2 = landerAssembly.buildPart("Small fuel tank");
		landerAssembly.attach(leftTank, 0, leftTank2, 2, Angle.Down);

		Part leftThruster = landerAssembly.buildPart("Small engine");
		leftThruster.control = Control.Right;
		leftThruster.attachedTanks.add(leftTank);
		leftThruster.attachedTanks.add(leftTank2);
		landerAssembly.attach(leftTank, 2, leftThruster, 0, Angle.Down);

		Part rightTank = landerAssembly.buildPart("Small fuel tank");
		landerAssembly.attach(largeTank, 1, rightTank, 3, Angle.Down);

		Part rightTank2 = landerAssembly.buildPart("Small fuel tank");
		landerAssembly.attach(rightTank, 0, rightTank2, 2, Angle.Down);

		Part rightThruster = landerAssembly.buildPart("Small engine");
		rightThruster.control = Control.Left;
		rightThruster.attachedTanks.add(rightTank);
		rightThruster.attachedTanks.add(rightTank2);
		landerAssembly.attach(rightTank, 2, rightThruster, 0, Angle.Down);*/
	}

	@Override
	public void onActivate() {
		// Do nothing
	}

	@Override
	public void onReactivate() {
		// Do nothing
	}

	@Override
	public void onDeactivate() {
		// Do nothing
	}

	@Override
	public void update(float delta) throws Exception {

		particleManager.update(delta);

		// Update physics
		world.step(1f / CrashLander.MAX_FPS, 6, 2);

		game.camera.position.set(lander.core.body.getPosition().x * BOX2D_TO_RENDER,
				lander.core.body.getPosition().y * BOX2D_TO_RENDER, 0);

		game.camera.update();
	}

	Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	Matrix4 worldCameraMatrix = new Matrix4();

	@Override
	public void render() throws Exception {

		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldCameraMatrix.set(game.camera.combined);
		worldCameraMatrix.scl(BOX2D_TO_RENDER);

		// Render world
		game.batch.setProjectionMatrix(game.camera.combined);
		game.batch.begin();

		// Render lander parts
		world.getBodies(tempBodyList);

		for (int b = 0; b < tempBodyList.size; b++) {
			Body body = tempBodyList.get(b);
			if (body.getUserData() != null) {
				if (body.getUserData() instanceof Part) {
					Part part = (Part) body.getUserData();
					render(game.batch, part.body, part.texture);
					if (part.design.tank != null) {
						renderGauge(game.batch, body, part.fuel / part.design.tank.fuel);
					}
				}
			}
		}

		particleManager.render(game.batch);
		game.batch.setColor(Color.WHITE);

		game.batch.end();

		// debugRenderer.render(world, worldCameraMatrix);
	}

	private void render(SpriteBatch batch, Body body, TextureRegion texture) {
		float x = body.getPosition().x * DesignScreen.BOX2D_TO_RENDER;
		float y = body.getPosition().y * DesignScreen.BOX2D_TO_RENDER;
		float angle = body.getAngle() * MathUtils.radiansToDegrees;
		float w = texture.getRegionWidth();
		float h = texture.getRegionHeight();
		batch.draw(texture, x - w / 2, y - h / 2, w / 2, h / 2, w, h, 1, 1, angle);
	}

	private void renderGauge(SpriteBatch batch, Body body, float filled) {
		float x = body.getPosition().x * DesignScreen.BOX2D_TO_RENDER;
		float y = body.getPosition().y * DesignScreen.BOX2D_TO_RENDER;
		float angle = body.getAngle() * MathUtils.radiansToDegrees;
		game.batch.setColor(GAUGE_COLOR_BACK);
		float w = gaugeBarTexture.getRegionWidth();
		float h = gaugeBarTexture.getRegionHeight();
		batch.draw(gaugeBarTexture, x - w / 2, y - h / 2, w / 2, h / 2, w, h, 1, 1, angle);
		game.batch.setColor(GAUGE_COLOR_FRONT);
		batch.draw(gaugeBarTexture, x - w / 2, y - h / 2, w / 2, h / 2, w, h * filled, 1, 1, angle);
		game.batch.setColor(Color.WHITE);
		w = gaugeCoverTexture.getRegionWidth();
		h = gaugeCoverTexture.getRegionHeight();
		batch.draw(gaugeCoverTexture, x - w / 2, y - h / 2, w / 2, h / 2, w, h, 1, 1, angle);

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
	public void onDispose() throws Exception {
		// Do nothing
	}

	@Override
	public boolean handleTouchDown(float worldX, float worldY) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handleTap(float worldX, float worldY) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handleLongPress(float worldX, float worldY) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handleFling(float velocityX, float velocityY) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handlePan(float worldX, float worldY, float deltaX, float deltaY) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handleZoom(float initialDistance, float distance) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handleTouchUp(float worldX, float worldY) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handleTouchDragged(float worldX, float worldY) {
		// Do nothing
		return false;
	}

	@Override
	public boolean handleKeyDown(int keycode) {
		return true;
	}

	@Override
	public boolean handleKeyUp(int keycode) {
		if (keycode == Input.Keys.ENTER) {
			try {
				//GameScreen gameScreen = new GameScreen(this.game, lander);
				//game.activateScreen(gameScreen);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean handleKeyTyped(char character) {
		// Do nothing
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return handleScrolled(amountX);
	}

	@Override
	public boolean handleScrolled(float amount) {
		if (amount < 0.0) {
			game.camera.zoom *= 0.8f;
			if (game.camera.zoom < 1f)
				game.camera.zoom = 1f;
		} else {
			game.camera.zoom *= 1.2f;
			if (game.camera.zoom > 100f)
				game.camera.zoom = 100f;
		}
		return true;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public void pinchStop() {

	}
}
