package dev.basvs.crashlander.lander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Json;
import dev.basvs.crashlander.CrashLander;
import dev.basvs.crashlander.GameScreen;
import dev.basvs.crashlander.lander.design.LanderDesign;
import dev.basvs.crashlander.lander.design.LanderDesignAttach;
import dev.basvs.crashlander.lander.design.LanderDesignPart;
import dev.basvs.crashlander.lander.part.Part;
import dev.basvs.crashlander.lander.part.PartDesign;
import dev.basvs.crashlander.particle.ParticleGenerator;
import dev.basvs.crashlander.particle.ParticleManager;
import dev.basvs.lib.math.MathUtils;

import java.io.BufferedReader;
import java.util.HashMap;

public class LanderBuilder {

	public static final short COLLISION_CATEGORY_PARTS = 0x0002;
	public static final short COLLISION_CATEGORY_FRAME = 0x0004;
	public static final short COLLISION_CATEGORY_WORLD = 0x0008;

	public enum Angle {
		Up, Right, Down, Left
	};

	private HashMap<String, PartDesign> partDesigns = new HashMap<String, PartDesign>();

	private TextureAtlas partTextures, particleTextures;

	private World world;
	private ParticleManager particleManager;

	public LanderBuilder(World world, ParticleManager particles) throws Exception {
		this.world = world;
		this.particleManager = particles;

		partTextures = CrashLander.getInstance().getAssets().get("data/parts.atlas", TextureAtlas.class);
		particleTextures = CrashLander.getInstance().getAssets().get("data/particles.atlas", TextureAtlas.class);

		Json json = new Json();

		// Load part designs
		BufferedReader br = new BufferedReader(Gdx.files.internal("data/parts.json").reader());
		PartDesign[] partDesignArray = json.fromJson(PartDesign[].class, br);
		br.close();
		for (PartDesign pd : partDesignArray) {
			// Convert shape to box coordinates
			for (Vector2 vector : pd.shape) {
				vector.scl(GameScreen.WORLD_TO_BOX2D);
			}
			partDesigns.put(pd.name, pd);
		}
	}

	public Lander buildFromDesign(LanderDesign design, float xPos, float yPos) {
		Lander lander = new Lander();
		lander.design = design;
		lander.core = buildPart(design.core, new Vector2(xPos, yPos));

		/*
		 * Part cockpit = buildPart("Cockpit", new Vector2(xPos, yPos)); lander.core =
		 * cockpit;
		 * 
		 * Part largeTank = buildPart("Large fuel tank"); attach(cockpit, 1, largeTank,
		 * 0, Angle.Down);
		 * 
		 * Part largeThruster = buildPart("Large engine"); largeThruster.control =
		 * Control.Up; largeThruster.attachedTanks.add(largeTank); attach(largeTank, 2,
		 * largeThruster, 0, Angle.Down);
		 * 
		 * Part leftTank = buildPart("Small fuel tank"); attach(largeTank, 3, leftTank,
		 * 1, Angle.Down);
		 * 
		 * Part leftTank2 = buildPart("Small fuel tank"); attach(leftTank, 0, leftTank2,
		 * 2, Angle.Down);
		 * 
		 * Part leftThruster = buildPart("Small engine"); leftThruster.control =
		 * Control.Right; leftThruster.attachedTanks.add(leftTank);
		 * leftThruster.attachedTanks.add(leftTank2); attach(leftTank, 2, leftThruster,
		 * 0, Angle.Down);
		 * 
		 * Part rightTank = buildPart("Small fuel tank"); attach(largeTank, 1,
		 * rightTank, 3, Angle.Down);
		 * 
		 * Part rightTank2 = buildPart("Small fuel tank"); attach(rightTank, 0,
		 * rightTank2, 2, Angle.Down);
		 * 
		 * Part rightThruster = buildPart("Small engine"); rightThruster.control =
		 * Control.Left; rightThruster.attachedTanks.add(rightTank);
		 * rightThruster.attachedTanks.add(rightTank2); attach(rightTank, 2,
		 * rightThruster, 0, Angle.Down);
		 */

		return lander;
	}

	public Part buildPart(LanderDesignPart landerDesignPart) {
		return buildPart(landerDesignPart, null);
	}

	// public Part buildPart(String designName, Vector2 position) {
	public Part buildPart(LanderDesignPart landerDesignPart, Vector2 position) {
		// TODO fix position thing
		PartDesign design = partDesigns.get(landerDesignPart.partName);

		Part part = new Part();
		part.design = design;
		initBuild(part, design, partTextures.findRegion(design.textureName), position);

		part.body.getFixtureList().get(0).getFilterData().categoryBits = COLLISION_CATEGORY_PARTS;
		part.body.getFixtureList().get(0).getFilterData().maskBits = COLLISION_CATEGORY_WORLD;

		// TODO: implement light functionality

		if (design.thruster != null) {
			ParticleGenerator particleGen = new ParticleGenerator();
			particleGen.position = new Vector2(0, 0);
			particleGen.velocity = new Vector2(0, 0);
			particleGen.texture = new TextureRegion[] { particleTextures.findRegion("particle1"),
					particleTextures.findRegion("particle2"), particleTextures.findRegion("particle3") };
			particleGen.startColor = new Color(1f, 1f, 0f, 1f);
			particleGen.endColor = new Color(0.3f, 0.3f, 0.2f, 0.2f);
			particleGen.angleSpread = MathUtils.HALF_PI / 6;
			particleGen.fireInterval = 0.004f;
			particleGen.timeUntilFire = MathUtils.randomFloat() * particleGen.fireInterval;
			particleGen.lifeMinimum = 0.3f;
			particleGen.lifeMaximum = 0.6f;
			particleGen.speedMinimum = 6f;
			particleGen.speedMinimum = 7.5f;
			particleManager.add(particleGen);
			part.particleGen = particleGen;
		}

		part.control = landerDesignPart.control;

		for (LanderDesignAttach attach : landerDesignPart.attached) {
			Part otherPart = buildPart(attach.otherPart, position);
			if (attach.fuelSource) {
				otherPart.fuelSources.add(part);
			}
			attach(part, attach.attachPoint, otherPart, attach.otherAttachPoint, attach.attachAngle);
		}

		return part;
	}

	public void attach(Part firstPart, int firstAttach, Part secondPart, int secondAttach, Angle angle) {
		// Make Box2D joint
		WeldJointDef jointDef = new WeldJointDef();
		jointDef.bodyA = firstPart.body;
		jointDef.bodyB = secondPart.body;
		jointDef.localAnchorA.set(firstPart.design.attach.get(firstAttach).x * GameScreen.WORLD_TO_BOX2D,
				firstPart.design.attach.get(firstAttach).y * GameScreen.WORLD_TO_BOX2D);
		jointDef.localAnchorB.set(secondPart.design.attach.get(secondAttach).x * GameScreen.WORLD_TO_BOX2D,
				secondPart.design.attach.get(secondAttach).y * GameScreen.WORLD_TO_BOX2D);
		jointDef.bodyB.setTransform(jointDef.bodyA.getPosition().x + jointDef.localAnchorA.x - jointDef.localAnchorB.x,
				jointDef.bodyA.getPosition().y + jointDef.localAnchorA.y - jointDef.localAnchorB.y, 0f);
		jointDef.collideConnected = false;
		switch (angle) {
		case Up:
			jointDef.referenceAngle = MathUtils.PI;
			break;
		case Down:
			break;
		case Left:
			jointDef.referenceAngle = -MathUtils.HALF_PI;
			break;
		case Right:
			jointDef.referenceAngle = MathUtils.HALF_PI;
			break;
		}
		jointDef.collideConnected = false;
		world.createJoint(jointDef);

		// TODO: position body b to correct place
	}

	private void initBuild(Part part, PartDesign design, TextureRegion texture, Vector2 position) {

		part.texture = texture;
		part.textureDeltaX = design.textureDeltaX;
		part.textureDeltaX = design.textureDeltaY;

		if (design.tank != null) {
			part.fuel = design.tank.fuel;
		}

		// Add body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		if (position == null) {
			bodyDef.position.set(0, 0);
		} else {
			bodyDef.position.set(position);
		}
		part.body = world.createBody(bodyDef);
		part.body.setUserData(part);
		// Give body a shape
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.set((Vector2[]) design.shape.toArray(Vector2.class));
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.9f;
		fixtureDef.restitution = 0.25f;
		part.body.createFixture(fixtureDef);
		polygonShape.dispose();
	}
}