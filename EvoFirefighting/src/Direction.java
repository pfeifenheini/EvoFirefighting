
public enum Direction {
	N, NE, E, SE, S, SW, W, NW;
	
	/**
	 * @return next direction in clockwise order
	 */
	public Direction nextCW() {
		return values()[(ordinal()+1)%8];
	}
	
	/**
	 * @return next direction in counter clockwise order
	 */
	public Direction nextCCW() {
		return values()[(ordinal()+7)%8];
	}
}
