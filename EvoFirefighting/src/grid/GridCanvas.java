package grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

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
		
		paintSector(g, new Coordinate(0,0), new Coordinate(grid.width()-1,grid.height()-1),cellSize);
	}
	
	/**
	 * @return An image of the current grid.
	 */
	public BufferedImage getImage() {
		Coordinate bottomLeft = new Coordinate(grid.width()-1,grid.height()-1);
		Coordinate topRight = new Coordinate(0,0);
		for(int x=0;x<grid.width();x++) {
			for(int y=0;y<grid.height();y++) {
				if(grid.state(x, y) != State.Free) {
					if(x<bottomLeft.x)
						bottomLeft.x = x;
					if(y<bottomLeft.y)
						bottomLeft.y = y;
					if(x>topRight.x)
						topRight.x = x;
					if(y>topRight.y)
						topRight.y = y;
				}
			}
		}
		BufferedImage image = new BufferedImage((topRight.x-bottomLeft.x+1)*10,(topRight.y-bottomLeft.y+1)*10,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		paintSector(g, bottomLeft, topRight, 10);
		return image;
	}
	
	/**
	 * Paints only the part of the grid between a bottom left and top right coordinate.
	 * @param g
	 */
	public void paintSector(Graphics g, Coordinate bottomLeft, Coordinate topRight, int cellSize) {
		float hue=0.0f, saturation=1.0f, brightness=1.0f, step=0.02f;
		
		int width = topRight.x-bottomLeft.x+1;
		int height = topRight.y-bottomLeft.y+1;
		
		if(grid != null) {
			synchronized(this) {
				// paint cells
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						switch (grid.state(x+bottomLeft.x, y+bottomLeft.y)) {
						case Burning:
							g.setColor(Color.getHSBColor(hue+step*(grid.time(x+bottomLeft.x, y+bottomLeft.y)%10), saturation, brightness));
							if(grid.time(x+bottomLeft.x, y+bottomLeft.y)==0)
								g.setColor(Color.CYAN);
							break;
						case Free:
							g.setColor(Color.WHITE);
							break;
						case Protected:
							g.setColor(Color.BLACK);
							break;
						}
						g.fillRect(x*cellSize, (height-y-1)*cellSize, cellSize, cellSize);
					}
				}
			}
			// paint raster
			if(cellSize>=3) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						if(grid.state(x+bottomLeft.x, y+bottomLeft.y)==State.Free) {
							g.setColor(Color.LIGHT_GRAY);
							g.drawRect(x*cellSize, (height-y-1)*cellSize, cellSize, cellSize);
						}
					}
				}
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						if(grid.state(x+bottomLeft.x, y+bottomLeft.y)!=State.Free) {
							g.setColor(Color.DARK_GRAY);
							g.drawRect(x*cellSize, (height-y-1)*cellSize, cellSize, cellSize);
						}
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
