package at.brandl.games.maze.generator;

import static at.brandl.games.commons.Direction.AHEAD;
import static at.brandl.games.commons.Direction.LEFT;
import static at.brandl.games.commons.Direction.RIGHT;
import static at.brandl.games.maze.generator.Path.Target.*;

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
import at.brandl.games.maze.generator.Path.Section;
import at.brandl.games.maze.generator.Path.Target;

public class MazeGenerator {

	private static final int AVERAGE_NUMBER_SECTIONS_TO_NEXT_BRANCH = 10;

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

		Queue<Path> startPaths = initPath(start, START);
		Queue<Path> endPaths = initPath(end, END);

		boolean solutionFound = false;

		do {
			Path startPath = startPaths.poll();
			Path endPath = endPaths.poll();

			boolean advanced;
			do {
				advanced = false;
				if (advance(startPath, startPaths)) {
					advanced = true;
					if (tryConnect(startPath, END)) {
						solutionFound = true;
						break;
					}
				}

				if (advance(endPath, endPaths)) {
					advanced = true;
					if (tryConnect(endPath, START)) {
						solutionFound = true;
						break;
					}
				}

			} while (advanced);

		} while (!(solutionFound || (startPaths.isEmpty() && endPaths.isEmpty())));
		if (!solutionFound) {
			throw new NoPathFoundException("no path found");
		}

		return merge(startPaths, endPaths);
	}

	private ConcurrentLinkedQueue<Path> merge(Queue<Path> startPaths,
			Queue<Path> endPaths) {
		List<Path> allPath = new ArrayList<Path>();
		allPath.addAll(startPaths);
		allPath.addAll(endPaths);
		Collections.shuffle(allPath);
		return new ConcurrentLinkedQueue<Path>(allPath);
	}

	private Queue<Path> initPath(Field<Section> field, Target target) {
		Orientation borderOrientation = board.getBorders(field).iterator()
				.next();
		Path path = new Path(borderOrientation.opposite()).go(AHEAD);
		Section section = path.getEnd();
		section.setTarget(target, section);
		field.setContent(section);

		Queue<Path> startPaths = new ConcurrentLinkedQueue<Path>();
		startPaths.add(path);
		return startPaths;
	}

	private boolean advance(Path path, Collection<Path> branches) {

		if (path == null) {
			return false;
		}

		Field<Section> field = path.getEnd().getField();
		Orientation currentOrientation = path.getCurrentDirection();

		Collections.shuffle(turns);
		for (Direction direction : turns) {

			Orientation newOrientation = currentOrientation.turn(direction);
			Field<Section> neighbour = field.getEmptyNeighbours().get(
					newOrientation);
			if (neighbour != null) {
				if (branchingTime()) {

					Path branch = path.createPath(direction);
					neighbour.setContent(branch.getStart());
					branches.add(branch);

				} else {

					path.go(direction);
					neighbour.setContent(path.getEnd());
					return true;

				}
			}
		}

		return false;
	}

	private boolean branchingTime() {
		return random.nextInt(AVERAGE_NUMBER_SECTIONS_TO_NEXT_BRANCH) == 0;
	}

	private boolean tryConnect(Path path, Target target) {

		Map<Orientation, Field<Section>> nonEmptyNeighbours = path.getEnd()
				.getField().getNonEmptyNeighbours();
		if (!nonEmptyNeighbours.isEmpty()) {
			Iterator<Entry<Orientation, Field<Section>>> iterator = nonEmptyNeighbours
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Orientation, Field<Section>> neighbour = iterator.next();

				Section neighbourSection = neighbour.getValue().getContent();
				if (neighbourSection.hasTarget(target)) {
					path.connect(neighbour.getKey(), neighbourSection);
					return true;
				}
			}
		}
		return false;
	}

}
