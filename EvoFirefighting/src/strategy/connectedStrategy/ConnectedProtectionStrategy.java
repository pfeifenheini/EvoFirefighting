package strategy.connectedStrategy;

import grid.Coordinate;
import grid.Grid;
import strategy.Strategy;

/**
 * Implements a connected strategy for the highway protection problem.
 * @see ConnectedStrategy
 * @author Martin
 *
 */
public class ConnectedProtectionStrategy extends ConnectedStrategy {
	/** The distance to the highway */
	private int highwayDistance = 20;
	
	/**
	 * Constructor.
	 * @see ConnectedStrategy
	 * @param highwayDistance Distance from the origin to the Highway.
	 */
	public ConnectedProtectionStrategy(
			int simulationTime,
			double initialAccount,
			double budget,
			double mutationRate,
			Coordinate startOffset,
			int highwayDistance) {
		super(simulationTime, initialAccount, budget, mutationRate, startOffset);
		this.highwayDistance = highwayDistance;
		grid = new Grid(2*this.simulationTime+1,this.simulationTime+this.highwayDistance+1);
		grid.setupHighway();
		startFire = new Coordinate(this.simulationTime,this.highwayDistance);
		
		if(startOffset == null)
			startOffset = new Coordinate(0, 0);
			
		start = new Coordinate(
				Math.max(0, Math.min(startFire.x+startOffset.x, grid.width()-1)),
				Math.max(0, Math.min(startFire.y+startOffset.y, grid.height()-1)));
		
		reset();
	}

	@Override
	public double fitness() {
		if(fitness<0) {
			while(step());
			fitness = grid.timeHighwayReached();
		}
		return fitness;
	};
	
	@Override
	public void reset() {
		super.reset();
		grid.setupHighway();
	}
	
	@Override
	public int compareTo(Strategy s) {
		ConnectedProtectionStrategy toCompare = (ConnectedProtectionStrategy) s;
		
		while(step());
		while(toCompare.step());
		
		if(grid.timeHighwayReached()!=toCompare.grid.timeHighwayReached()) {
			return toCompare.grid.timeHighwayReached()-grid.timeHighwayReached();
		}
		
		for(int row=0;row<grid.height();row++) {
			if(grid.burningCells(row)!=toCompare.grid.burningCells(row))
				return grid.burningCells(row)-toCompare.grid.burningCells(row);
			
		}
		return 0;
	}
}
