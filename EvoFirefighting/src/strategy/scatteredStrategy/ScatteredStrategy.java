package strategy.scatteredStrategy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import grid.Coordinate;
import grid.Grid;
import strategy.GeneralStrategy;
import strategy.Strategy;

public class ScatteredStrategy extends GeneralStrategy {
	
	protected ListIterator<Coordinate> it;
	
	protected ArrayList<Coordinate> sequence;
	
	public ScatteredStrategy(
			int simulationTime,
			double initialAccount,
			double budget,
			double mutationRate) {
		super(simulationTime, initialAccount, budget, mutationRate);
		int sequenceLength = (int)(this.initialAccount+this.budget*this.simulationTime);
		sequence = new ArrayList<Coordinate>(sequenceLength);
		int x, y;
		for(int i=0;i<sequenceLength;i++) {
			x = (int)(rand.nextGaussian()*grid.width()/10.0+grid.width()/2);
			y = (int)(rand.nextGaussian()*grid.heigth()/10.0+grid.heigth()/2);
			x = Math.max(0, Math.min(x, grid.width()-1));
			y = Math.max(0, Math.min(y, grid.heigth()-1));
			sequence.add(new Coordinate(x,y));
		}
		reset();
	}
	
	@Override
	public boolean protectStep() {
		if(finished()) return false;
		Coordinate nextCoord;
		boolean success = false;
		double account = initialAccount + grid.time()*budget - grid.protectedCells();
		
		while(it.hasNext() && account >= 1.0) {
			nextCoord = it.next(); 
			if(grid.protect(nextCoord.x,nextCoord.y)) {
				success = true;
				account -= 1;
			}
		}
		
		return success;
	}

	@Override
	public void reset() {
		super.reset();
		it = sequence.listIterator();
	}

	@Override
	public void mutate() {
		boolean changed = false;
		for(int i=0;i<sequence.size();i++) {
			if(rand.nextDouble()<mutationRate/sequence.size()) {
//				if(rand.nextBoolean())
					gaussianWiggle(sequence.get(i));
//				else
//					Collections.swap(sequence, i, rand.nextInt(sequence.size()));
				changed = true;
			}
		}
		
		Coordinate fireStart = new Coordinate(grid.width()/2,grid.heigth()/2);
		Collections.sort(sequence, new Comparator<Coordinate>() {
			@Override
			public int compare(Coordinate arg0, Coordinate arg1) {
				return Math.abs(arg0.x-fireStart.x)+Math.abs(arg0.y-fireStart.y)-Math.abs(arg1.x-fireStart.x)-Math.abs(arg1.y-fireStart.y);
			}
			
		});
		
		if(changed)
			fitness = -1;
		
		reset();
	}
	
	/**
	 * Changes the given coordinate to an close coordinate according to a gaussian distribution
	 * @param c
	 */
	private void gaussianWiggle(Coordinate c) {
		int xOffset = (int) (rand.nextGaussian()*5);
		int yOffset = (int) (rand.nextGaussian()*5);
		c.x = Math.max(0, Math.min(c.x+xOffset, grid.width()-1));
		c.y = Math.max(0, Math.min(c.y+yOffset, grid.heigth()-1));
	}

	@Override
	public Strategy generateOffspring(Strategy p1, Strategy p2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void copy(Strategy s) {
		ScatteredStrategy toCopy = (ScatteredStrategy) s;
		for(int i=0;i<sequence.size();i++)
			sequence.get(i).copy(toCopy.sequence.get(i));
		fitness = toCopy.fitness;
		reset();
	};

	@Override
	public Strategy clone() {
		ScatteredStrategy s = null;
		s = (ScatteredStrategy) super.clone();
		s.sequence = new ArrayList<Coordinate>(sequence.size());
		for(Coordinate c:sequence)
			s.sequence.add(c.clone());
		
		s.grid = new Grid(grid.width(),grid.heigth());
		s.reset();
		
		s.fitness = fitness;
		return s;
	}
}
