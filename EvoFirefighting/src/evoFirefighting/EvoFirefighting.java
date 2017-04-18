package evoFirefighting;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import grid.GridCanvas;
import strategy.Strategy;
import strategy.connectedStrategy.ConnectedStrategy;
import strategy.connectedStrategy.Direction;

public class EvoFirefighting extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JPanel centerPane;
	private JPanel topPane;
	private JPanel bottomPane;
	
	private GridCanvas canvas;

	private JButton zoomIn =
			new JButton("+");
	private JButton zoomOut =
			new JButton("-");
	
	private final String[] strategyChoices = {"connected", "scattered"}; 
	private JComboBox<String> strategyChoice =
			new JComboBox<String>(strategyChoices);
	
	private JButton decreaseAnimationSpeed =
			new JButton("<<");
	private JButton animate =
			new JButton("animate");
	private JButton increaseAnimationSpeed =
			new JButton(">>");

	
	private JButton startEvolution =
			new JButton("start evolution");
	private JButton decreaseOffset = 
			new JButton("-");
	private int offset = 0;
	private JLabel offsetLabel =
			new JLabel("1");
	private JButton increaseOffset = 
			new JButton("+");
	
	
	private static final int DEFAULT_ANIMATION_DELAY = 500;
	private int animationTime = 0;
	private Timer animationTimer = new Timer(
			DEFAULT_ANIMATION_DELAY,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					animationStep();
				}
			});
	private Strategy strategyToAnimate = null;
	
	private Evolution evolution = null;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				EvoFirefighting frame = new EvoFirefighting();
				frame.setVisible(true);
			}
		});
	}
	
	Strategy best = new ConnectedStrategy();
	Strategy clone = null;
	
	public EvoFirefighting() {
		this.setTitle("Evolutionary Firefighting");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		centerPane = new JPanel();
		canvas = new GridCanvas();
		centerPane.add(canvas);
		contentPane.add(centerPane,BorderLayout.CENTER);
		
		topPane = new JPanel();
		topPane.add(new JLabel("Zoom: "));
		topPane.add(zoomOut);
		topPane.add(zoomIn);
		contentPane.add(topPane,BorderLayout.PAGE_START);
		
		bottomPane = new JPanel();
		bottomPane.add(strategyChoice);
		bottomPane.add(decreaseAnimationSpeed);
		bottomPane.add(animate);
		bottomPane.add(increaseAnimationSpeed);
		bottomPane.add(startEvolution);
		bottomPane.add(decreaseOffset);
		bottomPane.add(offsetLabel);
		bottomPane.add(increaseOffset);
		contentPane.add(bottomPane,BorderLayout.PAGE_END);
		
		zoomIn.addActionListener(this);
		zoomOut.addActionListener(this);
		strategyChoice.addActionListener(this);
		decreaseAnimationSpeed.addActionListener(this);
		animate.addActionListener(this);
		increaseAnimationSpeed.addActionListener(this);
		startEvolution.addActionListener(this);
		decreaseOffset.addActionListener(this);
		increaseOffset.addActionListener(this);
		
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == zoomIn) {
			canvas.setPixelSize(canvas.getPixelSize()+1);
			zoomOut.setEnabled(true);
			pack();
		}
		if(e.getSource() == zoomOut) {
			if(canvas.getPixelSize()>1) {
				canvas.setPixelSize(canvas.getPixelSize()-1);
				if(canvas.getPixelSize() == 1)
					zoomOut.setEnabled(false);
				pack();
			}
		}
		if(e.getSource() == decreaseAnimationSpeed) {
			animationTimer.setDelay((int)(animationTimer.getDelay()*2.0));
			animationTimer.setInitialDelay(0);
			animationTimer.restart();
		}
		if(e.getSource() == animate) {
			animate.setEnabled(false);
			startEvolution.setEnabled(false);
			resetAnimation();
			startAnimation();
		}
		if(e.getSource() == increaseAnimationSpeed) {
			animationTimer.setDelay((int)(animationTimer.getDelay()/2.0));
			animationTimer.setInitialDelay(0);
			animationTimer.restart();
		}
		if(e.getSource() == startEvolution) {
			if(evolution != null) {
				evolution.stopEvolution();
			}
			evolution = new Evolution(50,(String)strategyChoice.getSelectedItem());
			evolution.startEvolution();
			
			strategyToAnimate = null;
			
			Timer t = new Timer(
					500,
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if(!isAnimating()) {
								if(strategyToAnimate == null)
									strategyToAnimate = evolution.cloneBestStrategy(offset);
								else
									evolution.copyBestStrategy(strategyToAnimate, offset);
								while(strategyToAnimate.step());
								canvas.loadGrid(strategyToAnimate.cloneGrid());
								System.out.println("Generation: " + evolution.generation());
							}
						}
					});
			
			t.start();
			
			startEvolution.setText("restart evolution");
		}
		if(e.getSource() == decreaseOffset) {
			if((e.getModifiers() & ActionEvent.ALT_MASK) != 0)
				offset = 0;
			else if((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0)
				offset = Math.max(0, offset-10);
			else
				offset = Math.max(0, offset-1);
			offsetLabel.setText(Integer.toString(offset+1));
		}
		if(e.getSource() == increaseOffset) {
			if((e.getModifiers() & ActionEvent.ALT_MASK) != 0)
				offset = evolution.populationSize()-1;
			else if((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0)
				offset = Math.min(offset+10, evolution.populationSize()-1);
			else
				offset = Math.min(offset+1, evolution.populationSize()-1);
			
			offsetLabel.setText(Integer.toString(offset+1));
		}
		
		repaint();
	}
	
	public boolean isAnimating() {
		return animationTimer.isRunning();
	}
	
	public void startAnimation() {
		animationTimer.start();
	}
	
	public void resetAnimation() {
		animationTimer.stop();
		while(animationTimer.isRunning()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		animationTimer.setDelay(DEFAULT_ANIMATION_DELAY);
		animationTimer.setInitialDelay(DEFAULT_ANIMATION_DELAY);
		strategyToAnimate.reset();
		animationTime = 0;
	}
	
	public void animationStep() {
		if(!strategyToAnimate.finished()) {
			if(animationTime%2 == 0) {
				if(!strategyToAnimate.protectStep())
					strategyToAnimate.spreadStep();
			}
			else
				strategyToAnimate.spreadStep();
			animationTime++;
			canvas.loadGrid(strategyToAnimate.cloneGrid());
		}
		else {
			stopAnimation();
		}
	}
	
	public void stopAnimation() {
		animationTimer.stop();
		animate.setEnabled(true);
		startEvolution.setEnabled(true);
	}

}
