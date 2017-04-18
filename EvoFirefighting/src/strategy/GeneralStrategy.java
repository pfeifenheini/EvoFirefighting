package strategy;


import java.util.Random;

import grid.Coordinate;
import grid.Grid;
import strategy.connectedStrategy.ConnectedStrategy;

public abstract class GeneralStrategy implements Strategy {

	/** random number generator that has to be used for all random processes **/
	protected static Random rand = new Random();
	
	protected static int gridWidth = 151;
	protected static int gridHeigth = 151;
	protected static double initialAccount = 2.0;
	protected static double budget = 1.8;
	protected static double mutationRate = 2.5;
	protected static Coordinate startFire  = new Coordinate(gridWidth/2,gridHeigth/2);
	
	protected Grid grid;
	
	protected double fitness = -1.0;
	protected boolean finished = false;
	
	public static void setParameters(
			int gridWidth,
			int gridHeigth,
			double initialAccount,
			double budget,
			double mutationRate,
			Coordinate startFire) {
		ConnectedStrategy.gridWidth = gridWidth;
		ConnectedStrategy.gridHeigth = gridHeigth;
		ConnectedStrategy.initialAccount = initialAccount;
		ConnectedStrategy.budget = budget;
		ConnectedStrategy.mutationRate = mutationRate;
		ConnectedStrategy.startFire = startFire;
	}

	@Override
	public boolean spreadStep() {
		if(finished()) return false;
		boolean fireStopped = !grid.spreadFire();
		boolean edgeReached = grid.fireReachedEdge();
		finished = fireStopped || edgeReached;
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
		grid.ignite(startFire.x, startFire.y);
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
