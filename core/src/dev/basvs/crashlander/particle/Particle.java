package dev.basvs.crashlander.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Particle {
	public TextureRegion texture;
	public Vector2 position = new Vector2();
	public Vector2 velocity = new Vector2();
	public float lifeTotal, lifeLeft;
	public Color startColor = new Color(1f, 1f, 1f, 1f);
	public Color endColor = new Color(0.2f, 0.2f, 0.2f, 0.1f);
}
