package strategy;


import java.util.Random;

import grid.Grid;

/**
 * This class implements a general strategy, since some functions will be identical throughout all
 * different strategies. However if necessary, functions can be overridden.
 * @author Martin
 *
 */
public abstract class GeneralStrategy implements Strategy {
	
	/** Random number generator that has to be used for all random processes. **/
	protected static Random rand = new Random();
	
	/** Initial account, defines the number of cells that can be protected in the first step. */
	protected double initialAccount = 2.0;
	/** Defines how many cells can be protected at each step */
	protected double budget = 2.0;
	/** Number of steps the strategy is simulated before determining its fitness. */
	protected int simulationTime = 50;
	/** A higher mutation rate increases the cahnce for changes during the mutation step */
	protected double mutationRate = 2.5;
	
	/** The grid that is used for the simulation of the strategy. */
	protected Grid grid;
	
	/** Fitness of this strategy. A value of -1 means that the fitness has not yet ben determined. */
	protected double fitness = -1.0;
	/** Indicates whether the simulation of this strategy is finished */
	protected boolean finished = false;
	
	/**
	 * Constructor
	 * @param simulationTime maximal number of simulated fire spreads
	 * @param initialAccount initial account defines the number of cell protecte in the first step
	 * @param budget defines how many cells can be protected per step
	 * @param mutationRate A higher mutation rate increases the cahnce for changes during the mutation step
	 */
	public GeneralStrategy(
			int simulationTime,
			double initialAccount,
			double budget,
			double mutationRate) {
		if(simulationTime>=0)
			this.simulationTime = simulationTime;
		if(initialAccount>=0)
			this.initialAccount = initialAccount;
		if(budget>=0)
			this.budget = budget;
		if(mutationRate>=0)
			this.mutationRate = mutationRate;
		
		grid = new Grid(2*this.simulationTime+1,2*this.simulationTime+1);
	}

	@Override
	public boolean spreadStep() {
		if(finished()) return false;
		boolean fireStopped = !grid.spreadFire();
		finished = fireStopped || grid.time()>=simulationTime;
		return !finished;
	}
	

	@Override
	public boolean step() {
		if(finished()) return false;
		protectStep();
		return spreadStep();
	}

	@Override
	public boolean finished() {
		return finished;
	}

	@Override
	public double fitness() {
		if(fitness<0) {
			while(step());
			fitness = grid.burningCells();
		}
		
		return fitness;
	}

	@Override
	public Grid cloneGrid() {
		return grid.clone();
	}

	@Override
	public void reset() {
		grid.reset();
		grid.ignite(grid.width()/2, grid.height()/2);
		finished = false;
	}

	@Override
	public Strategy clone() {
		try {
			return (Strategy) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int compareTo(Strategy s) {
		return (int) Math.signum(fitness()-s.fitness());
	}
}
