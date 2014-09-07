package at.brandl.games.maze;

import static at.brandl.games.commons.Direction.AHEAD;
import static at.brandl.games.commons.Direction.LEFT;
import static at.brandl.games.commons.Direction.RIGHT;

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

	private class NoPathFoundException extends RuntimeException {

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
	private static final int MAX_TRIES = 1000000;

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

	public void generate() {

		// create solution path
		Collection<Path> branches = createSolutionPath();

		// create branches
		createBranches(branches);

		// fill empty spots
		Iterator<Field<Section>> iterator = board.getEmptyFields().iterator();
		while(iterator.hasNext()) {
			Field<Section> field = board.getEmptyFields().iterator().next();
			Map<Orientation, Field<Section>> nonEmptyNeighbours = field.getNonEmptyNeighbours();
			if(!nonEmptyNeighbours.isEmpty()) {
				Entry<Orientation, Field<Section>> neighbour = nonEmptyNeighbours.entrySet().iterator().next();
				Path path = new Path(neighbour.getKey().opposite());
				field.setContent(path.getStart());
				path.connect(neighbour.getKey(), neighbour.getValue().getContent());
				boolean advanced = false;
				do {
					advanced = advance(path, branches);
					createBranches(branches);
				} while(advanced);
				iterator = board.getEmptyFields().iterator();
			}
		}
		
		System.out.println(board);
	}

	private void createBranches(Collection<Path> branches) {
		while (!branches.isEmpty()) {
			Iterator<Path> iterator = branches.iterator();
			while (iterator.hasNext()) {
				Path branch = iterator.next();
				if(!advance(branch, branches)){
					iterator.remove();
				}
			}

		}
	}

	private Queue<Path> createSolutionPath() {
		int tries = 0;
		Queue<Path> branches;
		while (true) {
			tries++;
			try {
				branches = new ConcurrentLinkedQueue<Path>();
				Field<Section> currentStartPathField = start;
				Orientation startBorder = board
						.getBorders(currentStartPathField).iterator().next();
				Path startPath = new Path(startBorder.opposite()).go(AHEAD);
				currentStartPathField.setContent(startPath.getEnd());

				Field<Section> currentEndPathField = end;
				Orientation endBorder = board.getBorders(currentEndPathField)
						.iterator().next();
				Path endPath = new Path(endBorder.opposite()).go(AHEAD);
				currentEndPathField.setContent(endPath.getEnd());

				do {
					boolean advanced = false;
					
					if (advance(startPath, branches)) {
						if (tryConnect(startPath)) {
							break;
						}
						advanced = true;
					}

					if (advance(endPath, branches)) {
						if (tryConnect(endPath)) {
							break;
						}
						advanced = true;
					} 
					
					if (!advanced) {
						board.clear();
						branches.clear();
						throw new NoPathFoundException("no path found in "
								+ tries + " tries");
					}

				} while (true);
				break;
			} catch (NoPathFoundException e) {

				if (tries >= MAX_TRIES) {
					throw e;
				}
			}
		}
		return branches;
	}

	private boolean advance(Path path, Collection<Path> branches) {

		Collections.shuffle(turns);

		Field<Section> field = path.getEnd().getField();
		Map<Orientation, Field<Section>> neighbours = field.getNeighbours();
		Orientation currentDirection = path.getCurrentDirection();

		boolean advanced = false;
		Field<Section> neighbour;
		for (Direction direction : turns) {

			neighbour = neighbours.get(currentDirection.turn(direction));
			if (neighbour != null && neighbour.isEmpty()) {
				if (random.nextInt(5) == 0) {
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

	private boolean tryConnect(Path path) {

		Map<Orientation, Field<Section>> nonEmptyNeighbours = path.getEnd()
				.getField().getNonEmptyNeighbours();
		if (!nonEmptyNeighbours.isEmpty()) {
			Iterator<Entry<Orientation, Field<Section>>> iterator = nonEmptyNeighbours
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Orientation, Field<Section>> neighbour = iterator.next();


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
