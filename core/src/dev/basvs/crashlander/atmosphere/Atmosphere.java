package dev.basvs.crashlander.atmosphere;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Atmosphere {

	private List<AtmosphereLayer> atmosphereLayers = new ArrayList<AtmosphereLayer>();
	private float altitude;

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public Atmosphere() {
		AtmosphereLayer layer1 = new AtmosphereLayer();
		layer1.altitude = 2000;
		layer1.color = new Color(0.6f, 0.6f, 1f, 1f);
		atmosphereLayers.add(layer1);

		AtmosphereLayer layer2 = new AtmosphereLayer();
		layer2.altitude = 4000;
		layer2.color = new Color(0.85f, 0.6f, 1f, 1f);
		atmosphereLayers.add(layer2);

		AtmosphereLayer layer3 = new AtmosphereLayer();
		layer3.altitude = 6000;
		layer3.color = new Color(0f, 0f, 0f, 1f);
		atmosphereLayers.add(layer3);
	}

	public void render(SpriteBatch batch) {
		float R = 0, G = 0, B = 0;
		int layers = atmosphereLayers.size();
		for (int l = 0; l < layers; l++) {
			AtmosphereLayer layer = atmosphereLayers.get(l);
			if (altitude <= layer.altitude) {
				// We're in this layer
				R = layer.color.r;
				G = layer.color.g;
				B = layer.color.b;
				if (l > 0) {
					// Mix with the layer below
					AtmosphereLayer lowerLayer = atmosphereLayers.get(l - 1);
					float progress = (altitude - lowerLayer.altitude) / (layer.altitude - lowerLayer.altitude);
					R = R * progress + lowerLayer.color.r * (1f - progress);
					G = G * progress + lowerLayer.color.g * (1f - progress);
					B = B * progress + lowerLayer.color.b * (1f - progress);
				}
				break;
			} else if (l == layers - 1) {
				R = layer.color.r;
				G = layer.color.g;
				B = layer.color.b;
				break;
			}
		}
		Gdx.gl.glClearColor(R, G, B, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	}
}
