package com.xf013.maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid implements Cloneable {

	private int width = 0;
	private int height = 0;
	private boolean[] currentCells;
	private boolean[] nextCells;

	private int intelligentX = 0;
	private int intelligentY = 0;

	private int targetX = 0;
	private int targetY = 0;

	private boolean isOriginal = false;

	private List<Integer> intellingentMovementHistory = new ArrayList<Integer>();

	public void setOriginal() {
		isOriginal = true;
	}

	private boolean getCellAt(int x, int y) {
		return currentCells[x * width + y];
	}

	private void setCellAt(int x, int y, boolean isAlive) {

		currentCells[x * width + y] = isAlive;
	}

	@Override
	public Grid clone() {

		Grid ret = new Grid(width, height, intelligentX, intelligentY, targetX,
				targetY);
		ret.setCurrentCells(Arrays.copyOf(currentCells, currentCells.length));
		for (Integer i : intellingentMovementHistory) {
			ret.getIntellingentMovementHistory().add(i);
		}
		return ret;

	}

	public int getIntelligentY() {
		return intelligentY;
	}

	public void setIntelligentY(int intelligentY) {
		this.intelligentY = intelligentY;
	}

	public List<Integer> getIntellingentMovementHistory() {
		return intellingentMovementHistory;
	}

	public void setIntellingentMovementHistory(
			List<Integer> intellingentMovementHistory) {
		this.intellingentMovementHistory = intellingentMovementHistory;
	}

	public Grid(int width, int height, int intelligentX, int intelligentY,
			int targetX, int targetY) {
		super();
		this.width = width;
		this.height = height;
		this.intelligentX = intelligentX;
		this.intelligentY = intelligentY;
		this.targetX = targetX;
		this.targetY = targetY;

		currentCells = new boolean[width * height];
		nextCells = new boolean[width * height];
	}

	public boolean willIntelligentCellBeAliveNextTurn() {
		int nNeighbours = 0;
		int x = intelligentX;
		int y = intelligentY;
		for (int deltaX = -1; deltaX <= 1; deltaX++) {
			int nextX = x + deltaX;
			if (nextX == width) {
				// nextX = 0;
				continue;
			} else if (nextX < 0) {
				// nextX = width - 1;
				continue;
			}

			for (int deltaY = -1; deltaY <= 1; deltaY++) {
				if ((deltaX == 0) && (deltaY == 0)) {
					continue;
				}

				int nextY = y + deltaY;
				if (nextY == height) {
					// nextY = 0;
					continue;
				} else if (nextY < 0) {
					// nextY = height - 1;
					continue;
				}

				if (getCellAt(nextX, nextY))
					nNeighbours++;
			}
		}

		if (nNeighbours == 2 || nNeighbours == 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param position
	 * @return true if can move there or false otherwise
	 */
	public boolean moveIntelligentCell(int position) {
		int newIntelligentX = intelligentX;
		int newIntelligentY = intelligentY;
		switch (position) {
		case 1:
			newIntelligentX -= 1;
			newIntelligentY -= 1;
			break;
		case 2:
			newIntelligentX -= 1;
			break;
		case 3:
			newIntelligentY += 1;
			newIntelligentX -= 1;
			break;
		case 4:
			newIntelligentY += 1;
			break;
		case 5:
			newIntelligentY += 1;
			newIntelligentX += 1;
			break;
		case 6:
			newIntelligentX += 1;
			break;
		case 7:
			newIntelligentY -= 1;
			newIntelligentX += 1;
			break;
		case 8:
			newIntelligentY -= 1;
			break;
		}

		// check borders

		if (newIntelligentX < 0 || newIntelligentX >= width
				|| newIntelligentY < 0 || newIntelligentY >= height) {
			return false;
		}
		// check if the new position is taken
		if (position != 0 && getCellAt(newIntelligentX, newIntelligentY)) {
			return false;
		}

		setCellAt(intelligentX, intelligentY, false);
		intelligentX = newIntelligentX;
		intelligentY = newIntelligentY;
		setCellAt(intelligentX, intelligentY, true);
		intellingentMovementHistory.add(position);
		return true;
	}

	public void setCell(int x, int y, boolean isAlive) {
		setCellAt(x, y, isAlive);
	}

	public boolean isIntelligentCellAlive() {
		return getCellAt(intelligentX, intelligentY);
	}

	public boolean isIntelligenceCellAtTarget() {
		return (intelligentX == targetX && intelligentY == targetY);
	}

	private int getNeighbours(int x, int y) {
		int nNeighbours = 0;
		for (int deltaX = -1; deltaX <= 1; deltaX++) {
			int nextX = x + deltaX;
			if (nextX == width) {
				// nextX = 0;
				continue;
			} else if (nextX < 0) {
				// nextX = width - 1;
				continue;
			}

			for (int deltaY = -1; deltaY <= 1; deltaY++) {
				if ((deltaX == 0) && (deltaY == 0)) {
					continue;
				}

				int nextY = y + deltaY;
				if (nextY == height) {
					// nextY = 0;
					continue;
				} else if (nextY < 0) {
					// nextY = height - 1;
					continue;
				}

				if (getCellAt(nextX, nextY))
					nNeighbours++;
			}
		}

		return nNeighbours;
	}

	public void takeNextStep() {
		if (isOriginal) {
			isOriginal = false;
			return;
		}
		
		int currentCellsLength = currentCells.length;
		for (int i = 0; i < currentCellsLength; i++) {
			int x = i / width;
			int y = i % width;
			int neighbours = getNeighbours(x, y);
			if (getCellAt(x, y)) {
				switch (neighbours) {
				case 0:
				case 1:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					nextCells[x * width + y] = false;
					break;

				default:
					nextCells[x * width + y] = true;
				}
			} else {
				nextCells[x * width + y] = neighbours == 3;
			}			
		}
		

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setCellAt(x, y, nextCells[x * width + y]);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean[] getCurrentCells() {
		return currentCells;
	}

	public void setCurrentCells(boolean[] currentCells) {
		this.currentCells = currentCells;
	}

	public boolean[] getNextCells() {
		return nextCells;
	}

	public void setNextCells(boolean[] nextCells) {
		this.nextCells = nextCells;
	}

	public int getIntelligentX() {
		return intelligentX;
	}

	public void setIntelligentX(int intelligentX) {
		this.intelligentX = intelligentX;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public int getDistanceToTarget() {
		int xDistance = Math.abs(getIntelligentX() - getTargetX());
		int yDistance = Math.abs(getIntelligentY() - getTargetY());
		return Math.max(xDistance, yDistance);
	}

	public int hashCode() {
		return (String.valueOf(String.valueOf(Arrays.hashCode(currentCells))
				+ String.valueOf(intelligentX) + String.valueOf(intelligentY)))
				.hashCode();
	}

	// public String getTextualRepresentation() {
	// StringBuilder sb = new StringBuilder();
	// for (int n = 0; n < currentCells.length; n ++) {
	// int i = n / width;
	// int j = i % width;
	//
	// String value = getCellAt(i, j) ? "+" : "-";
	// if (i == intelligentX && j == intelligentY && getCellAt(i, j)) {
	// value = "x";
	// }
	// sb.append(value);
	// if (j == 0) {
	// sb.append("\n");
	// }
	// }
	// return sb.toString();
	//
	// }

}
