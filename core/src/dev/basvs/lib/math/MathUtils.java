package dev.basvs.lib.math;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Utility and fast math functions.<br>
 * <br>
 * Thanks to Riven on JavaGaming.org for sin/cos/atan2/floor/ceil.<br>
 */
public class MathUtils {

	public static final float TWO_PI = 6.283185307179586476925286766559f;
	static public final float PI = TWO_PI / 2f;
	static public final float HALF_PI = TWO_PI / 4f;

	static private final int SIN_BITS = 13; // Adjust for accuracy.
	static private final int SIN_MASK = ~(-1 << SIN_BITS);
	static private final int SIN_COUNT = SIN_MASK + 1;

	static private final float radFull = PI * 2;
	static private final float degFull = 360;
	static private final float radToIndex = SIN_COUNT / radFull;
	static private final float degToIndex = SIN_COUNT / degFull;

	static public final float RADIANS_TO_DEGREES = 180f / PI;
	static public final float DEGREES_TO_RADIANS = PI / 180;

	static public final float[] sin = new float[SIN_COUNT];
	static public final float[] cos = new float[SIN_COUNT];

	static public final FastRandom random = new FastRandom();

	static {
		for (int i = 0; i < SIN_COUNT; i++) {
			float a = (i + 0.5f) / SIN_COUNT * radFull;
			sin[i] = (float) Math.sin(a);
			cos[i] = (float) Math.cos(a);
		}
		for (int i = 0; i < 360; i += 90) {
			sin[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * DEGREES_TO_RADIANS);
			cos[(int) (i * degToIndex) & SIN_MASK] = (float) Math.cos(i * DEGREES_TO_RADIANS);
		}
	}

	static public final float sin(float rad) {
		return sin[(int) (rad * radToIndex) & SIN_MASK];
	}

	static public final float cos(float rad) {
		return cos[(int) (rad * radToIndex) & SIN_MASK];
	}

	static public final float sinDeg(float deg) {
		return sin[(int) (deg * degToIndex) & SIN_MASK];
	}

	static public final float cosDeg(float deg) {
		return cos[(int) (deg * degToIndex) & SIN_MASK];
	}

	// ---

	static private final int ATAN2_BITS = 7; // Adjust for accuracy.
	static private final int ATAN2_BITS2 = ATAN2_BITS << 1;
	static private final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	static private final int ATAN2_COUNT = ATAN2_MASK + 1;
	static private final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
	static private final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);
	static private final float[] atan2 = new float[ATAN2_COUNT];
	static {
		for (int i = 0; i < ATAN2_DIM; i++) {
			for (int j = 0; j < ATAN2_DIM; j++) {
				float x0 = (float) i / ATAN2_DIM;
				float y0 = (float) j / ATAN2_DIM;
				atan2[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
			}
		}
	}

	static public final float atan2(float y, float x) {
		float add, mul;
		if (x < 0) {
			if (y < 0) {
				y = -y;
				mul = 1;
			} else
				mul = -1;
			x = -x;
			add = -PI;
		} else {
			if (y < 0) {
				y = -y;
				mul = -1;
			} else
				mul = 1;
			add = 0;
		}
		float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);
		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);
		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}

	// ---

	static public int nextPowerOfTwo(int value) {
		if (value == 0)
			return 1;
		value--;
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	static public boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	static public int clamp(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public short clamp(short value, short min, short max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public float clamp(float value, float min, float max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	// ---

	static private final int BIG_ENOUGH_INT = 16 * 1024;
	static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
	static private final double CEIL = 0.9999999;
	static private final double BIG_ENOUGH_CEIL = Double
			.longBitsToDouble(Double.doubleToLongBits(BIG_ENOUGH_INT + 1) - 1);
	static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

	/**
	 * Returns the largest integer less than or equal to the specified float. This method will only properly floor
	 * floats from -(2^14) to (Float.MAX_VALUE - 2^14).
	 */
	static public int floor(float x) {
		return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	/**
	 * Returns the largest integer less than or equal to the specified float. This method will only properly floor
	 * floats that are positive. Note this method simply casts the float to int.
	 */
	static public int floorPositive(float x) {
		return (int) x;
	}

	/**
	 * Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil
	 * floats from -(2^14) to (Float.MAX_VALUE - 2^14).
	 */
	static public int ceil(float x) {
		return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
	}

	/**
	 * Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil
	 * floats that are positive.
	 */
	static public int ceilPositive(float x) {
		return (int) (x + CEIL);
	}

	/**
	 * Returns the closest integer to the specified float. This method will only properly round floats from -(2^14) to
	 * (Float.MAX_VALUE - 2^14).
	 */
	static public int round(float x) {
		return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
	}

	/**
	 * Returns the closest integer to the specified float. This method will only properly round floats that are
	 * positive.
	 */
	static public int roundPositive(float x) {
		return (int) (x + 0.5f);
	}

	/**
	 * Calculate the square of the distance between two points. Useful for fast comparisons of distances.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static float distanceSq(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return dx * dx + dy * dy;
	}

	/**
	 * Calculate the exact distance between two points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static float distance(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Calculate the length of a vector.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static float length(float x, float y) {
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * @param array
	 */
	public static void randomIntArrayPermutation(int[] array, FastRandom random) {

		int length = array.length;

		for (int i = 0; i < length; i++) {

			// randomly chosen position in array whose element
			// will be swapped with the element in position i
			// note that when i = 0, any position can chosen (0 thru length-1)
			// when i = 1, only positions 1 through length -1
			// NOTE: r is an instance of java.util.Random
			int ran = i + random.nextInt(length - i);

			// perform swap
			int temp = array[i];
			array[i] = array[ran];
			array[ran] = temp;
		}
	}

	/**
	 * @param array
	 */
	public static void randomObjectArrayPermutation(Object[] array, int size, FastRandom random) {

		for (int i = 0; i < size; i++) {

			// randomly chosen position in array whose element
			// will be swapped with the element in position i
			// note that when i = 0, any position can chosen (0 thru length-1)
			// when i = 1, only positions 1 through length -1
			// NOTE: r is an instance of java.util.Random
			int ran = i + random.nextInt(size - i);

			// perform swap
			Object temp = array[i];
			array[i] = array[ran];
			array[ran] = temp;
		}
	}

	public static float randomFloat() {
		return random.nextFloat();
	}

	public static int randomInt() {
		return random.nextInt();
	}

	public static int randomInt(int n) {
		return random.nextInt(n);
	}

	public static boolean randomBoolean() {
		return random.nextBoolean();
	}

	public static float randomAngle() {
		return random.nextFloat() * MathUtils.TWO_PI;
	}
}
