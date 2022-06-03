package dev.basvs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import dev.basvs.crashlander.lander.part.PartDesign;
import dev.basvs.crashlander.lander.part.Tank;
import dev.basvs.crashlander.lander.part.Thruster;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

/**
 * Copyright (c) 2013, Bas van Schoonhoven. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
