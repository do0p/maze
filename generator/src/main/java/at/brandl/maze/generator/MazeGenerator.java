package at.brandl.maze.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import at.brandl.games.commons.Board;
import at.brandl.games.commons.Direction;
import at.brandl.games.commons.Board.Field;
import at.brandl.maze.generator.Path.Section;

public class MazeGenerator {

	private static final int LEFT = 0;
	private static final int AHEAD = 1;
	private static final int RIGHT = 2;
	private List<Integer> turns = Arrays.asList(LEFT, AHEAD, RIGHT);

	private final Board<Section> board;
	private Field<Section> start;
	private Field<Section> end;

	public MazeGenerator(Board<Section> board) {
		this.board = board;
	}

	public void setStart(int row, int column) {
		start = board.getField(row, column);
		if (board.getBorders(start).isEmpty()) {
			throw new IllegalArgumentException("row " + row + " and column "
					+ column + " not on border");
		}
	}

	public void setEnd(int row, int column) {
		end = board.getField(row, column);
		if (board.getBorders(end).isEmpty()) {
			throw new IllegalArgumentException("row " + row + " and column "
					+ column + " not on border");
		}
	}

	public void generate() {

		// create solution path
		Field<Section> currentStartPathField = start;
		Direction startBorder = board.getBorders(start).iterator().next();
		Path startPath = new Path(startBorder.opposite());
		currentStartPathField.setContent(startPath.getEnd());

		Field<Section> currentEndPathField = end;
		Direction endBorder = board.getBorders(start).iterator().next();
		Path endPath = new Path(endBorder.opposite());
		currentEndPathField.setContent(endPath.getEnd());

		boolean connected = false;
		do {

			if (currentStartPathField != null) {
				currentStartPathField = advance(currentStartPathField,
						startPath);
				connected = tryConnect(currentStartPathField, startPath);

				if (connected) {
					break;
				}
			}

			if (currentEndPathField != null) {
				currentEndPathField = advance(currentEndPathField, endPath);
				connected = tryConnect(currentEndPathField, endPath);
			} else if (currentStartPathField == null) {
				throw new IllegalStateException(
						"no connection possible on board:\n " + board);
			}

		} while (!connected);

		// create branches
		// fill empty spots
		System.out.println(board);
	}

	private Field<Section> advance(Field<Section> currentPathField, Path path) {

		Collections.shuffle(turns);

		Map<Direction, Field<Section>> neighbours = currentPathField.getNeighbours();
		Direction currentDirection = path.getCurrentDirection();

		Field<Section> newPathField = null;
		Field<Section> neighbour;
		for (Integer turn : turns) {
			switch (turn) {
			case LEFT:
				neighbour = neighbours.get(currentDirection.left());
				if (neighbour != null && neighbour.isEmpty()) {
					path.turnLeft();
					neighbour.setContent(path.getEnd());
					newPathField = neighbour;
				}
				break;

			case AHEAD:
				neighbour = neighbours.get(currentDirection);
				if (neighbour != null && neighbour.isEmpty()) {
					path.ahead();
					neighbour.setContent(path.getEnd());
					newPathField = neighbour;
				}
				break;

			case RIGHT:
				neighbour = neighbours.get(currentDirection.right());
				if (neighbour != null && neighbour.isEmpty()) {
					path.turnRight();
					neighbour.setContent(path.getEnd());
					newPathField = neighbour;
				}
				break;

			}
			if (newPathField != null) {
				break;
			}
		}

		return newPathField;
	}

	private boolean tryConnect(Field<Section> currentField, Path path) {
		if(currentField == null) {
			return false;
		}
		Map<Direction, Field<Section>> nonEmptyNeighbours = currentField
				.getNonEmptyNeighbours();
		if (!nonEmptyNeighbours.isEmpty()) {
			Iterator<Entry<Direction, Field<Section>>> iterator = nonEmptyNeighbours
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Direction, Field<Section>> neighbour = iterator.next();

				Section neighbourSection = neighbour.getValue().getContent();
				if (!path.equals(neighbourSection.getPath())) {
					path.connect(neighbour.getKey(), neighbourSection);
					return true;
				}
			}
		}
		return false;
	}

}
