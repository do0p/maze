package at.brandl.games.maze;

import static at.brandl.games.commons.Direction.AHEAD;
import static at.brandl.games.commons.Direction.LEFT;
import static at.brandl.games.commons.Direction.RIGHT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import at.brandl.games.commons.Board;
import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Direction;
import at.brandl.games.commons.Orientation;
import at.brandl.games.maze.Path.Section;

public class MazeGenerator {

	public static class NoPathFoundException extends Exception {

		public NoPathFoundException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 7071070037078290539L;

	}

	private List<Direction> turns = Arrays.asList(LEFT, AHEAD, RIGHT);

	private final Board<Section> board;
	private final Random random;
	private Field<Section> start;
	private Field<Section> end;


	public MazeGenerator(Board<Section> board) {
		this.board = board;
		random = new Random(System.currentTimeMillis());
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

	public void generate() throws NoPathFoundException {

		// create solution path
		Collection<Path> branches = createSolutionPath();

		// create branches
		createBranches(branches);

		// fill empty spots
		fillEmptyFields(branches);

	}

	private void fillEmptyFields(Collection<Path> branches) {
		Iterator<Field<Section>> iterator = board.getEmptyFields().iterator();
		while (iterator.hasNext()) {
			Field<Section> field = iterator.next();
			Map<Orientation, Field<Section>> nonEmptyNeighbours = field
					.getNonEmptyNeighbours();
			if (!nonEmptyNeighbours.isEmpty()) {
				Entry<Orientation, Field<Section>> neighbour = nonEmptyNeighbours
						.entrySet().iterator().next();
				Path path = new Path(neighbour.getKey().opposite());
				field.setContent(path.getStart());
				path.connect(neighbour.getKey(), neighbour.getValue()
						.getContent());
				boolean advanced = false;
				do {
					advanced = advance(path, branches);
					createBranches(branches);
				} while (advanced);
				iterator = board.getEmptyFields().iterator();
			}
		}
	}

	private void createBranches(Collection<Path> branches) {
		while (!branches.isEmpty()) {
			Iterator<Path> iterator = branches.iterator();
			while (iterator.hasNext()) {
				Path branch = iterator.next();
				if (!advance(branch, branches)) {
					iterator.remove();
				}
			}

		}
	}

	private Collection<Path> createSolutionPath() throws NoPathFoundException {

		Queue<Path> startPaths = new ConcurrentLinkedQueue<Path>();
		Queue<Path> endPaths = new ConcurrentLinkedQueue<Path>();

		Orientation startBorder = board.getBorders(start).iterator().next();
		Path startPath = new Path(startBorder.opposite()).go(AHEAD);
		start.setContent(startPath.getEnd());
		startPaths.add(startPath);

		Orientation endBorder = board.getBorders(end).iterator().next();
		Path endPath = new Path(endBorder.opposite()).go(AHEAD);
		end.setContent(endPath.getEnd());
		endPaths.add(endPath);

		boolean advanced;
		List<Path> existingStartPaths = new ArrayList<Path>();
		List<Path> existingEndPaths = new ArrayList<Path>();
		do {
			startPath = startPaths.poll();
			if(startPath != null) {
				existingStartPaths.add(startPath);
			}
			endPath = endPaths.poll();
			if(endPath != null) {
				existingEndPaths.add(endPath);
			}
			do {
				advanced = false;
				if (advance(startPath, startPaths)) {
					advanced = true;
					if (tryConnect(startPath, existingEndPaths)) {
						break;
					}
				} 

				if (advance(endPath, endPaths)) {
					advanced = true;
					if (tryConnect(endPath, existingStartPaths)) {
						break;
					}
				}

			} while (advanced);
			if (advanced) {
				break;
			}
			
		} while (!(startPaths.isEmpty() && endPaths.isEmpty()));
		if (!advanced) {
			throw new NoPathFoundException("no path found");
		}

		List<Path> allPath = new ArrayList<Path>();
		allPath.addAll(startPaths);
		allPath.addAll(endPaths);
		Collections.shuffle(allPath);
		return new ConcurrentLinkedQueue<Path>(allPath);
	}

	private boolean advance(Path path, Collection<Path> branches) {

		if (path == null) {
			return false;
		}

		Collections.shuffle(turns);

		Field<Section> field = path.getEnd().getField();
		Map<Orientation, Field<Section>> neighbours = field.getNeighbours();
		Orientation currentDirection = path.getCurrentDirection();

		boolean advanced = false;
		Field<Section> neighbour;
		for (Direction direction : turns) {

			neighbour = neighbours.get(currentDirection.turn(direction));
			if (neighbour != null && neighbour.isEmpty()) {
				if (branchingTime()) {
					Path branch = path.createPath(direction);
					neighbour.setContent(branch.getStart());
					branches.add(branch);
				} else {
					path.go(direction);
					neighbour.setContent(path.getEnd());
					advanced = true;
					break;
				}

			}

		}

		return advanced;
	}

	private boolean branchingTime() {
		return random.nextInt(5) == 0;
	}

	private boolean tryConnect(Path path, Collection<Path> paths) {

		Map<Orientation, Field<Section>> nonEmptyNeighbours = path.getEnd()
				.getField().getNonEmptyNeighbours();
		if (!nonEmptyNeighbours.isEmpty()) {
			Iterator<Entry<Orientation, Field<Section>>> iterator = nonEmptyNeighbours
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Orientation, Field<Section>> neighbour = iterator.next();

				Section neighbourSection = neighbour.getValue().getContent();
				if (paths.contains(neighbourSection.getPath())) {
					path.connect(neighbour.getKey(), neighbourSection);
					return true;
				}
			}
		}
		return false;
	}

}
