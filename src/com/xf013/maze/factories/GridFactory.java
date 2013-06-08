package com.xf013.maze.factories;

import com.xf013.maze.Grid;

public interface GridFactory {
	Grid createGrid() throws GridFactoryException;
}
