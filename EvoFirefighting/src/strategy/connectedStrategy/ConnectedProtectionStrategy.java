package strategy.connectedStrategy;

import grid.Coordinate;
import grid.Grid;
import strategy.Strategy;

public class ConnectedProtectionStrategy extends ConnectedStrategy {
	
	private int highwayDistance = 20;
	
	public ConnectedProtectionStrategy(
			int simulationTime,
			double initialAccount,
			double budget,
			double mutationRate,
			Coordinate startOffset,
			int highwayDistance) {
		super(simulationTime, initialAccount, budget, mutationRate, startOffset);
		this.highwayDistance = highwayDistance;
		grid = new Grid(2*this.simulationTime+1,2*this.highwayDistance+1);
		
		if(startOffset != null)
			start = new Coordinate(
					Math.max(0, Math.min(grid.width()/2+startOffset.x, grid.width()-1)),
					Math.max(0, Math.min(grid.heigth()/2+startOffset.y, grid.heigth()-1)));
		
		reset();
	}

	@Override
	public double fitness() {
		if(fitness<0) {
			while(step());
			fitness = grid.timeBottomReached();
		}
		return fitness;
	};
	
	@Override
	public int compareTo(Strategy s) {
		ConnectedProtectionStrategy toCompare = (ConnectedProtectionStrategy) s;
		
		while(step());
		while(toCompare.step());
		
		if(grid.timeBottomReached()!=toCompare.grid.timeBottomReached()) {
			return toCompare.grid.timeBottomReached()-grid.timeBottomReached();
		}
		
		for(int row=0;row<grid.heigth();row++) {
			if(grid.burningCells(row)!=toCompare.grid.burningCells(row))
				return grid.burningCells(row)-toCompare.grid.burningCells(row);
			
		}
		return 0;
	}
}
