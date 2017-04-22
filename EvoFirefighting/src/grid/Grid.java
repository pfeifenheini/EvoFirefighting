package grid;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Implements a two dimensional grid used for the fire fighting problem.
 * You can set cells on fire, protect cells, spread the fire from all burning cells to neighbors, etc..
 * @author Martin
 *
 */
public class Grid implements Cloneable {
	/** Two dimensional array of cells defines the grid. */
	private Cell[][] cells;
	
	/** Width of the grid. */
	private int width;
	/** Height of the grid. */
	private int height;
	
	/** Current time, i.e. the number of times the fire has been spread. */
	private int time = 0;
	/** Toal number of burning cells on the grid. */
	private int numberOfBurningCells = 0;
	/** Total number of protected cells on the grid. */
	private int numberOfProtectedCells = 0;
	/** Total number of burning cell per row. */
	private int[] burningCellsPerRow;
	/** First time a highway cell would catches fire. Max value of Integer, if highway is never reached. */
	private int firstHighwayCellReached = Integer.MAX_VALUE;
	/** Contains all cells of the current fire front. These are the cells that will spread the fire in the next step. */
	private Queue<Coordinate> fireFront = new LinkedList<Coordinate>();
	
	/**
	 * Constructor.
	 * @param width Width.
	 * @param height Height.
	 */
	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
		cells = new Cell[width][];
		for(int i=0;i<width;i++)
			cells[i] = new Cell[height];
		for(int x=0;x<width;x++)
			for(int y=0;y<height;y++)
				cells[x][y] = new Cell(x,y,State.Free);
		burningCellsPerRow = new int[height];
	}
	
	/**
	 * Resets the grid.
	 */
	public void reset() {
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				cells[x][y].reset();
			}
		}
		time = 0;
		numberOfBurningCells = 0;
		numberOfProtectedCells = 0;
		for(int i=0;i<burningCellsPerRow.length;i++)
			burningCellsPerRow[i] = 0;
		firstHighwayCellReached = Integer.MAX_VALUE;
		fireFront.clear();
	}
	
	/**
	 * Sets a cell on fire if it is not protected. Invalid coordinates are ignored.
	 * @param x coordinate
	 * @param y coordinate
	 * @return true if a free cell has been set on fire, false if the cell is protected or already burning
	 */
	public boolean ignite(int x, int y) {
		if(x>=0 && x<width && y>=0 && y<height) {
			if(cells[x][y].state == State.Free) {
				cells[x][y].state = State.Burning;
				cells[x][y].time = time;
				fireFront.add(new Coordinate(x,y));
				numberOfBurningCells++;
				burningCellsPerRow[y]++;
				return true;
			}
			if(cells[x][y].state==State.Highway && firstHighwayCellReached==Integer.MAX_VALUE)
				firstHighwayCellReached = time;
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
		if(x>=0 && x<width && y>=0 && y<height) {
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

	/**
	 * Is called if a method tries to access a cell that lies otside of the grid.
	 * @param x X coordinate of the accessed cell.
	 * @param y Y coordinate of the accessed cell.
	 * @param e Catched Exception.
	 */
	private void accessError(int x, int y, Exception e) {
		System.out.println("Cell (" + x + "," + y + ") can not be accessed");
		e.printStackTrace();
		System.exit(1);
	}

	/**
	 * 
	 * @return Width of the grid.
	 */
	public int width() {
		return width;
	}

	/**
	 * 
	 * @return Height of the grid.
	 */
	public int height() {
		return height;
	}
	
	/**
	 * 
	 * @param row Row whose number of burning cells shall be returned.
	 * @return Number of burning cells in the specified row.
	 */
	public int burningCells(int row) {
		return burningCellsPerRow[row];
	}
	
	/**
	 * 
	 * @return Total number of burning cells in the grid.
	 */
	public int burningCells() {
		return numberOfBurningCells;
	}
	
	/**
	 * 
	 * @return Total number of protected cells in the grid.
	 */
	public int protectedCells() {
		return numberOfProtectedCells;
	}
	
	/**
	 * 
	 * @return Size of the fire front. It 0, the fire can no longer spread.
	 */
	public int fireFrontSize() {
		return fireFront.size();
	}
	
	/**
	 * 
	 * @return Time needed for the fire to reach the bottom. Max integer value, if the bottom was never reached.
	 */
	public int timeHighwayReached() {
		return firstHighwayCellReached;
	}
	
	/**
	 * 
	 * @return Current time passed, i.e. the number of spreadFire() calls that happen since the last reset.
	 */
	public int time() {
		return time;
	}
	
	/**
	 * @param x X Coordinate.
	 * @param y Y Coordinate.
	 * @return The time at which the status of cell (x,y) has been set.
	 */
	public int time(int x, int y) {
		try {
			return cells[x][y].time;
		} catch (Exception e) {
			accessError(x, y, e);
		}
		return 0;
	}
	
	/**
	 * 
	 * @param x X Coordinate.
	 * @param y Y Coordinate.
	 * @return State of cell (x,y).
	 */
	public State state(int x, int y) {
		try {
			return cells[x][y].state;
		} catch (Exception e) {
			accessError(x, y, e);
		}
		return null;
	}

	@Override
	public Grid clone() {
		Grid g = null;
		try {
			g = (Grid) super.clone();
			g.width = width;
			g.height = height;
			g.cells = cells.clone();
			
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					g.cells[x][y] = cells[x][y].clone();
				}
			}
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return g;
	}

	/**
	 * Defines the bottom cells as highway cells.
	 */
	public void setupHighway() {
		for(int x=0;x<width;x++)
			cells[x][0].state = State.Highway;
	}
}
