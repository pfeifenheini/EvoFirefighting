package evoFirefighting;

import java.awt.event.MouseAdapter;

import grid.GridCanvas;

public class GridMouseAdapter extends MouseAdapter {
	
	private GridCanvas canvas;
	
	public GridMouseAdapter(GridCanvas canvas) {
		super();
		this.canvas = canvas;
	}

}
