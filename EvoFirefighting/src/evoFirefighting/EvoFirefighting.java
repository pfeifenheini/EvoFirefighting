package evoFirefighting;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import grid.Grid;
import grid.GridCanvas;
import strategy.Strategy;
import strategy.connectedStrategy.ConnectedProtectionStrategy;

public class EvoFirefighting extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	/** For the grid. */
	private JPanel centerPane;
	/** For settings and zoom buttons. */
	private JPanel topPane;
	/** For Buttons. */
	private JPanel bottomPane;
	/** For status information. */
	private JPanel infoPane;
	
	/** The canvas painting the grid. */
	private GridCanvas canvas;

	// --------------------- \\
	// --- BUTTONS START --- \\
	// --------------------- \\
	
	private JButton zoomIn =
			new JButton("+");
	private JButton zoomOut =
			new JButton("-");
	
	private JButton settings = 
			new JButton("settings");
	 
	// Don't know how to fix this warning
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JComboBox<String> strategyChoice =
			new JComboBox<String>(new DefaultComboBoxModel(Mode.values()));
	
	private JButton decreaseAnimationSpeed =
			new JButton("<<");
	private JButton animate =
			new JButton("animate");
	private JButton increaseAnimationSpeed =
			new JButton(">>");
	
	private JButton startEvolution =
			new JButton("(re)start evolution");
	private JButton decreaseOffset = 
			new JButton("-");
	private int offset = 0;
	private JLabel offsetLabel =
			new JLabel("1");
	private JButton increaseOffset = 
			new JButton("+");
	
	// ------------------- \\
	// --- BUTTONS END --- \\
	// ------------------- \\
	
	/** Delay between animation steps */
	private static final int DEFAULT_ANIMATION_DELAY = 500;
	/** Used to alter animation between protection and spreading */
	private int animationTime = 0;
	/** Contains the strategy that will be animated. */
	private Strategy strategyToAnimate = null;
	/** Animation timer */
	private Timer animationTimer = new Timer(
			DEFAULT_ANIMATION_DELAY,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					animationStep();
				}
			});
	
	/** Controls how often a strategy is loaded while the evolution is running. */
	private Timer refreshTimer = new Timer(
			DEFAULT_ANIMATION_DELAY,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!isAnimating()) {
						if(strategyToAnimate == null)
							strategyToAnimate = evolution.cloneBestStrategy(offset);
						else
							evolution.copyBestStrategy(strategyToAnimate, offset);
						while(strategyToAnimate.step());
						loadGrid(strategyToAnimate.cloneGrid());
					}
				}
			});
	
	/** Evolution algorithm currently running */
	private Evolution evolution = null;
	
	/** Used for the parameters of an evolution */
	private Properties parameters = new Properties();
	
	/**
	 * Main function.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				EvoFirefighting frame = new EvoFirefighting();
				frame.setVisible(true);
			}
		});
	}
	
	/**
	 * Constructor. Creates a new window end sets everything up.
	 */
	public EvoFirefighting() {
		
		Parameter[] parameterList = Parameter.values();
		for(int i=0;i<parameterList.length;i++)
			parameters.setProperty(parameterList[i].name(),parameterList[i].getDefaultValueString());
		
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
		
		infoPane = new JPanel();
		infoPane.setLayout(new BoxLayout(infoPane, BoxLayout.Y_AXIS));
		contentPane.add(infoPane,BorderLayout.LINE_END);
		
		topPane = new JPanel();
		topPane.add(new JLabel("Zoom: "));
		topPane.add(zoomOut);
		topPane.add(zoomIn);
		topPane.add(settings);
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
		settings.addActionListener(this);
		strategyChoice.addActionListener(this);
		decreaseAnimationSpeed.addActionListener(this);
		animate.addActionListener(this);
		increaseAnimationSpeed.addActionListener(this);
		startEvolution.addActionListener(this);
		decreaseOffset.addActionListener(this);
		increaseOffset.addActionListener(this);
		
		setTooltips();
		decreaseAnimationSpeed.setEnabled(false);
		increaseAnimationSpeed.setEnabled(false);
		animate.setEnabled(false);
		decreaseOffset.setEnabled(false);
		increaseOffset.setEnabled(false);
		
		loadGrid(new Grid(GridCanvas.DEFAULT_WIDTH,GridCanvas.DEFAULT_HEIGHT));
		pack();
	}
	
	/**
	 * Sets the tool tips for all the buttons.
	 */
	private void setTooltips() {
		zoomIn.setToolTipText("increase the cell size");
		zoomOut.setToolTipText("decrease the cell size");
		settings.setToolTipText("change parameters");
		decreaseAnimationSpeed.setToolTipText("decrease animation speed");
		increaseAnimationSpeed.setToolTipText("increase animation speed");
		animate.setToolTipText("start animation");
		startEvolution.setToolTipText("reinitializes and restarts the evolution with current settings");
		decreaseOffset.setToolTipText("skip through population");
		increaseOffset.setToolTipText("skip through population");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == zoomIn) {
			canvas.setCellSize(canvas.getCellSize()+1);
			zoomOut.setEnabled(true);
			pack();
		}
		if(e.getSource() == zoomOut) {
			if(canvas.getCellSize()>1) {
				canvas.setCellSize(canvas.getCellSize()-1);
				if(canvas.getCellSize() == 1)
					zoomOut.setEnabled(false);
				pack();
			}
		}
		if(e.getSource() == settings) {
			
			String key;
			try {
				key = ((Parameter)JOptionPane.showInputDialog(
						this,
						"please type the key you want to change",
						"key",
						JOptionPane.PLAIN_MESSAGE,
						null,
						Parameter.values(),
						"key")).name();
			} catch (NullPointerException ex) {
				key = null;
			}
			if(key!=null) {
				String value = parameters.getProperty(key);
				try {
					value = (String)JOptionPane.showInputDialog(
							this,
							"Please type the new value for \"" + key + "\"",
							"value",
							JOptionPane.PLAIN_MESSAGE,
							null,
							null,
							value);
				} catch(NullPointerException ex) {
					value = null;
				}
				
				if(value!=null)
					parameters.setProperty(key, value);
			}
		}
		if(e.getSource() == decreaseAnimationSpeed) {
			animationTimer.setDelay((int)(animationTimer.getDelay()*2.0));
			animationTimer.setInitialDelay((int)(animationTimer.getDelay()*2.0));
			animationTimer.restart();
		}
		if(e.getSource() == animate) {
			resetAnimation();
			startAnimation();
		}
		if(e.getSource() == increaseAnimationSpeed) {
			animationTimer.setDelay((int)(animationTimer.getDelay()/2.0));
			animationTimer.setInitialDelay(0);
			animationTimer.restart();
		}
		if(e.getSource() == startEvolution) {
			refreshTimer.stop();
			
			if(evolution != null) {
				evolution.stopEvolution();
			}
			
			evolution = new Evolution(Mode.values()[strategyChoice.getSelectedIndex()], parameters);
			evolution.startEvolution();
			
			strategyToAnimate = null;
			
			refreshTimer.start();
			animate.setEnabled(true);
			decreaseOffset.setEnabled(true);
			increaseOffset.setEnabled(true);
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
	
	/**
	 * Updates the info panel.
	 */
	private void updateInfo() {
		infoPane.removeAll();
		
		updateParametersInfo();
		updateEvolutionInfo();
		updateStrategyInfo();
		updateGridInfo(canvas.getGrid());

		infoPane.validate();
		infoPane.repaint();
	}
	
	/**
	 * Update the information about the parameters.
	 */
	private void updateParametersInfo() {
		addInfoText("--- Parameters ---");
		addInfoText(Parameter.initialAccount.name() + ": " + parameters.getProperty(Parameter.initialAccount.name()).replaceAll(",", "."));
		addInfoText(Parameter.budget.name() + ": " + parameters.getProperty(Parameter.budget.name()).replaceAll(",", "."));
		addInfoText(Parameter.mutationRate.name() + ": " + parameters.getProperty(Parameter.mutationRate.name()).replaceAll(",", "."));
		if(strategyToAnimate instanceof ConnectedProtectionStrategy)
			addInfoText(Parameter.startOffset.name() + ": " + parameters.getProperty(Parameter.startOffset.name()));
		if(strategyToAnimate instanceof ConnectedProtectionStrategy)
			addInfoText(Parameter.highwayDistance.name() + ": " + parameters.getProperty(Parameter.highwayDistance.name()));
	}

	/**
	 * Updates information about the evolution.
	 */
	private void updateEvolutionInfo() {
		if(evolution==null) return;
		addInfoText("--- Evolution ---");
		addInfoText("generation: " + evolution.generation());
		addInfoText(Parameter.populationSize.name() + ": " + evolution.populationSize());
		addInfoText(Parameter.simulationTime.name() + ": " + evolution.simulationTime());
	}
	
	/**
	 * Updates the information about the Strategy.
	 */
	private void updateStrategyInfo() {
		if(strategyToAnimate==null) return;
		addInfoText("--- Strategy ---");
		if(evolution!=null)
			addInfoText("individual: " + (offset+1) + "/" + evolution.populationSize());
		addInfoText("fitness: " + strategyToAnimate.fitness());
	}
	
	/**
	 * Updates the information about the grid.
	 * @param g
	 */
	private void updateGridInfo(Grid g) {
		if(g != null) {
			addInfoText("--- Grid ---");
			addInfoText("width: " + g.width());
			addInfoText("height: " + g.height());
			addInfoText("burning: " + g.burningCells());
			addInfoText("protected: " + g.protectedCells());
			if(evolution!=null)
				addInfoText("time: " + g.time() + "/" + evolution.simulationTime());
			else
				addInfoText("time: " + g.time());
			if(g.timeBottomReached() == Integer.MAX_VALUE)
				addInfoText("BottomReached at: inf");
			else
				addInfoText("BottomReached at: " + g.timeBottomReached());
		}
	}
	
	/**
	 * Adds a line to the info panel.
	 * @param s Text line to be added.
	 */
	private void addInfoText(String s) {
		JLabel label = new JLabel(s);
		infoPane.add(label);
	}
	
	/**
	 * 
	 * @return True is an animation is running.
	 */
	public boolean isAnimating() {
		return animationTimer.isRunning();
	}
	
	/**
	 * Start the animation.
	 */
	public void startAnimation() {
		animate.setEnabled(false);
		startEvolution.setEnabled(false);
		decreaseOffset.setEnabled(false);
		increaseOffset.setEnabled(false);
		strategyChoice.setEnabled(false);
		decreaseAnimationSpeed.setEnabled(true);
		increaseAnimationSpeed.setEnabled(true);
		refreshTimer.stop();
		animationTimer.start();
	}
	
	/**
	 * Stops the animation.
	 */
	public void stopAnimation() {
		animationTimer.stop();
		refreshTimer.start();
		animate.setEnabled(true);
		startEvolution.setEnabled(true);
		decreaseOffset.setEnabled(true);
		increaseOffset.setEnabled(true);
		strategyChoice.setEnabled(true);
		decreaseAnimationSpeed.setEnabled(false);
		increaseAnimationSpeed.setEnabled(false);
	}

	/**
	 * Resets the animation.
	 */
	public void resetAnimation() {
		animationTimer.stop();
		while(animationTimer.isRunning()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		animationTimer.setDelay(DEFAULT_ANIMATION_DELAY);
		animationTimer.setInitialDelay(DEFAULT_ANIMATION_DELAY);
		strategyToAnimate.reset();
		animationTime = 0;
	}
	
	/**
	 * Performs a single step of the animation.
	 */
	public void animationStep() {
		if(!strategyToAnimate.finished()) {
			if(animationTime%2 == 0) {
				if(!strategyToAnimate.protectStep())
					strategyToAnimate.spreadStep();
			}
			else
				strategyToAnimate.spreadStep();
			animationTime++;
			loadGrid(strategyToAnimate.cloneGrid());
		}
		else {
			stopAnimation();
		}
	}
	
	/**
	 * Loads a grid to the canvas and updates the info panel.
	 * @param g
	 */
	private void loadGrid(Grid g) {
		canvas.loadGrid(g);
		updateInfo();
	}

}
