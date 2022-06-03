package dev.basvs.crashlander;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import dev.basvs.crashlander.lander.Lander;
import dev.basvs.crashlander.terrain.LanderWorld;

public class AltitudeRaycastCallback implements RayCastCallback {

	private LanderWorld landerWorld;
	private Lander lander;

	private float altitude = 0;

	public float getAltitude() {
		return altitude;
	}

	public AltitudeRaycastCallback(LanderWorld landerWorld, Lander lander) {
		super();
		this.landerWorld = landerWorld;
		this.lander = lander;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		if (fixture.getBody() == landerWorld.getLandscapeBody()) {
			altitude = (lander.core.body.getPosition().y - point.y) * GameScreen.BOX2D_TO_WORLD;
			return 0;
		} else {
			return -1f;
		}
	}
}
