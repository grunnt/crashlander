package dev.basvs.crashlander.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParticleGenerator {

	public boolean active;
	public Vector2 position, velocity;
	public float angle, angleSpread;
	public TextureRegion[] texture;
	public Color startColor, endColor;
	public float lifeMinimum, lifeMaximum;
	public float speedMinimum, speedMaximum;
	public float fireInterval, timeUntilFire;

}
