package dev.basvs.crashlander.lander.design;

import com.badlogic.gdx.utils.Array;
import dev.basvs.crashlander.controller.LanderController.Control;
import dev.basvs.crashlander.lander.LanderBuilder.Angle;

public class LanderDesignPart {
	public String partName;
	public Array<LanderDesignAttach> attached = new Array<LanderDesignAttach>();
	public Control control;

	public LanderDesignPart control(Control control) {
		this.control = control;
		return this;
	}

	public LanderDesignPart attach(String partName, int attachPoint, int otherAttachPoint, Angle angle,
			boolean fuelSource) {
		LanderDesignAttach newAttach = new LanderDesignAttach();
		newAttach.attachPoint = attachPoint;
		newAttach.otherAttachPoint = otherAttachPoint;
		newAttach.attachAngle = angle;
		newAttach.otherPart = new LanderDesignPart();
		newAttach.otherPart.partName = partName;
		newAttach.fuelSource = fuelSource;
		attached.add(newAttach);
		return newAttach.otherPart;
	}
}
