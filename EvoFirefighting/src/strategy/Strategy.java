package strategy;

import grid.Grid;

/**
 * This interface defines a general strategy. 
 * @author Martin
 *
 */
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
	 * Combines one protection and one spreading step.
	 * @return true if the fire can continue spreading
	 */
	public boolean step();
	/**
	 * Returns whether this strategy is finished. That means ne fire has been spread for the maximal simulation time,
	 * or can no longer spread. 
	 * @return
	 */
	public boolean finished();
	/**
	 * Returns the fitness of the strategy.
	 * @return
	 */
	public double fitness();
	/**
	 * Returns a clone of the current grid the strategy uses for simulation.
	 * @return
	 */
	public Grid cloneGrid();
	/**
	 * Resets this strategy. That means the grid is cleared and only the origin is set on fire.
	 */
	public void reset();
	/**
	 * Performs a mutation on this strategy. A mutation is usually a small random change
	 * to the parameters that define the strategy.
	 */
	public void mutate();
	/**
	 * Copies the parameters of the given Strategy.
	 * @param arg0 Strategy that shall be copied.
	 */
	public void copy(Strategy arg0);
	public Strategy clone();
	public int compareTo(Strategy arg0);
}
