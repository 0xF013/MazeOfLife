package com.xf013.maze;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public class GridProcessingManager {

	// private Stack<Grid> stack = new Stack<Grid>();
	private Queue<Grid> stack = new PriorityBlockingQueue<Grid>(1,
			new Comparator<Grid>() {

				@Override
				public int compare(Grid o1, Grid o2) {
					if (o1.getDistanceToTarget() < o2.getDistanceToTarget()) {
						return -1;
					} else {
						return 1;
					}
				}
			});

	private Set<Integer> visited = Collections
			.synchronizedSet(new HashSet<Integer>());

	private List<Integer> shortestHistory = new ArrayList<Integer>();
	private int threadsCount;
	private GridWorker[] threads;

	private Grid initialGrid;
	
	private boolean stopCalled = false;

	// true for waiting
	private boolean[] threadStates;

	private int maxTries = 10;

	public GridProcessingManager(int threadsCount) {
		this.threadsCount = threadsCount;
		threads = new GridWorker[threadsCount];
		threadStates = new boolean[threadsCount];
	}

	public Queue<Grid> getStack() {
		return stack;
	}

	public Set<Integer> getVisited() {
		return visited;
	}

	synchronized public void setShortestHistory(List<Integer> shortestHistory) {
		if (this.shortestHistory.size() == 0
				|| shortestHistory.size() < this.shortestHistory.size()) {
			this.shortestHistory = shortestHistory;
		}
	}

	public List<Integer> getShortestHistory() {
		return shortestHistory;
	}

	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}

	public int getMaxTries() {
		return maxTries;
	}

	public void setInitialGrid(Grid grid) {
		this.initialGrid = grid;
	}

	public void setThreadState(int id, boolean state) {
		threadStates[id] = state;
	}
	
	public void stop() throws InterruptedException {
		if (stopCalled) {
			return;
		}
		stopCalled = true;
//		System.out.println("stop ");
		for (int i = 0; i < threadsCount; i++) {
			GridWorker x = threads[i];
			x.setIsRunning(false);		
		}
		

		
		while(true) {
			boolean allStopped = true;
			for (int i = 0; i < threadsCount; i++) {
				if (!threads[i].getIsFinished()) {
					// System.out.println(i);
					allStopped = false;
					break;
				}
			}
			
			if (allStopped) {
				break;
			} else {
				synchronized (getStack()) {
					stack.clear();
					stack.notify();			
				}						
				synchronized (threadStates) {					
					threadStates.wait();
				}
			}
		}
		
		
		
//		synchronized (threadStates) {
//			threadStates.notify();
//		}		
	}

	public void start() throws InterruptedException {

		Grid clone = initialGrid.clone();
		clone.setOriginal();
		stack.add(clone);
		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new GridWorker(this, i);
			threadStates[i] = false;
			threads[i].start();
		}

		while (!stopCalled) {
			boolean waiting = true;
			synchronized (threadStates) {

				for (int j = 0; j < threadsCount; j++) {
					if (threadStates[j] == false) {
						waiting = false;
						break;
					}
				}
			}

			if (waiting) {
//				boolean allStopped = true;
//				for (int i = 0; i < threadsCount; i++) {
//					if (!threads[i].getIsFinished()) {
//						//System.out.println(i);
//						allStopped = false;
//						break;
//					}
//				}
//				if (allStopped) {
//					System.out.println("breaking");
//					break;
//				}
				break;
			} else {
				synchronized (threadStates) {
					threadStates.wait();
				}
			}

		}
		

	}

	public boolean[] getThreadStates() {
		return threadStates;
	}

}
