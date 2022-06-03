package dev.basvs.crashlander;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
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
import dev.basvs.crashlander.atmosphere.Atmosphere;
import dev.basvs.crashlander.controller.LanderController;
import dev.basvs.crashlander.gui.Meters;
import dev.basvs.crashlander.lander.Lander;
import dev.basvs.crashlander.lander.LanderBuilder;
import dev.basvs.crashlander.lander.design.LanderDesign;
import dev.basvs.crashlander.lander.design.LanderDesignPart;
import dev.basvs.crashlander.lander.part.Part;
import dev.basvs.crashlander.particle.ParticleManager;
import dev.basvs.crashlander.terrain.LanderWorld;
import dev.basvs.crashlander.terrain.MountainWorldGenerator;
import dev.basvs.lib.game.AbstractGame;
import dev.basvs.lib.game.AbstractScreen;
import dev.basvs.crashlander.controller.LanderController.Control;
import dev.basvs.crashlander.lander.LanderBuilder.Angle;

public class GameScreen extends AbstractScreen {

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

	// Graphics
	private Atmosphere background;
	private PolygonSpriteBatch polyBatch;

	// Box2D stuff
	private World world;

	// Game data
	private LanderController landerController;
	private Lander lander;

	private ParticleManager particleManager;

	private Meters meters;

	private TextureRegion gaugeBarTexture, gaugeCoverTexture;

	private LanderWorld landerWorld;

	LanderBuilder landerAssembly;

	private Array<Body> tempBodyList = new Array<Body>();

	private AltitudeRaycastCallback callback;

	public GameScreen(AbstractGame game) throws Exception {
		super(game);

		particleManager = new ParticleManager();

		background = new Atmosphere();

		polyBatch = new PolygonSpriteBatch();

		TextureAtlas partTextures = CrashLander.getInstance().getAssets().get("data/parts.atlas",
				TextureAtlas.class);
		gaugeBarTexture = partTextures.findRegion("gauge-small-bar");
		gaugeCoverTexture = partTextures.findRegion("gauge-small-cover");

		meters = new Meters();

		setupWorld();

		setupLander();

		landerController = new LanderController(lander);

		callback = new AltitudeRaycastCallback(landerWorld, lander);
	}

	private void setupWorld() {
		world = new World(new Vector2(0, -1), true);
		MountainWorldGenerator gen = new MountainWorldGenerator();
		landerWorld = gen.generate(world, LANDSCAPE_WIDTH);
	}

	private void setupLander() throws Exception {
		landerAssembly = new LanderBuilder(world, particleManager);

		float xPos = LANDER_START_X * WORLD_TO_BOX2D;
		float yPos = LANDER_START_Y * WORLD_TO_BOX2D;

		LanderDesign design = new LanderDesign();
		design.name = "Test lander";
		design.core = new LanderDesignPart();
		design.core.partName = "Cockpit";

		LanderDesignPart largeTank = design.core.attach("Large fuel tank", 1, 0, Angle.Down, false);
		LanderDesignPart largeThruster = largeTank.attach("Large engine", 2, 0, Angle.Down, true).control(Control.Up);

		LanderDesignPart leftLowerTank = largeTank.attach("Small fuel tank", 3, 1, Angle.Down, true);
		LanderDesignPart leftThruster = leftLowerTank.attach("Small engine", 2, 0, Angle.Down, true)
				.control(Control.Right);

		LanderDesignPart rightLowerTank = largeTank.attach("Small fuel tank", 1, 3, Angle.Down, true);
		LanderDesignPart rightThruster = rightLowerTank.attach("Small engine", 2, 0, Angle.Down, true)
				.control(Control.Left);

		lander = landerAssembly.buildFromDesign(design, xPos, yPos);
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

		// Move lander to other side of landscape boundaries if it crosses the edge
		// TODO: warnings & destruction if flying outside border
		float landerX = lander.core.body.getPosition().x * BOX2D_TO_WORLD;
		if (landerX > landerWorld.getRightX()) {
			lander.teleport(-(landerWorld.getRightX() - landerWorld.getLeftX()), 0f);
			landerX = lander.core.body.getPosition().x * BOX2D_TO_WORLD;
			System.out.println("new x: " + (int) landerX);
		} else if (landerX < landerWorld.getLeftX()) {
			lander.teleport((landerWorld.getRightX() - landerWorld.getLeftX()), 0f);
			landerX = lander.core.body.getPosition().x * BOX2D_TO_WORLD;
			System.out.println("new x: " + (int) landerX);
		}

		background.setAltitude(lander.core.body.getPosition().y * BOX2D_TO_WORLD);

		particleManager.update(delta);

		// Update physics
		world.step(1f / CrashLander.MAX_FPS, 6, 2);

		landerController.update(delta, world, game.camera);

		game.camera.position.set(lander.core.body.getPosition().x * BOX2D_TO_RENDER,
				lander.core.body.getPosition().y * BOX2D_TO_RENDER, 0);
		// game.camera.position.set(0, 250, 0);
		game.camera.update();
	}

	Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	Matrix4 worldCameraMatrix = new Matrix4();

	@Override
	public void render() throws Exception {

		worldCameraMatrix.set(game.camera.combined);
		worldCameraMatrix.scl(BOX2D_TO_RENDER);

		background.render(game.batch);

		polyBatch.setProjectionMatrix(game.camera.combined);
		polyBatch.begin();
		landerWorld.getPolySprite().draw(polyBatch);
		polyBatch.end();

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

		game.batch.begin();
		game.batch.setProjectionMatrix(game.guiCamera.combined);
		// Determine altitude of lander
		world.rayCast(callback, lander.core.body.getPosition(),
				new Vector2(lander.core.body.getPosition().x, LanderWorld.LANDSCAPE_POLYGON_BOTTOM_Y * WORLD_TO_BOX2D));
		meters.render(callback.getAltitude(), lander.core.body.getLinearVelocity().x * BOX2D_TO_WORLD,
				lander.core.body.getLinearVelocity().y * BOX2D_TO_WORLD, game.batch);
		game.batch.end();
		game.batch.setProjectionMatrix(game.camera.combined);

		// debugRenderer.render(world, worldCameraMatrix);
	}

	private void render(SpriteBatch batch, Body body, TextureRegion texture) {
		float x = body.getPosition().x * GameScreen.BOX2D_TO_RENDER;
		float y = body.getPosition().y * GameScreen.BOX2D_TO_RENDER;
		float angle = body.getAngle() * MathUtils.radiansToDegrees;
		float w = texture.getRegionWidth();
		float h = texture.getRegionHeight();
		batch.draw(texture, x - w / 2, y - h / 2, w / 2, h / 2, w, h, 1, 1, angle);
	}

	private void renderGauge(SpriteBatch batch, Body body, float filled) {
		float x = body.getPosition().x * GameScreen.BOX2D_TO_RENDER;
		float y = body.getPosition().y * GameScreen.BOX2D_TO_RENDER;
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
		if (keycode == Input.Keys.W) {
			landerController.controlSwitch(lander.core, Control.Up, true);
		}
		if (keycode == Input.Keys.A) {
			landerController.controlSwitch(lander.core, Control.Left, true);
		}
		if (keycode == Input.Keys.D) {
			landerController.controlSwitch(lander.core, Control.Right, true);
		}
		if (keycode == Input.Keys.S) {
			landerController.controlSwitch(lander.core, Control.Down, true);
		}
		if (keycode == Input.Keys.Q) {
			landerController.controlSwitch(lander.core, Control.StrafeLeft, true);
		}
		if (keycode == Input.Keys.E) {
			landerController.controlSwitch(lander.core, Control.StrafeRight, true);
		}
		return true;
	}

	@Override
	public boolean handleKeyUp(int keycode) {
		if (keycode == Input.Keys.W) {
			landerController.controlSwitch(lander.core, Control.Up, false);
		}
		if (keycode == Input.Keys.A) {
			landerController.controlSwitch(lander.core, Control.Left, false);
		}
		if (keycode == Input.Keys.D) {
			landerController.controlSwitch(lander.core, Control.Right, false);
		}
		if (keycode == Input.Keys.S) {
			landerController.controlSwitch(lander.core, Control.Down, false);
		}
		if (keycode == Input.Keys.Q) {
			landerController.controlSwitch(lander.core, Control.StrafeLeft, false);
		}
		if (keycode == Input.Keys.E) {
			landerController.controlSwitch(lander.core, Control.StrafeRight, false);
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
		return handleScrolled(amountY);
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
