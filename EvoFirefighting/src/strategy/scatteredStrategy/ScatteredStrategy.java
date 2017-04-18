package strategy.scatteredStrategy;


import java.util.ArrayList;
import java.util.ListIterator;
import grid.Coordinate;
import grid.Grid;
import strategy.GeneralStrategy;
import strategy.Strategy;
import strategy.connectedStrategy.ConnectedStrategy;

public class ScatteredStrategy extends GeneralStrategy {
	
	private ListIterator<Coordinate> it;
	
	private ArrayList<Coordinate> sequence;
	
	public ScatteredStrategy() {
		grid = new Grid(gridWidth,gridHeigth);
		int sequenceLength = (int)(initialAccount+budget*(gridWidth+gridHeigth));
		sequence = new ArrayList<Coordinate>(sequenceLength);
		int x, y;
		for(int i=0;i<sequenceLength;i++) {
			x = (int)(rand.nextGaussian()*gridWidth/10.0+startFire.x);
			y = (int)(rand.nextGaussian()*gridHeigth/10.0+startFire.y);
			x = Math.max(0, Math.min(x, gridWidth-1));
			y = Math.max(0, Math.min(y, gridHeigth-1));
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
//				sequence.set(i, new Coordinate(rand.nextInt(gridWidth),rand.nextInt(gridHeigth)));
				gaussianWiggle(sequence.get(i));
				changed = true;
			}
		}
		if(changed)
			fitness = -1;
	}
	
	/**
	 * Changes the given coordinate to an close coordinate according to a gaussian distribution
	 * @param c
	 */
	private void gaussianWiggle(Coordinate c) {
		int xOffset = (int) (rand.nextGaussian()*5);
		int yOffset = (int) (rand.nextGaussian()*5);
		c.x = Math.max(0, Math.min(c.x+xOffset, gridWidth-1));
		c.y = Math.max(0, Math.min(c.y+yOffset, gridHeigth-1));
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
