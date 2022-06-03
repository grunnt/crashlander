package dev.basvs.crashlander.lander.part;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PartDesign {

	public String name;
	public Array<Vector2> shape;
	public String textureName;
	public float textureDeltaX, textureDeltaY;

	public boolean core;

	public float mass;

	public Tank tank;
	public boolean fuelTransmitter;

	public Thruster thruster;
	public Lights light;

	public Array<Vector2> attach;

	@Override
	public String toString() {
		return name;
	}
}
