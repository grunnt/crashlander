package dev.basvs.crashlander.lander;

import com.badlogic.gdx.physics.box2d.Body;
import dev.basvs.crashlander.GameScreen;
import dev.basvs.crashlander.lander.design.LanderDesign;
import dev.basvs.crashlander.lander.part.Part;

public class Lander {

	public Part core;
	public LanderDesign design;

	public void teleport(float deltaX, float deltaY) {

		System.out.println("teleport " + (int) deltaX + " , " + (int) deltaY);

		teleportBodyRecursive(core.body, deltaX * GameScreen.WORLD_TO_BOX2D, deltaY * GameScreen.WORLD_TO_BOX2D);
	}

	private void teleportBodyRecursive(Body body, float deltaX, float deltaY) {
		body.setTransform(body.getPosition().x + deltaX, body.getPosition().y + deltaY, 0f);
		int nAttach = body.getJointList().size;
		for (int a = 0; a < nAttach; a++) {
			Body other = body.getJointList().get(a).joint.getBodyB();
			if (other != body) {
				teleportBodyRecursive(other, deltaX, deltaY);
			}
		}
	}
}
