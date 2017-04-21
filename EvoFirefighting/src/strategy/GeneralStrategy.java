package strategy;


import java.util.Random;

import grid.Grid;

public abstract class GeneralStrategy implements Strategy {

//	public static double defaultInitialAccount = 2.0;
//	public static double defaultBudget = 2.0;
//	public static int defaultSimulationTime = 50;
//	public static double defaultMutationRate = 2.5;
	
	/** random number generator that has to be used for all random processes **/
	protected static Random rand = new Random();
	
	protected double initialAccount = 2.0;
	protected double budget = 2.0;
	protected int simulationTime = 50;
	protected double mutationRate = 2.5;
//	protected Coordinate startFire = null;
	
	
	protected Grid grid;
	
	protected double fitness = -1.0;
	protected boolean finished = false;
	
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
		
//		startFire  = new Coordinate(this.simulationTime,this.simulationTime);
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
		grid.ignite(grid.width()/2, grid.heigth()/2);
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
