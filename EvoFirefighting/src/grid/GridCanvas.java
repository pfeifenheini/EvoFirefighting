package grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import evoFirefighting.GridMouseAdapter;

public class GridCanvas extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public static final int REFRESH_RATE = 30;
	public static final int DEFAULT_PIXEL_SIZE = 5;
	public static final int DEFAULT_WIDTH = 101;
	public static final int DEFAULT_HEIGTH = 101;
	public static final int DEFAULT_ANIMATION_DELAY = 500;
	
	private int pixelSize = DEFAULT_PIXEL_SIZE;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGTH;
	
	private Grid grid = null;
	
	private Timer refreshTimer= new Timer(
			1000/REFRESH_RATE,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					repaint();
				}
			});
	
	public GridCanvas() {
//		loadGrid(new Grid(width,height));
		
		refreshTimer.start();
		
		setBackground(Color.WHITE);
		
		GridMouseAdapter a = new GridMouseAdapter(this);
		addMouseListener(a);
		addMouseMotionListener(a);
	}
	
	public void loadGrid(Grid grid) {
		synchronized(this) {
			this.grid = grid;
			width = grid.width();
			height = grid.heigth();
			setPreferredSize(new Dimension(pixelSize*width,pixelSize*height));
		}
	}
	
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
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
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
						g.fillRect(x*pixelSize, (height-y-1)*pixelSize, pixelSize, pixelSize);
					}
				}
			}
			// paint raster
			if(pixelSize>=3) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						if(grid.state(x, y)==State.Free)
							g.setColor(Color.LIGHT_GRAY);
						else
							g.setColor(Color.DARK_GRAY);
						g.drawRect(x*pixelSize, (height-y-1)*pixelSize, pixelSize, pixelSize);
					}
				}
			}
		}
	}
	

	public int getPixelSize() {
		return pixelSize;
	}

	public void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
		setPreferredSize(new Dimension(pixelSize*width,pixelSize*height));
//		repaint();
	}
}
