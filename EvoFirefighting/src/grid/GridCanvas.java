package grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import evoFirefighting.GridMouseAdapter;

/**
 * Panel that draws a Grid.
 * @author Martin
 *
 */
public class GridCanvas extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public static final int REFRESH_RATE = 30;
	public static final int DEFAULT_CELL_SIZE = 5;
	public static final int DEFAULT_WIDTH = 101;
	public static final int DEFAULT_HEIGHT = 101;

	/** Size of a single cell in the grid. */
	private int cellSize = DEFAULT_CELL_SIZE;
	
	/** The grid that is painted. */
	private Grid grid = null;
	
	/** Continues repainting this component. */
	private Timer refreshTimer= new Timer(
			1000/REFRESH_RATE,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					repaint();
				}
			});
	
	/**
	 * Constructor.
	 */
	public GridCanvas() {
		refreshTimer.start();
		
		setBackground(Color.WHITE);
		
		GridMouseAdapter a = new GridMouseAdapter(this);
		addMouseListener(a);
		addMouseMotionListener(a);
	}
	
	/**
	 * Loads a grid. It will be painted during the next refresh cycle.
	 * @param grid Grid to paint.
	 */
	public void loadGrid(Grid grid) {
		synchronized(this) {
			this.grid = grid;
			setPreferredSize(new Dimension(cellSize*grid.width(),cellSize*grid.height()));
		}
	}
	
	/**
	 * Returns the currently loaded grid.
	 * @return Current grid.
	 */
	public Grid getGrid() {
		return grid;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		float hue=0.0f, saturation=1.0f, brightness=1.0f, step=0.02f;
		
		if(grid != null) {
			synchronized(this) {
				// paint cells
				for(int x=0;x<grid.width();x++) {
					for(int y=0;y<grid.height();y++) {
						switch (grid.state(x, y)) {
						case Burning:
							g.setColor(Color.getHSBColor(hue+step*(grid.time(x, y)%10), saturation, brightness));
							if(grid.time(x, y)==0)
								g.setColor(Color.CYAN);
							break;
						case Free:
							g.setColor(Color.WHITE);
							break;
						case Protected:
							g.setColor(Color.BLACK);
							break;
						}
						g.fillRect(x*cellSize, (grid.height()-y-1)*cellSize, cellSize, cellSize);
					}
				}
			}
			// paint raster
			if(cellSize>=3) {
				for(int x=0;x<grid.width();x++) {
					for(int y=0;y<grid.height();y++) {
						if(grid.state(x, y)==State.Free)
							g.setColor(Color.LIGHT_GRAY);
						else
							g.setColor(Color.DARK_GRAY);
						g.drawRect(x*cellSize, (grid.height()-y-1)*cellSize, cellSize, cellSize);
					}
				}
			}
		}
	}

	/**
	 * @return The current pixel size of a cell.
	 */
	public int getCellSize() {
		return cellSize;
	}

	/**
	 * Sets the pixel size of a cell.
	 * @param cellSize New size.
	 */
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
		setPreferredSize(new Dimension(cellSize*grid.width(),cellSize*grid.height()));
	}
}
