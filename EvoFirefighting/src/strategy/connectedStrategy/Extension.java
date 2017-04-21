package strategy.connectedStrategy;

import java.util.Random;

/**
 * An Extension is used by connected strategies and defines a direction and whether the front or
 * the back of a strategy.
 * @see ConnectedStrategy
 * @author Martin
 *
 */
class Extension implements Cloneable {
	/** direction of the the cell */
	Direction dir;
	/** indicator whether to extend the front or the back of the barrier */
	boolean extendFront;
	
	/**
	 * Constructor. Initializes randomly using given number generator.
	 * @param rand Random number generator used.
	 */
	public Extension(Random rand) {
		dir = Direction.values()[rand.nextInt(8)];
		extendFront = rand.nextBoolean();
	}
	
	/**
	 * Constructor. Initializes with given values.
	 * @param dir
	 * @param extendFront
	 */
	public Extension(Direction dir, boolean extendFront) {
		this.dir = dir;
		this.extendFront = extendFront;
	}
	
	/**
	 * Overwrites the parameters of this extension with those of a given one.
	 * @param toCopy
	 */
	public void copy(Extension toCopy) {
		dir = toCopy.dir;
		extendFront = toCopy.extendFront;
	}
	
	@Override
	public Extension clone() {
		Extension e = null;
		try {
			e = (Extension) super.clone();
			e.dir = dir;
			e.extendFront = extendFront;
		} catch (CloneNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return e;
	}
}
