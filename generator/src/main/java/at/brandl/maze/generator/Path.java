package at.brandl.maze.generator;

import java.util.HashMap;
import java.util.Map;

public class Path {

	public static class Section {

		private final Map<Direction, Section> neighbours = new HashMap<Direction, Path.Section>();

		private Section addNeighbour(Direction direction) {
			return addNeighbour(direction, new Section());

		}

		private Section addNeighbour(Direction direction, Section section) {
			Section previous = neighbours.put(direction, section);
			if (previous != null) {
				throw new IllegalPathExcption();
			}
			section.neighbours.put(direction.opposite(), this);
			return section;

		}

		public Section getNeighbour(Direction direction) {
			return neighbours.get(direction);

		}

	}

	private final Section start;
	private Section end;
	private Direction currentDirection;

	public Path(Direction startingDirection, Section section) {
		currentDirection = startingDirection;
		start = section;
		end = start;
	}

	public Path turnLeft() {
		currentDirection = currentDirection.left();
		end = end.addNeighbour(currentDirection);
		return this;
	}

	public Path turnRight() {
		currentDirection = currentDirection.right();
		end = end.addNeighbour(currentDirection);
		return this;
	}

	public Path ahead() {
		end = end.addNeighbour(currentDirection);
		return this;
	}

	public Path createPathLeft() {
		Direction direction = currentDirection.left();
		return new Path(direction, end.addNeighbour(direction));
	}

	public Path createPathRight() {
		Direction direction = currentDirection.right();
		return new Path(direction, end.addNeighbour(direction));
	}

	public Path createPathAhead() {
		return new Path(currentDirection, end.addNeighbour(currentDirection));
	}

	public Section getStart() {
		return start;
	}

	public Section getEnd() {
		return end;
	}

	public void connect(Direction direction, Section section) {
		end.addNeighbour(direction, section);

	}

}
