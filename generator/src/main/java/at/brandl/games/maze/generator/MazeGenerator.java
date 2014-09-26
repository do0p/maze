package at.brandl.games.maze.generator;

import static at.brandl.games.commons.Direction.AHEAD;
import static at.brandl.games.commons.Direction.LEFT;
import static at.brandl.games.commons.Direction.RIGHT;
import static at.brandl.games.maze.generator.Path.Target.END;
import static at.brandl.games.maze.generator.Path.Target.START;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import at.brandl.games.commons.Board;
import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Direction;
import at.brandl.games.commons.Orientation;
import at.brandl.games.maze.generator.Path.Section;

public class MazeGenerator {

	public static interface ProgressListener {
		public void updateProgress(int progress);
	}
	
	private int averageNumberOfStepsToNextBranch = 15;

	private final List<Direction> turns = Arrays.asList(LEFT, AHEAD, RIGHT);
	private final Map<Path, Integer> pathLengths = new HashMap<>();
	private final HashSet<Section> endpoints = new HashSet<>();

	private final Board<Section> board;
	private final Random random;
	private Field<Section> start;
	private Field<Section> end;
	
	private volatile int progressPcnt = 0;

	private Collection<ProgressListener> listeners = new ArrayList<>();

	public MazeGenerator(Board<Section> board) {
		this.board = board;
		start = board.getField(0, 0);
		random = new Random(System.currentTimeMillis());
	}

	public int getAverageNumberOfStepsToNextBranch() {
		return averageNumberOfStepsToNextBranch;
	}

	public void setAverageNumberOfStepsToNextBranch(
			int averageNumberOfStepsToNextBranch) {
		this.averageNumberOfStepsToNextBranch = averageNumberOfStepsToNextBranch;
	}

	public Field<Section> getStart() {
		return start;
	}

	public Field<Section> getEnd() {
		return end;
	}

	public void generate() {

		Collection<Path> branches = createSolutionPath();

		createBranches(branches);

		fillEmptyFields(branches);

		setLongestPath();

	}

	public void addProgressListener(ProgressListener listener) {
		listeners .add(listener);
	}
	
	private void setLongestPath() {
		findAllEndpoints();
		Path longestPath = findLongestPath();
		setStartAndEnd(longestPath);
	}

	private void setStartAndEnd(Path longestPath) {
		Section startSection = longestPath.getStart();
		startSection.setTarget(START, startSection);
		start = startSection.getField();
		board.setStart(start);
		
		Section endSection = longestPath.getEnd();
		endSection.setTarget(END, endSection);
		end = longestPath.getEnd().getField();
		board.setEnd(end);
	}

	private Path findLongestPath() {
		
		int numEndpoints = endpoints.size();
		int numPaths = numEndpoints * (numEndpoints - 1) / 2;
		
		Iterator<Section> iterator = endpoints.iterator();
		Path longestPath = new Path(Orientation.NORTH, start.getContent());
		int progress = 0;
		while (iterator.hasNext()) {
			Section startPoint = iterator.next();
			iterator.remove();
			for (Section endPoint : endpoints) {

				Path path = new Path(Orientation.NORTH, startPoint, endPoint);
				if (pathLengths.containsKey(path)) {
					continue;
				}
				startPoint.setTarget(START, startPoint);
				Section target = endPoint.getTarget(START);
				int length = 0;
				while (!target.equals(startPoint)) {
					length++;
					target = target.getTarget(START);
				}
				path.setLength(length);
				if (length > longestPath.getLength()) {
					longestPath = path;
				}
				
				progressPcnt = ++progress * 100 / numPaths;
				notifyProgress(progressPcnt);
			}
		}
		return longestPath;
	}

	private void findAllEndpoints() {
		for (Field<Section> field : board.getFields()) {
			Section section = field.getContent();
			if (section.getNeighbours().size() == 1) {
				endpoints.add(section);
			}
		}
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

	private Collection<Path> createSolutionPath() {

		Path path = new Path(Orientation.EAST);
		start.setContent(path.getStart());
		ConcurrentLinkedQueue<Path> concurrentLinkedQueue = new ConcurrentLinkedQueue<Path>();
		concurrentLinkedQueue.add(path);
		return concurrentLinkedQueue;

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
		return random.nextInt(averageNumberOfStepsToNextBranch) == 0;
	}

	public int getProgressPcnt() {
		return progressPcnt;
	}
	
	private void notifyProgress(int progress) {
		for(ProgressListener listener : listeners) {
			listener.updateProgress(progress);
		}
	}

}
