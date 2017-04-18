package grid;

public class Cell implements Cloneable {
	
	/** coordinate in the grid of the cell */
	Coordinate coord;
	/** state of the cell */
	public State state;
	/** last time the state was changed */
	public int time;
	
	public Cell() {
		this(0,0,State.Free);
	}
	
	public Cell(int x, int y) {
		this(x,y,State.Free);
	}
	
	public Cell(int x, int y, State state) {
		coord = new Coordinate(x,y);
		this.state = state;
		time = 0;
	}
	
	public void reset() {
		state = State.Free;
		time = 0;
	}
	
	@Override
	public Cell clone() {
		Cell c = null;
		try {
			c = (Cell) super.clone();
			c.coord = coord.clone();
			c.state = state;
			c.time = time;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
}
