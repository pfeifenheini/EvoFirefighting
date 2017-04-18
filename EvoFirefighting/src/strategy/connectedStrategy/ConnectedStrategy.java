package strategy.connectedStrategy;


import java.util.ArrayList;
import java.util.ListIterator;
import grid.Coordinate;
import grid.Grid;
import grid.State;
import strategy.GeneralStrategy;
import strategy.Strategy;

public class ConnectedStrategy extends GeneralStrategy {
	
	private Coordinate start;
	private ArrayList<Extension> sequence;
	
	private ListIterator<Extension> it;
	private Coordinate front;
	private Coordinate back;
	
	public ConnectedStrategy() {
		grid = new Grid(gridWidth,gridHeigth);
		start = new Coordinate(startFire.x,startFire.y-1);
		int sequenceLength = (int)(initialAccount+budget*(gridWidth+gridHeigth));
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
