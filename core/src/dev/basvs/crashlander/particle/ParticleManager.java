package dev.basvs.crashlander.particle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.basvs.crashlander.GameScreen;
import dev.basvs.lib.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {

	private List<ParticleGenerator> generators = new ArrayList<ParticleGenerator>();
	private List<Particle> particles = new ArrayList<Particle>();

	public ParticleManager() {
		for (int p = 0; p < 500; p++) {
			particles.add(new Particle());
		}
	}

	public void add(ParticleGenerator generator) {
		generators.add(generator);
	}

	public void remove(ParticleGenerator generator) {
		generators.remove(generator);
	}

	public void update(float delta) {
		// Update generators
		for (int g = 0; g < generators.size(); g++) {
			ParticleGenerator generator = generators.get(g);
			if (generator.active) {
				generator.timeUntilFire -= delta;
				if (generator.timeUntilFire <= 0f) {
					Particle particle = getParticle();
					particle.texture = generator.texture[MathUtils.randomInt(generator.texture.length)];
					particle.startColor.set(generator.startColor);
					particle.endColor.set(generator.endColor);
					particle.position.set(generator.position);
					particle.lifeTotal = generator.lifeMinimum + MathUtils.randomFloat()
							* (generator.lifeMaximum - generator.lifeMinimum);
					particle.lifeLeft = particle.lifeTotal;
					particle.velocity.set(0, generator.speedMinimum + MathUtils.randomFloat()
							* (generator.speedMaximum - generator.speedMinimum));
					particle.velocity.rotate((MathUtils.PI + generator.angle + (MathUtils.randomFloat() - 0.5f)
							* generator.angleSpread)
							* MathUtils.RADIANS_TO_DEGREES);
					particle.velocity.add(generator.velocity);
					generator.timeUntilFire = generator.fireInterval;
				}
			}
		}

		// Update particles
		int pCount = particles.size();
		for (int p = 0; p < pCount; p++) {
			Particle prt = particles.get(p);
			if (prt.lifeLeft > 0f) {
				prt.lifeLeft -= delta;
				prt.position.add(prt.velocity.x * delta, prt.velocity.y * delta);
				//prt.velocity.scl(Math.max(0, 1f - 10f * delta));
			}
		}
	}

	private Particle getParticle() {
		Particle particle = null;
		int pCount = particles.size();
		for (int p = 0; p < pCount; p++) {
			Particle prt = particles.get(p);
			if (prt.lifeLeft <= 0) {
				particle = prt;
				break;
			}
		}
		if (particle == null) {
			particle = new Particle();
			particles.add(particle);
		}
		return particle;
	}

	public void render(SpriteBatch batch) {
		int pCount = particles.size();
		for (int p = 0; p < pCount; p++) {
			Particle prt = particles.get(p);
			if (prt.lifeLeft > 0f) {
				float remaining = prt.lifeLeft / prt.lifeTotal;
				batch.setColor(prt.startColor.r * remaining + prt.endColor.r * (1f - remaining), prt.startColor.g
						* remaining + prt.endColor.g * (1f - remaining), prt.startColor.b * remaining + prt.endColor.b
						* (1f - remaining), prt.startColor.a * remaining + prt.endColor.a * (1f - remaining));
				batch.draw(prt.texture, prt.position.x * GameScreen.BOX2D_TO_RENDER - prt.texture.getRegionWidth() / 2,
						prt.position.y * GameScreen.BOX2D_TO_RENDER - prt.texture.getRegionHeight() / 2);
			}
		}
	}
}
