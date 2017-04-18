import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class ScatteredStrategy implements Strategy {

	private static Random rand = new Random();
	
	private static double initialAccount = 2.0;
	private static double budget = 1.8;
	private static double mutationRate = 2.5;
	private static int gridWidth = 151;
	private static int gridHeigth = 151;
	private static Coordinate startFire  = new Coordinate(gridWidth/2,gridHeigth/2);
	
	private Grid grid;
	
	private ListIterator<Coordinate> it;
	
	private ArrayList<Coordinate> sequence;
	
	private int fitness = -1;
	private boolean finished = false;
	
	public ScatteredStrategy() {
		grid = new Grid(gridWidth,gridHeigth);
		int sequenceLength = (int)(initialAccount+budget*(gridWidth+gridHeigth)); // TODO correct length
		sequence = new ArrayList<Coordinate>(sequenceLength); 
		for(int i=0;i<sequenceLength;i++) {
			sequence.add(new Coordinate(rand.nextInt(gridWidth),rand.nextInt(gridHeigth)));
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
	public boolean spreadStep() {
		if(finished()) return false;
		boolean fireStopped = !grid.spreadFire();
		boolean edgeReached = grid.fireReachedEdge();
		finished = fireStopped || edgeReached;
		return !finished;
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
		it = sequence.listIterator();
		grid.reset();
		grid.ignite(startFire.x, startFire.y);
		finished = false;
	}

	@Override
	public void mutate() {
		boolean changed = false;
		for(int i=0;i<sequence.size();i++) {
			if(rand.nextDouble()<mutationRate/sequence.size()) {
				sequence.set(i, new Coordinate(rand.nextInt(gridWidth),rand.nextInt(gridHeigth)));
				changed = true;
			}
		}
		if(changed)
			fitness = -1;
	}

	@Override
	public Strategy generateOffspring(Strategy p1, Strategy p2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Strategy clone() {
		ScatteredStrategy s = null;
		try {
			s = (ScatteredStrategy) super.clone();
			s.sequence = new ArrayList<Coordinate>(sequence.size());
			for(Coordinate c:sequence)
				s.sequence.add(c.clone());
			
			s.grid = new Grid(grid.width(),grid.heigth());
			s.reset();
			
			s.fitness = fitness;
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return s;
	}

	@Override
	public int compareTo(Strategy s) {
		if(!(s instanceof ScatteredStrategy))
			throw new ClassCastException("Tries to compare incompatible Strategies");
		return (int) Math.signum(fitness()-s.fitness());
	}

}
