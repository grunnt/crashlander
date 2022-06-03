package dev.basvs.lib.math;

/**
 * Fast 32-bit pseudo random number generator using XORShift. Roughly 2 or 3 times as fast as the Java implementation.
 */
public class FastRandom {

	// Current "seed" of the random number generator, changes with every number generated.
	private int seed;

	/**
	 * Create a random number generator with a seed based on the system clock.
	 */
	public FastRandom() {
		setSeed((int) System.nanoTime());
	}

	/**
	 * * Create a random number generator with a specified seed.
	 * 
	 * @param seed
	 */
	public FastRandom(int seed) {
		setSeed(seed);
	}

	/**
	 * Set a new seed for the random number generator.
	 * 
	 * @param seed
	 */
	public final void setSeed(int seed) {
		this.seed = seed != 0 ? seed : 1;
	}

	/**
	 * Get the seed of the random number generator. Note that each generated number changes this seed.
	 * 
	 * @return
	 */
	public final int getSeed() {
		return seed;
	}

	/**
	 * Generate a pseudo-random integer value.
	 * 
	 * @return
	 */
	public final int nextInt() {
		seed ^= (seed << 13);
		seed ^= (seed >>> 17);
		seed ^= (seed << 5);

		return seed;
	}

	/**
	 * Generate a pseudo-random integer value in the range [0, n).
	 * 
	 * @param n
	 * @return
	 */
	public final int nextInt(int n) {
		return ((nextInt() >>> 15) * n) >>> 17;
	}

	/**
	 * Generate a pseudo-random float value in the range [0, 1).
	 * 
	 * @return
	 */
	public final float nextFloat() {
		return (nextInt() >>> 8) * 0x1p-24f;
	}

	/**
	 * Generate a pseudo-random boolean value.
	 * 
	 * @return
	 */
	public final boolean nextBoolean() {
		return nextInt() > 0;
	}
}
