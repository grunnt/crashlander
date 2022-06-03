package dev.basvs.crashlander.lander.part;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import dev.basvs.crashlander.particle.ParticleGenerator;
import dev.basvs.crashlander.controller.LanderController.Control;

public class Part {

	public TextureRegion texture;
	public Body body;
	public float textureDeltaX, textureDeltaY;

	public boolean active;
	public float fuel;

	public PartDesign design;

	public Control control;
	public Array<Part> fuelSources = new Array<>();

	public ParticleGenerator particleGen;
}
