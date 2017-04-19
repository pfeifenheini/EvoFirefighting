package evoFirefighting;

import java.util.Arrays;
import strategy.Strategy;
import strategy.connectedStrategy.ConnectedStrategy;
import strategy.scatteredStrategy.ScatteredStrategy;

public class Evolution implements Runnable{
	
	Strategy[] population;
	
	Thread thread;
	private boolean running = false;
	private boolean pause = false;
	
	private int generation = 0;
	
	public Evolution(int populationSize, String strategy) {
		population = new Strategy[populationSize];
		for(int i=0;i<population.length;i++) {
			if(strategy.equals("scattered"))
				population[i] = new ScatteredStrategy();
			else if(strategy.equals("protect"))
				population[i] = new ConnectedProtectionStrategy();
			else
				population[i] = new ConnectedStrategy();
		}
		Arrays.sort(population);
	}
	
	/**
	 * Copies the currently best strategy.
	 * @param target strategy that will be overwritten. Needs to be instantiated
	 * @param offset
	 */
	public void copyBestStrategy(Strategy target, int offset) {
		offset = Math.max(0, Math.min(offset, population.length));
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
		offset = Math.max(0, Math.min(offset, population.length));
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
