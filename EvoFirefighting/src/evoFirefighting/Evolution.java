package evoFirefighting;

import java.util.Arrays;
import strategy.Strategy;
import strategy.connectedStrategy.ConnectedStrategy;
import strategy.scatteredStrategy.ScatteredStrategy;

public class Evolution implements Runnable{
	
//	private static final double parentRation = 0.2;
	
	Strategy[] population;
	
	Thread thread;
	private boolean running = false;
	
	public Evolution(int populationSize, String strategy) {
		population = new Strategy[populationSize];
		for(int i=0;i<population.length;i++) {
			if(strategy.equals("scattered"))
				population[i] = new ScatteredStrategy();
			else
				population[i] = new ConnectedStrategy();
		}
		Arrays.sort(population);
	}
	
	/**
	 * Returns the i-th best strategy. i is defined by the offset.
	 * @param offset
	 * @return i-th best strategy in the population
	 */
	public Strategy cloneBestStrategy(int offset) {
		Strategy s;
		offset = Math.max(0, Math.min(offset, population.length));
		synchronized(this) {
			s = population[offset].clone();
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
	
	@Override
	public void run() {
		running = true;
		while(running) {
			synchronized(this) {
				for(int i=1;i<population.length;i++) {
					population[i] = population[0].clone();
					population[i].mutate();
				}
			}
			Arrays.sort(population);
		}
	}
}
