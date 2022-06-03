package dev.basvs.crashlander.terrain;

import com.badlogic.gdx.physics.box2d.World;

public interface ILanderWorldGenerator {

	public LanderWorld generate(World world, float widthM);

}
