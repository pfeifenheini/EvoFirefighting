package strategy.scatteredStrategy;

import grid.Coordinate;
import grid.Grid;
import strategy.GeneralStrategy;
import strategy.Strategy;

/**
 * Implements a scattered strategy for the highway protection problem.
 * @see ScatteredStrategy
 * @author Martin
 *
 */
public class ScatteredProtectionStrategy extends ScatteredStrategy {
	/** The distance to the highway */
	private int highwayDistance = 20;
	
	/**
	 * Constructor
	 * @see GeneralStrategy
	 * @param highwayDistance Distance from the origin to the Highway.
	 */
	public ScatteredProtectionStrategy(
			int simulationTime,
			double initialAccount,
			double budget,
			double mutationRate,
			int highwayDistance) {
		super(simulationTime, initialAccount, budget, mutationRate);
		this.highwayDistance = highwayDistance;
		grid = new Grid(2*this.simulationTime+1,this.simulationTime+this.highwayDistance+1);
		grid.setupHighway();
		startFire = new Coordinate(this.simulationTime,this.highwayDistance);
		
		int x, y;
		for(int i=0;i<sequence.size();i++) {
			x = (int)(rand.nextGaussian()*grid.width()/10.0+startFire.x);
			y = (int)(rand.nextGaussian()*this.highwayDistance/5.0+startFire.y);
			x = Math.max(0, Math.min(x, grid.width()-1));
			y = Math.max(0, Math.min(y, grid.height()-1));
			sequence.set(i,new Coordinate(x,y));
		}
		
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
		ScatteredProtectionStrategy toCompare = (ScatteredProtectionStrategy) s;
		
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
