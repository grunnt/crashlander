package dev.basvs.crashlander.terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import dev.basvs.crashlander.CrashLander;
import dev.basvs.crashlander.GameScreen;
import dev.basvs.crashlander.lander.LanderBuilder;

public class LanderWorld {

	public static final float LANDSCAPE_POLYGON_BOTTOM_Y = -1000;

	private Array<Vector2> landscape;
	private Body landscapeBody;
	private PolygonSprite landscapePolySprite;
	private float leftX, rightX;
	private World world;
	private Texture groundTexture;

	public Array<Vector2> getLandscape() {
		return landscape;
	}

	public Body getLandscapeBody() {
		return landscapeBody;
	}

	public PolygonSprite getPolySprite() {
		return landscapePolySprite;
	}

	public float getLeftX() {
		return leftX;
	}

	public float getRightX() {
		return rightX;
	}

	public LanderWorld(World world) {
		this.world = world;

		groundTexture = CrashLander.getInstance().getAssets().get("data/ground.png", Texture.class);
	}

	public void setLandscape(Array<Vector2> landscape) {
		this.landscape = landscape;
		leftX = landscape.first().x;
		rightX = landscape.peek().x;

		// Create arrays for Box2D body and sprite polygons
		Vector2[] chunkBodyVertices = new Vector2[landscape.size];
		float[] chunkSpriteVertices = new float[landscape.size * 2 + 4];

		// Fill arrays based on world shape
		for (int i = 0; i < landscape.size; i++) {
			Vector2 v = landscape.get(i);
			chunkSpriteVertices[i * 2] = v.x * GameScreen.WORLD_TO_RENDER;
			chunkSpriteVertices[i * 2 + 1] = v.y * GameScreen.WORLD_TO_RENDER;
			chunkBodyVertices[i] = new Vector2(v.x * GameScreen.WORLD_TO_BOX2D, v.y * GameScreen.WORLD_TO_BOX2D);
		}

		// Ground body is located at altitude = 0f
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0f, 0f);
		landscapeBody = world.createBody(bodyDef);

		// Create chain shape representing this part of the landscape
		ChainShape chainShape = new ChainShape();
		chainShape.createChain(chunkBodyVertices);
		landscapeBody.createFixture(chainShape, 0);
		chainShape.dispose();
		landscapeBody.getFixtureList().get(0).getFilterData().categoryBits = LanderBuilder.COLLISION_CATEGORY_WORLD;
		landscapeBody.getFixtureList().get(0).getFilterData().maskBits = LanderBuilder.COLLISION_CATEGORY_FRAME
				| LanderBuilder.COLLISION_CATEGORY_PARTS;

		// Create rectangular bottom part of sprite polygon
		chunkSpriteVertices[chunkSpriteVertices.length - 4] = chunkSpriteVertices[chunkSpriteVertices.length - 6];
		chunkSpriteVertices[chunkSpriteVertices.length - 3] = LANDSCAPE_POLYGON_BOTTOM_Y * GameScreen.WORLD_TO_RENDER;
		chunkSpriteVertices[chunkSpriteVertices.length - 2] = chunkSpriteVertices[0];
		chunkSpriteVertices[chunkSpriteVertices.length - 1] = LANDSCAPE_POLYGON_BOTTOM_Y * GameScreen.WORLD_TO_RENDER;

		ShortArray triangleIndices = new EarClippingTriangulator().computeTriangles(chunkSpriteVertices);
		PolygonRegion polyReg = new PolygonRegion(new TextureRegion(groundTexture), chunkSpriteVertices, triangleIndices.toArray());
		landscapePolySprite = new PolygonSprite(polyReg);
	}
}