package grid;


import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Queue;

public class Grid implements Cloneable {
	
	private Cell[][] cells;
	
	private int width;
	private int heigth;
	
	private int time = 0;
	private int numberOfBurningCells = 0;
	private int numberOfProtectedCells = 0;
	private int[] burningCellsPerRow;
	private int timeToReachBottom = -1;
	private boolean fireReachedEdge = false;
	
	Queue<Coordinate> fireFront = new LinkedList<Coordinate>();
	
	public Grid(int width, int heigth) {
		this.width = width;
		this.heigth = heigth;
		cells = new Cell[width][];
		for(int i=0;i<width;i++)
			cells[i] = new Cell[heigth];
		for(int x=0;x<width;x++)
			for(int y=0;y<heigth;y++)
				cells[x][y] = new Cell(x,y);
		burningCellsPerRow = new int[heigth];
	}
	
	public State state(int x, int y) {
		try {
			return cells[x][y].state;
		} catch (Exception e) {
			accessError(x, y, e);
		}
		return null;
	}
	
	public void reset() {
		for(int x=0;x<width;x++) {
			for(int y=0;y<heigth;y++) {
				cells[x][y].reset();
			}
		}
		time = 0;
		numberOfBurningCells = 0;
		numberOfProtectedCells = 0;
		for(int i=0;i<burningCellsPerRow.length;i++)
			burningCellsPerRow[i] = 0;
		fireReachedEdge = false;
		timeToReachBottom = -1;
		fireFront.clear();
	}
	
	/**
	 * Sets a cell on fire if it is not protected. Invalid coordinates are ignored.
	 * @param x coordinate
	 * @param y coordinate
	 * @return true if a free cell has been set on fire, false if the cell is protected or already burning
	 */
	public boolean ignite(int x, int y) {
		if(x>=0 && x<width && y>=0 && y<heigth) {
			if(cells[x][y].state == State.Free) {
				cells[x][y].state = State.Burning;
				fireFront.add(new Coordinate(x,y));
				numberOfBurningCells++;
				if(x==0 || y==0 || x==width-1 || y==heigth-1)
					fireReachedEdge = true;
				burningCellsPerRow[y]++;
				if(y==0 && timeToReachBottom==-1)
					timeToReachBottom = time;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Protects a free cell if it is not burning. Invalid coordinates are ignored.
	 * @param x coordinate
	 * @param y coordinate
	 * @return true if a free cell has been protected, false if the cell is burning or already protected
	 */
	public boolean protect(int x, int y) {
		if(x>=0 && x<width && y>=0 && y<heigth) {
			if(cells[x][y].state == State.Free) {
				cells[x][y].state = State.Protected;
				numberOfProtectedCells++;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Spreads the fire on the grid one steps further. If this method return false, the fire can no longer spread.
	 * @return false if the fire can no longer spread, true otherwise.
	 */
	public boolean spreadFire() {
		int currentFrontSize = fireFront.size();
		Coordinate c;
		time++;
		for(int i=0;i<currentFrontSize;i++) {
			c = fireFront.remove();
			ignite(c.x+1,c.y);
			ignite(c.x-1,c.y);
			ignite(c.x,c.y+1);
			ignite(c.x,c.y-1);
		}
		return !fireFront.isEmpty();
	}
	
	public boolean fireReachedEdge() {
		return fireReachedEdge;
	}

	private void accessError(int x, int y, Exception e) {
		System.out.println("Cell (" + x + "," + y + ") can not be accessed");
		e.printStackTrace();
		System.exit(1);
	}

	public int width() {
		return width;
	}

	public int heigth() {
		return heigth;
	}
	
	public int burningCells(int row) {
		return burningCellsPerRow[row];
	}
	
	public int burningCells() {
		return numberOfBurningCells;
	}
	
	public int protectedCells() {
		return numberOfProtectedCells;
	}
	
	public int fireFrontSize() {
		return fireFront.size();
	}
	
	public int timeToReachBottom() {
		return timeToReachBottom;
	}
	
	public int time() {
		return time;
	}
	
	@Override
	public Grid clone() {
		Grid g = null;
		try {
			g = (Grid) super.clone();
			g.width = width;
			g.heigth = heigth;
			g.cells = cells.clone();
			
			for(int x=0;x<width;x++) {
				for(int y=0;y<heigth;y++) {
					g.cells[x][y] = cells[x][y].clone();
				}
			}
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return g;
	}
}
