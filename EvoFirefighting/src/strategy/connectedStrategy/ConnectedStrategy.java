package strategy.connectedStrategy;


import java.util.ArrayList;
import java.util.ListIterator;
import grid.Coordinate;
import grid.Grid;
import grid.State;
import strategy.GeneralStrategy;
import strategy.Strategy;

public class ConnectedStrategy extends GeneralStrategy {
	
	protected Coordinate start;
	protected ArrayList<Extension> sequence;
	
	protected ListIterator<Extension> it;
	protected Coordinate front;
	protected Coordinate back;
	
	public ConnectedStrategy(
			int simulationTime,
			double initialAccount,
			double budget,
			double mutationRate,
			Coordinate startOffset) {
		super(simulationTime, initialAccount, budget, mutationRate);
		
		if(startOffset == null)
			start = new Coordinate(startFire.x,startFire.y-1);
		else
			start = new Coordinate(
					Math.max(0, Math.min(startFire.x+startOffset.x, grid.width()-1)),
					Math.max(0, Math.min(startFire.y+startOffset.y, grid.heigth()-1)));
		
		int sequenceLength = (int)(this.initialAccount+this.budget*this.simulationTime);
		sequence = new ArrayList<Extension>(sequenceLength);
		for(int i=0;i<sequenceLength;i++) {
			sequence.add(new Extension(rand));
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
		
//		boolean extendFront = grid.time()%2==0;
		
		while(it.hasNext() && account >= 1.0) {
			e = it.next();
			dir = e.dir;
			if(e.extendFront) {
				end = front;
//				dir = Direction.NE;
			}
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
//			extendFront = false;
		}
		
		return success;
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
	public void reset() {
		super.reset();
		it = sequence.listIterator();
		front = start;
		back = start;
	}

	@Override
	public void mutate() {
		boolean changed = false;
		for(int i=0;i<sequence.size();i++) {
			if(rand.nextDouble()<mutationRate/sequence.size()) {
				sequence.set(i, new Extension(rand));
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
	public void copy(Strategy s) {
		ConnectedStrategy toCopy = (ConnectedStrategy) s;
		start.copy(toCopy.start);
		for(int i=0;i<sequence.size();i++)
			sequence.get(i).copy(toCopy.sequence.get(i));
		fitness = toCopy.fitness;
		reset();
	};
	
	@Override
	public ConnectedStrategy clone() {
		ConnectedStrategy s = null;
		s = (ConnectedStrategy) super.clone();
		s.start = start.clone();
		s.sequence = new ArrayList<Extension>(sequence.size());
		for(Extension e:sequence)
			s.sequence.add(e.clone());
		
		s.grid = new Grid(grid.width(),grid.heigth());
		s.reset();
		
		s.fitness = fitness;
		return s;
	}


}
