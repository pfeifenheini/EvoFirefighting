package strategy.connectedStrategy;

import grid.Coordinate;
import strategy.Strategy;

public class ConnectedProtectionStrategy extends ConnectedStrategy {
	
	public ConnectedProtectionStrategy(
			int simulationTime,
			double initialAccount,
			double budget,
			double mutationRate,
			Coordinate startFire,
			Coordinate startProtection) {
		super(simulationTime, initialAccount, budget, mutationRate, startFire, startProtection);
	}

	@Override
	public double fitness() {
		while(step());
		return grid.timeToReachBottom();
	};
	
	@Override
	public int compareTo(Strategy s) {
		ConnectedProtectionStrategy toCompare = (ConnectedProtectionStrategy) s;
		
		while(step());
		while(toCompare.step());
		
		if(grid.timeToReachBottom()!=toCompare.grid.timeToReachBottom())
			return toCompare.grid.timeToReachBottom()-grid.timeToReachBottom();
		
		for(int row=0;row<grid.heigth();row++) {
			if(grid.burningCells(row)!=toCompare.grid.burningCells(row))
				return grid.burningCells(row)-toCompare.grid.burningCells(row);
			
		}
		return 0;
	}
}
