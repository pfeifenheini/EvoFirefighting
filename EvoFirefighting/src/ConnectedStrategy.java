import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class ConnectedStrategy implements Strategy {

	private class Extension implements Cloneable {
		Direction dir;
		boolean extendFront;
		
		public Extension() {
			dir = Direction.values()[rand.nextInt(8)];
			extendFront = rand.nextBoolean();
		}
		
		public Extension(Direction dir, boolean extendFront) {
			this.dir = dir;
			this.extendFront = extendFront;
		}
		
		@Override
		public Extension clone() {
			Extension e = null;
			try {
				e = (Extension) super.clone();
				e.dir = dir;
				e.extendFront = extendFront;
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return e;
		}
	}
	
	private static Random rand = new Random();
	
	private static double initialAccount = 2.0;
	private static double budget = 1.8;
	private static double mutationRate = 2.5;
	private static int gridWidth = 151;
	private static int gridHeigth = 151;
	private static Coordinate startFire  = new Coordinate(gridWidth/2,gridHeigth/2);
	
	private Grid grid;
	
	private ListIterator<Extension> it;
	private Coordinate front;
	private Coordinate back;
	
	private Coordinate start;
	private ArrayList<Extension> sequence;
	
	private int fitness = -1;
	private boolean finished = false;
	
	public ConnectedStrategy() {
		grid = new Grid(gridWidth,gridHeigth);
		start = new Coordinate(startFire.x,startFire.y-1);
		int sequenceLength = (int)(initialAccount+budget*(gridWidth+gridHeigth)); // TODO correct length
		sequence = new ArrayList<Extension>(sequenceLength); 
		for(int i=0;i<sequenceLength;i++) {
			sequence.add(new Extension());
		}
		reset();
	}

	@Override
	public boolean protectStep() {
		if(finished()) return false;
		Extension e;
		Direction dir;
		Coordinate end, neighbor;
		boolean success = false;
		double account = initialAccount + grid.time()*budget - grid.protectedCells();
		
		if(grid.state(start.x, start.y) == State.Free && account >= 1.0) {
			grid.protect(start.x, start.y);
			account -= 1.0;
			success = true;
		}
		while(it.hasNext() && account >= 1.0) {
			e = it.next();
			dir = e.dir;
			if(e.extendFront)
				end = front;
			else
				end = back;
			
			for(int i=0;i<8;i++) {
				neighbor = getNeighbor(end, dir);
				if(grid.protect(neighbor.x, neighbor.y)) {
					account -= 1.0;
					success = true;
					if(e.extendFront)
						front = neighbor;
					else
						back = neighbor;
					break;
				}
				if(e.extendFront)
					dir = dir.nextCW();
				else
					dir = dir.nextCCW();
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

	private Coordinate getNeighbor(Coordinate c, Direction dir) {
		int x = c.x;
		int y = c.y;
		switch (dir) {
		case E:
			x++;
			break;
		case N:
			y++;
			break;
		case NE:
			x++;
			y++;
			break;
		case NW:
			x--;
			y++;
			break;
		case S:
			y--;
			break;
		case SE:
			y--;
			x++;
			break;
		case SW:
			y--;
			x--;
			break;
		case W:
			x--;
			break;
		}
		return new Coordinate(x,y);
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
	public void reset() {
		it = sequence.listIterator();
		front = start;
		back = start;
		grid.reset();
		grid.ignite(startFire.x, startFire.y);
		finished = false;
	}
	
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
	public void mutate() {
		boolean changed = false;
		for(int i=0;i<sequence.size();i++) {
			if(rand.nextDouble()<mutationRate/sequence.size()) {
				sequence.set(i, new Extension());
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
	public Grid cloneGrid() {
		return grid.clone();
	}

	@Override
	public boolean finished() {
		return finished;
	}
	
	@Override
	public ConnectedStrategy clone() {
		ConnectedStrategy s = null;
		try {
			s = (ConnectedStrategy) super.clone();
			s.start = start.clone();
			s.sequence = new ArrayList<Extension>(sequence.size());
			for(Extension e:sequence)
				s.sequence.add(e.clone());
			
			s.grid = new Grid(grid.width(),grid.heigth());
			s.reset();
			
			s.fitness = fitness;
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	@Override
	public int compareTo(Strategy s) {
		if(!(s instanceof ConnectedStrategy))
			throw new ClassCastException("Tries to compare incompatible Strategies");
		return (int) Math.signum(fitness()-s.fitness());
	}
}
