package dev.basvs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Json;
import dev.basvs.crashlander.lander.part.PartDesign;
import dev.basvs.crashlander.lander.part.Tank;
import dev.basvs.crashlander.lander.part.Thruster;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PartFileGenerator {

	public static final String PART_DESIGNS_FILE = "../lander-android/assets/data/parts.json";

	public static void main(String[] args) {

		try {

			List<PartDesign> partDesigns = new ArrayList<PartDesign>();

			PartDesign design = new PartDesign();
			design.name = "Part name";
			design.core = true;
			design.tank = new Tank();
			design.tank.fuel = 1;
			design.mass = 1;
			design.fuelTransmitter = true;
			design.thruster = new Thruster();
			design.thruster.burnout = true;
			design.thruster.fuelPerSecond = 1;
			design.thruster.powerPerFuel = 1;
			design.thruster.position = new Vector2(1, 1);
			design.textureName = "texture";

			design.shape = new Array<Vector2>();
			design.shape.add(new Vector2(0, 0));
			design.shape.add(new Vector2(0, 1));
			design.shape.add(new Vector2(1, 0));

			design.attach = new Array<Vector2>();
			design.attach.add(new Vector2(1, 1));

			partDesigns.add(design);

			Json json = new Json();
			String jsonString = json.prettyPrint(partDesigns);
			File file = new File(PART_DESIGNS_FILE);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(jsonString);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
