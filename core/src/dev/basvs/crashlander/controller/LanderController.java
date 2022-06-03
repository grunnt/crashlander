package dev.basvs.crashlander.controller;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import dev.basvs.crashlander.CrashLander;
import dev.basvs.crashlander.GameScreen;
import dev.basvs.crashlander.lander.Lander;
import dev.basvs.crashlander.lander.part.Part;
import dev.basvs.crashlander.particle.ParticleGenerator;

public class LanderController {

	public static final float ZERO_GRAVITY_ALTITUDE = 100000f;
	public final static float JOINT_BREAK_FORCE = 0.003f;

	private Sound thrusterSound, explosionSound;

	public enum Control {
		Up, Right, Down, Left, StrafeLeft, StrafeRight
	};

	private Array<Body> bodies = new Array<Body>();

	private Lander lander;

	public LanderController(Lander lander) {
		this.lander = lander;

		AssetManager assets = CrashLander.getInstance().getAssets();
		thrusterSound = assets.get("data/audio/rocket.wav");
		explosionSound = assets.get("data/audio/boom.wav");
	}

	public void update(float delta, World world, Camera camera) {

		float gravityScale = Math.max(0,
				1f - ((lander.core.body.getPosition().y * GameScreen.BOX2D_TO_WORLD) / ZERO_GRAVITY_ALTITUDE));

		world.getBodies(bodies);
		for (int b = 0; b < bodies.size; b++) {
			Object obj = bodies.get(b).getUserData();
			if (obj instanceof Part) {
				Part part = (Part) obj;
				handleJointBreaking(part, delta, world);
				// TODO: also detach fuel tanks
				updatePart(delta, part, gravityScale, camera);
			}
		}
	}

	/**
	 * Activate or deactivate parts attached to the core frame and linked to a
	 * control.
	 * 
	 * @param control
	 * @param on
	 */
	public void controlSwitch(Part part, Control control, boolean on) {
		// Control this part
		if (part.control == control) {
			part.active = on;
			if (part.design.thruster != null) {
				if (on) {
					thrusterSound.loop();
				} else {
					thrusterSound.stop();
				}
			}
		}

		// Control any attached parts
		int nAttach = part.body.getJointList().size;
		for (int a = 0; a < nAttach; a++) {
			Part other = (Part) part.body.getJointList().get(a).joint.getBodyB().getUserData();
			if (other != part) {
				controlSwitch(other, control, on);
			}
		}
	}

	/**
	 * Break joints attached to this body if the force that is applied is too
	 * strong.
	 * 
	 * @param delta
	 * @param world
	 */
	private void handleJointBreaking(Part part, float delta, World world) {
		int joints = part.body.getJointList().size;
		for (int j = 0; j < joints; j++) {
			Joint joint = part.body.getJointList().get(j).joint;
			Part otherPart = (Part) joint.getBodyB().getUserData();
			if (otherPart != part) {
				if (joint.getReactionForce(-delta).len() > JOINT_BREAK_FORCE) {
					world.destroyJoint(joint);
					explosionSound.play();
					joints--;
				}
			}
		}
	}

	/**
	 * Update the part, such as adjusting gravity, applying thrust and activating
	 * lights.
	 * 
	 * @param part
	 * @param gravityScale
	 */
	private Vector2 thrustTempVector = new Vector2(0, 0);

	private void updatePart(float delta, Part part, float gravityScale, Camera camera) {
		part.body.setGravityScale(gravityScale);

		if (part.active && part.design.thruster != null) {
			float fuelConsumption = part.design.thruster.fuelPerSecond * delta;
			boolean thrust = false;
			while (fuelConsumption > 0) {
				Part tank = findFuel(part);
				if (tank != null) {
					if (tank.fuel >= fuelConsumption) {
						tank.fuel -= fuelConsumption;
						fuelConsumption = 0;
					} else {
						fuelConsumption -= tank.fuel;
						tank.fuel = 0;
					}
					thrust = true;
				} else {
					fuelConsumption = 0;
					part.active = false;
					thrusterSound.stop();
				}
			}
			if (thrust) {
				thrustTempVector.set(0, part.design.thruster.powerPerFuel * part.design.thruster.fuelPerSecond);
				part.body.applyForceToCenter(part.body.getWorldVector(thrustTempVector), true);
			}
		}

		if (part.particleGen != null) {
			ParticleGenerator particleGen = part.particleGen;
			particleGen.active = part.active;
			if (part.active) {
				particleGen.position.set(part.body.getPosition());
				thrustTempVector.set(part.design.thruster.position).scl(GameScreen.WORLD_TO_BOX2D);
				particleGen.position.add(part.body.getWorldVector(thrustTempVector));
				particleGen.angle = part.body.getAngle();
				particleGen.velocity.set(part.body.getLinearVelocity());
			}
		}
	}

	/**
	 * Get a fuel tank with fuel remaining.
	 * 
	 * @param part
	 * @return
	 */
	private Part findFuel(Part part) {
		for (int a = 0; a < part.fuelSources.size; a++) {
			Part other = part.fuelSources.get(a);
			if (other.design.tank != null && other.fuel > 0) {
				return other;
			}
			other = findFuel(other);
			if (other != null) {
				return other;
			}
		}
		return null;
	}
}
