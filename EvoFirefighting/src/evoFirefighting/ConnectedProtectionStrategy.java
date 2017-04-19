package evoFirefighting;

import strategy.Strategy;
import strategy.connectedStrategy.ConnectedStrategy;

public class ConnectedProtectionStrategy extends ConnectedStrategy {
	
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
