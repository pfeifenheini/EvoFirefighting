package evoFirefighting;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BoxLayout;
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
import strategy.connectedStrategy.ConnectedStrategy;

public class EvoFirefighting extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JPanel centerPane;
	private JPanel topPane;
	private JPanel bottomPane;
	private JPanel infoPane;
	
	private GridCanvas canvas;

	private JButton zoomIn =
			new JButton("+");
	private JButton zoomOut =
			new JButton("-");
	
	private JButton settings = 
			new JButton("settings");
	
	private final String[] strategyChoices = {"connected", "scattered", "protect"}; 
	private JComboBox<String> strategyChoice =
			new JComboBox<String>(strategyChoices);
	
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
	
	private Timer refreshTimer = new Timer(
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
						loadGrid(strategyToAnimate.cloneGrid());
					}
				}
			});
	
	private Evolution evolution = null;
	
	private String[] keyList = {
			"populationSize",
			"simulationTime",
			"initialBudget",
			"budget",
			"mutationRate",
			"startOffset",
			"highwayDistance",
			};
	private Properties parameters = new Properties();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				EvoFirefighting frame = new EvoFirefighting();
				frame.setVisible(true);
			}
		});
	}
	

	
	Strategy best = new ConnectedStrategy(-1,-1,-1,-1,null);
	Strategy clone = null;
	
	public EvoFirefighting() {
		
		parameters.setProperty("populationSize", "20");
		parameters.setProperty("simulationTime", "50");
		parameters.setProperty("initialBudget", "2.0");
		parameters.setProperty("budget", "2.0");
		parameters.setProperty("mutationRate", "2.5");
		parameters.setProperty("startOffset", "(0,-1)");
		parameters.setProperty("highwayDistance", "20");
		
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
		if(e.getSource() == settings) {
			String key = (String)JOptionPane.showInputDialog(
					this,
					"please type the key you want to change",
					"key",
					JOptionPane.PLAIN_MESSAGE,
					null,
					keyList,
					"key");
			if(key!=null) {
				String value = parameters.getProperty(key, "default");
				value = (String)JOptionPane.showInputDialog(
						this,
						"Please type the new value for \"" + key + "\"",
						"value",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						value);
				if(value!=null)
					parameters.setProperty(key, value);
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
			refreshTimer.stop();
			
			if(evolution != null) {
				evolution.stopEvolution();
			}
			
			evolution = new Evolution((String)strategyChoice.getSelectedItem(), parameters);
			evolution.startEvolution();
			
			strategyToAnimate = null;
			
			refreshTimer.start();
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
	
	private void updateInfo(Grid g) {
		infoPane.removeAll();
		
		updateParametersInfo();
		updateEvolutionInfo();
		updateStrategyInfo();
		updateGridInfo(g);

		pack();
		infoPane.repaint();
	}
	
	private void updateParametersInfo() {
		addInfoText("--- Parameters ---");
		addInfoText("initialBudget: " + parameters.getProperty("initialBudget").replaceAll(",", "."));
		addInfoText("budget: " + parameters.getProperty("budget").replaceAll(",", "."));
		addInfoText("mutationRate: " + parameters.getProperty("mutationRate").replaceAll(",", "."));
		if(strategyToAnimate instanceof ConnectedProtectionStrategy)
			addInfoText("startOffset: " + parameters.getProperty("startOffset"));
		if(strategyToAnimate instanceof ConnectedProtectionStrategy)
			addInfoText("highwayDistance: " + parameters.getProperty("highwayDistance"));
	}

	private void updateEvolutionInfo() {
		if(evolution==null) return;
		addInfoText("--- Evolution ---");
		addInfoText("generation: " + evolution.generation());
		addInfoText("population size: " + evolution.populationSize());
		addInfoText("simulation Time: " + evolution.simulationTime());
	}
	
	private void updateStrategyInfo() {
		if(strategyToAnimate==null) return;
		addInfoText("--- Strategy ---");
		if(evolution!=null)
			addInfoText("individual: " + (offset+1) + "/" + evolution.populationSize());
		addInfoText("fitness: " + strategyToAnimate.fitness());
	}
	
	private void updateGridInfo(Grid g) {
		addInfoText("--- Grid ---");
		addInfoText("width: " + g.width());
		addInfoText("heigth: " + g.heigth());
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
	
	private void addInfoText(String s) {
		JLabel label = new JLabel(s);
		infoPane.add(label);
	}
	
	public boolean isAnimating() {
		return animationTimer.isRunning();
	}
	
	public void startAnimation() {
		refreshTimer.stop();
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
			loadGrid(strategyToAnimate.cloneGrid());
		}
		else {
			stopAnimation();
		}
	}
	
	private void loadGrid(Grid g) {
		canvas.loadGrid(g);
		updateInfo(g);
	}
	
	public void stopAnimation() {
		animationTimer.stop();
		refreshTimer.start();
		animate.setEnabled(true);
		startEvolution.setEnabled(true);
	}

}
