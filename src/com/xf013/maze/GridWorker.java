package com.xf013.maze;


public class GridWorker extends Thread {

	GridProcessingManager gpm;
	private int id;
	Grid grid;
	
	private boolean finished = false;
	
	private boolean running = true;

	public GridWorker(GridProcessingManager gpm, int id) {
		this.gpm = gpm;
		this.id = id;
	}
	
	public void setIsRunning(boolean running) {
		this.running = running;
	}

	public void setIsWaiting(boolean isWaiting) {
		synchronized (gpm.getThreadStates()) {
			gpm.setThreadState(id, isWaiting);
			gpm.getThreadStates().notify();
		}
	}

	public void run() {
		while (running) {
//			System.out.println("running " + id);
			setIsWaiting(false);
			try {

				if (gpm.getStack().isEmpty()) {
					setIsWaiting(true);
					synchronized (gpm.getStack()) {
						//System.out.println("waiting " + id);
						gpm.getStack().wait();
						//System.out.println("after waiting " + id);
						continue;
					}
				} else {
					grid = gpm.getStack().poll();
					if (grid == null) {
						continue;
					}
					
					int hash = grid.hashCode();
					if (gpm.getVisited().contains(hash)) {
						continue;
					} else {
						gpm.getVisited().add(hash);
					}

				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			
			
			grid.takeNextStep();
			
			if (grid.isIntelligenceCellAtTarget()) {
				gpm.setShortestHistory(grid.getIntellingentMovementHistory());
				continue;
			}
			if (areTriesLeft() && isCurrentHistoryValid()) {
				for (int i = 1; i <= 9; i++) {
					int position = i;
					if (i == 9) {
						position = 0;
					}

					Grid clone = grid.clone();

					if (clone.moveIntelligentCell(position)) {

						if (clone.willIntelligentCellBeAliveNextTurn() && running) {
//							System.out.println("before add " + id);
							gpm.getStack().add(clone);
							synchronized (gpm.getStack()) {
								gpm.getStack().notify();
							}
//							System.out.println("after add " + id);
						} else {
							continue;
						}

					}
				}
			}

		}
//		System.out.println("STOOPED " + id);
		finished = true;
		setIsWaiting(true);
		
	}

	private boolean isCurrentHistoryValid() {
		return gpm.getShortestHistory().size() == 0
				|| (gpm.getShortestHistory().size() > grid
						.getIntellingentMovementHistory().size() +  grid.getDistanceToTarget());
	}

	private boolean areTriesLeft() {
		return gpm.getMaxTries() >= grid.getIntellingentMovementHistory()
				.size();
	}

	public boolean getIsRunning() {
		return running;
	}
	
	public boolean getIsFinished() {
		return finished;
	}	

}
