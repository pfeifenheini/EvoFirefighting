package evoFirefighting;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import grid.Coordinate;
import strategy.Strategy;
import strategy.connectedStrategy.ConnectedProtectionStrategy;
import strategy.connectedStrategy.ConnectedStrategy;
import strategy.scatteredStrategy.ScatteredProtectionStrategy;
import strategy.scatteredStrategy.ScatteredStrategy;

/**
 * Implements an evolutionary algorithm to solve the fire fighter problem on a grid.
 * @author Martin
 *
 */
public class Evolution implements Runnable{
	
	/** Population, which will be ordered by fitness. The best individual will be at index 0. */
	Strategy[] population;
	
	/** Thread that will run the algorithm. */
	Thread thread;
	/** Indicator whether the thread is running. */
	private boolean running = false;
	/** Interrupts the thread if the population is accessed */
	private boolean pause = false;
	
	/** Generation counter. */
	private int generation = 0;
	/** Simulation time that is used for fitnes determination. */
	private int simulationTime = 0;
	
	/**
	 * Constructor.
	 * @param mode Operation mode defines which problem will be solved and what kind of strategy is used.
	 * @param parameters Parameters for the algorithm.
	 */
	public Evolution(
			Mode mode,
			Properties parameters) {
		if(parameters==null) parameters=new Properties();
		simulationTime = Integer.parseInt(parameters.getProperty(Parameter.simulationTime.name(), Parameter.simulationTime.getDefaultValueString()));
		double initialAccount = Double.parseDouble(parameters.getProperty(Parameter.initialAccount.name(), Parameter.initialAccount.getDefaultValueString()).replaceAll(",", "."));
		double budget = Double.parseDouble(parameters.getProperty(Parameter.budget.name(), Parameter.budget.getDefaultValueString()).replaceAll(",", "."));
		double mutationRate = Double.parseDouble(parameters.getProperty(Parameter.mutationRate.name(), Parameter.mutationRate.getDefaultValueString()).replaceAll(",", "."));
		
		Coordinate startOffset = parseCoordinate(parameters.getProperty(Parameter.startOffset.name(),Parameter.startOffset.getDefaultValueString()));
		if(startOffset.x == 0 && startOffset.y == 0)
			startOffset = null;
		
		int highwayDistance = Integer.parseInt(parameters.getProperty(Parameter.highwayDistance.name(), Parameter.highwayDistance.getDefaultValueString()));
		
		
		population = new Strategy[Integer.parseInt(parameters.getProperty(Parameter.populationSize.name(),Parameter.populationSize.getDefaultValueString()))];
		for(int i=0;i<population.length;i++) {
			if(mode == Mode.EncloseScattered)
				population[i] = new ScatteredStrategy(simulationTime,initialAccount,budget,mutationRate);
			else if(mode == Mode.ProtectConnected)
				population[i] = new ConnectedProtectionStrategy(simulationTime,initialAccount,budget,mutationRate,startOffset,highwayDistance);
			else if(mode == Mode.ProtectScattered)
				population[i] = new ScatteredProtectionStrategy(simulationTime,initialAccount,budget,mutationRate,highwayDistance);
			else //default EncloseConnected
				population[i] = new ConnectedStrategy(simulationTime,initialAccount,budget,mutationRate,startOffset);
		}
		Arrays.sort(population);
	}
	
	/**
	 * Parses two dimensional coordinates from a String in the form "(x,y)". 
	 * @param s String to parse.
	 * @return Coordinated the string represents.
	 */
	public static Coordinate parseCoordinate(String s) {
		int x, y;
		String cleaned = s.replaceAll(" ", "");
		int left,comma,right;
		left = cleaned.indexOf("(");
		comma = cleaned.indexOf(",");
		right = cleaned.indexOf(")");
		
		x = Integer.parseInt(cleaned.substring(left+1, comma));
		y = Integer.parseInt(cleaned.substring(comma+1, right));
		return new Coordinate(x,y);
	}
	
	/**
	 * Copies the currently best strategy.
	 * @param target strategy that will be overwritten. Needs to be instantiated
	 * @param offset
	 */
	public void copyBestStrategy(Strategy target, int offset) {
		offset = Math.max(0, Math.min(offset, population.length-1));
		pause = true;
		synchronized(this) {
			target.copy(population[offset]);
			notify();
		}
	}
	
	/**
	 * Returns the i-th best strategy. i is defined by the offset.
	 * @param offset
	 * @return i-th best strategy in the population
	 */
	public Strategy cloneBestStrategy(int offset) {
		Strategy s;
		offset = Math.max(0, Math.min(offset, population.length-1));
		pause = true;
		synchronized(this) {
			s = population[offset].clone();
			notify();
		}
		return s;
	}
	
	/**
	 * Starts a new thread which will perform the algorithm. Only starts if the thread isn't already running.
	 */
	public void startEvolution() {
		if(!running) {
			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}
	
	/**
	 * Stops the thread. After this method has been called, the algorithm can be startet again.
	 */
	public void stopEvolution() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return Population size.
	 */
	public int populationSize() {
		return population.length;
	}
	
	/**
	 * 
	 * @return Simulation time used for fitness determination.
	 */
	public int simulationTime() {
		return simulationTime;
	}
	
	/**
	 * 
	 * @return Current generation.
	 */
	public int generation() {
		return generation;
	}
	
	/**
	 * Implements the evolution as a kind random optimization.
	 * This means that every individual evolves independently. At each generation, each individual
	 * is copied and mutated. If this mutation has a higher fitness than the original, the original
	 * is replaced.
	 */
	private void algorithmRandomOptimization() {
		Strategy tmp = population[0].clone();
		synchronized (this) {
			while(running) {
				for(int i=0;i<population.length;i++) {
					tmp.copy(population[i]);
					tmp.mutate();
					if(tmp.compareTo(population[i])<0) {
						population[i].copy(tmp);
					}
				}
				Arrays.sort(population);
				generation++;
				if(pause) {
					pause = false;
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Implements a usual evolutionary algorithm that follows the cycle 
	 * External Selection -> ParentSelection -> Inheritance -> Mutation -> Fitness Evaluation.
	 */
	private void algorithmTypicalEvolution() {
		int parents = population.length/5; // 20% are kept as parents
		Random rand = new Random();
		synchronized (this) {
			while(running) {
				
				for(int i=parents;i<population.length;i++) {
					population[i].copy(population[rand.nextInt(population.length)]);
					population[i].mutate();
				}
			
				Arrays.sort(population);
				generation++;
				if(pause) {
					pause = false;
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		running = true;
		algorithmRandomOptimization();
//		algorithmTypicalEvolution();
	}
}
