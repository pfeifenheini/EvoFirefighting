package evoFirefighting;

import java.util.Arrays;
import java.util.Properties;

import grid.Coordinate;
import strategy.Strategy;
import strategy.connectedStrategy.ConnectedProtectionStrategy;
import strategy.connectedStrategy.ConnectedStrategy;
import strategy.scatteredStrategy.ScatteredStrategy;

public class Evolution implements Runnable{
	
	Strategy[] population;
	
	Thread thread;
	private boolean running = false;
	private boolean pause = false;
	
	private int generation = 0;
	private int simulationTime = 0;
	
	public Evolution(
			String strategy,
			Properties parameters) {
		if(parameters==null) parameters=new Properties();
		simulationTime = Integer.parseInt(parameters.getProperty("simulationTime", "-1"));
		double initialAccount = Double.parseDouble(parameters.getProperty("initialAccount", "-1"));
		double budget = Double.parseDouble(parameters.getProperty("budget", "-1"));
		double mutationRate = Double.parseDouble(parameters.getProperty("mutationRate", "-1"));
		
		Coordinate startOffset = parseCoordinate(parameters.getProperty("startOffset","(0,0)"));
		if(startOffset.x == 0 && startOffset.y == 0)
			startOffset = null;
		
		int highwayDistance = Integer.parseInt(parameters.getProperty("highwayDistance", "-1"));
		
		
		population = new Strategy[Integer.parseInt(parameters.getProperty("populationSize","20"))];
		for(int i=0;i<population.length;i++) {
			if(strategy.equals("scattered"))
				population[i] = new ScatteredStrategy(simulationTime,initialAccount,budget,mutationRate);
			else if(strategy.equals("protect"))
				population[i] = new ConnectedProtectionStrategy(simulationTime,initialAccount,budget,mutationRate,startOffset,highwayDistance);
			else
				population[i] = new ConnectedStrategy(simulationTime,initialAccount,budget,mutationRate,startOffset);
		}
		Arrays.sort(population);
	}
	
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
	
	public void startEvolution() {
		if(!running) {
			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}
	
	public void stopEvolution() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int populationSize() {
		return population.length;
	}
	
	public int simulationTime() {
		return simulationTime;
	}
	
	public int generation() {
		return generation;
	}
	
	@Override
	public void run() {
		running = true;
		Strategy tmp = population[0].clone();
		synchronized(this) {
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
}
