package com.xf013.maze;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.xf013.maze.factories.FromFileGridFactory;
import com.xf013.maze.factories.GridFactory;

public class Main {

	public static void main(String argv[]) throws Exception {
		final long startTime = System.currentTimeMillis();
		String inputFileName = argv[0];
		final String outputFileName = argv[1];
		int threadsCount = 4;
		if (argv.length > 2) {
			threadsCount = Integer.parseInt(argv[2]);
		}

		GridFactory gridFactory = new FromFileGridFactory(inputFileName);
		Grid grid = gridFactory.createGrid();
		final GridProcessingManager gpm = new GridProcessingManager(
				threadsCount);
		gpm.setInitialGrid(grid);
		gpm.setMaxTries(grid.getDistanceToTarget() + 111111);
		// Well this is a nasty dirty hack to be sure we don't run more than 2
		// minutes
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				//System.out.println("timer here");
				try {
					gpm.stop();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 1 * 60 * 100 - 200);
//		System.out.println("started");
		gpm.start();
		timer.cancel();
		gpm.stop();
//		System.out.println("after started");
		
		executeFinalStep(outputFileName, gpm);

		long endTime = System.currentTimeMillis();
		
		System.out.println("Time spent:" + (endTime - startTime));
	}

	private static void executeFinalStep(String outputFileName,
			final GridProcessingManager gpm) throws IOException {
		List<Integer> history = gpm.getShortestHistory();
		String result = "";
		
		if (history.isEmpty()) {
			result = "No result";
		} else {
			for (int i = 0; i < history.size(); i++) {
				result += history.get(i) + (i % 40 == 39 ? "\n" : " ");
			}
		}
		
		FileWriter fstream = new FileWriter(outputFileName);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write(result.trim());
	    out.close();
	    System.out.println(gpm.getVisited().size());
	}

}