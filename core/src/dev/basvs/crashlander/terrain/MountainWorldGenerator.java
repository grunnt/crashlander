package dev.basvs.crashlander.terrain;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import dev.basvs.lib.math.SimplexNoiseGenerator;

public class MountainWorldGenerator implements ILanderWorldGenerator {

	private static float overallScale = 0.0025f;
	private static float[] octaveScale = { 2, 4, 8, 16 };
	private static float[] octaveAmplitude = { 8, 4, 2, 1 };

	private float heightDelta = 150;

	public float getHeightDelta() {
		return heightDelta;
	}

	public void setHeightDelta(float heightDelta) {
		this.heightDelta = heightDelta;
	}

	@Override
	public LanderWorld generate(World world, float width) {

		SimplexNoiseGenerator.genGrad(System.nanoTime());

		LanderWorld landerWorld = new LanderWorld(world);

		// Generate an interesting landscape shape using simplex noise in octaves
		// TODO: make connection of left & right points more smooth
		int numberOfVertices = (int) (width / 2.5f);
		float xStep = width / numberOfVertices;
		Array<Vector2> landscape = new Array<Vector2>(numberOfVertices);
		float x = 0;
		int octaves = octaveScale.length;
		float amplitudeSum = 0;
		for (int a = 0; a < octaves; a++) {
			amplitudeSum += octaveAmplitude[a];
		}
		for (int i = 0; i < numberOfVertices; i++) {
			float noiseSum = 0;
			for (int o = 0; o < octaves; o++) {
				noiseSum += (float) SimplexNoiseGenerator.noise(x * octaveScale[o] * overallScale, 0f)
						* (octaveAmplitude[o] / amplitudeSum);
			}
			landscape.add(new Vector2(x, (float) noiseSum * heightDelta));
			x += xStep;
		}

		landerWorld.setLandscape(landscape);
		return landerWorld;
	}
}
