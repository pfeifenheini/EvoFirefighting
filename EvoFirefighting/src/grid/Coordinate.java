package grid;

/**
 * Implements a two dimensional coordinate.
 * @author Martin
 *
 */
public class Coordinate implements Cloneable {
	/** x coordinate */
	public int x;
	/** y coordinate */
	public int y;
	
	/**
	 * Constructor
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Copies the values of a given coordinate.
	 * @param toCopy
	 */
	public void copy(Coordinate toCopy) {
		x = toCopy.x;
		y = toCopy.y;
	}
	
	@Override
	public Coordinate clone() {
		Coordinate c = null;
		try {
			c = (Coordinate) super.clone();
			c.x = x;
			c.y = y;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
}
