package strategy;

import grid.Grid;

public interface Strategy extends Cloneable, Comparable<Strategy> {

	/**
	 * Protects all cells possible in the current step according to the concrete strategy.
	 * @return true if at least one additional cell has been protected, false if no new cell can be protected anymore
	 */
	public boolean protectStep();
	/**
	 * Spreads the fire for one step.
	 * @return true if the fire can continue spreading
	 */
	public boolean spreadStep();
	/**
	 * combines one protection and one spreading step.
	 * @return true if the fire can continue spreading
	 */
	public boolean step();
	public boolean finished();
	public double fitness();
	public Grid cloneGrid();
	public void reset();
	public void mutate();
	public Strategy generateOffspring(Strategy p1, Strategy p2);
	public Strategy clone();
	public int compareTo(Strategy s);
}
