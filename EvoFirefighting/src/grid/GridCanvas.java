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
	
	private Grid grid;
	
	private Timer refreshTimer;
	
	public GridCanvas() {
		loadGrid(new Grid(width,height));
		
		refreshTimer = new Timer(
				1000/REFRESH_RATE,
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						repaint();
					}
				});
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
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		synchronized(this) {
			// paint cells
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					switch (grid.state(x, y)) {
					case Burning:
						g.setColor(Color.RED);
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
		if(pixelSize>=4) {
			g.setColor(Color.lightGray);
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					g.drawRect(x*pixelSize, (height-y-1)*pixelSize, pixelSize, pixelSize);
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
		repaint();
	}
}
