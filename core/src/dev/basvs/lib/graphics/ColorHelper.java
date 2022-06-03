package dev.basvs.lib.graphics;

import com.badlogic.gdx.graphics.Color;

public class ColorHelper {

	public static void setColorAsHSV(Color color, float hue, float saturation, float value, float alpha) {

		int h = (int) (hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0:
			color.set(value, t, p, alpha);
			break;
		case 1:
			color.set(q, value, p, alpha);
			break;
		case 2:
			color.set(p, value, t, alpha);
			break;
		case 3:
			color.set(p, q, value, alpha);
			break;
		case 4:
			color.set(t, p, value, alpha);
			break;
		case 5:
			color.set(value, p, q, alpha);
			break;
		default:
			throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", "
					+ saturation + ", " + value);
		}
	}

	public static Color getColorAsHSV(float hue, float saturation, float value, float alpha) {
		Color color = new Color(1f, 1f, 1f, 1f);
		setColorAsHSV(color, hue, saturation, value, alpha);
		return color;
	}
}
