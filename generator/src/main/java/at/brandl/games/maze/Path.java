package at.brandl.games.maze;

import java.util.HashMap;
import java.util.Map;

import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Direction;
import at.brandl.games.commons.FieldContent;
import at.brandl.games.commons.Orientation;

public class Path {

	public static class IllegalPathExcption extends RuntimeException {

		private static final long serialVersionUID = -6275090835845428411L;

	}

	public static class Section implements FieldContent<Section> {

		private final Map<Orientation, Section> neighbours = new HashMap<Orientation, Path.Section>();
		private final Path path;
		private Field<Section> field;

		Section(Path path) {
			this.path = path;
		}

		private Section addNeighbour(Orientation direction) {
			return addNeighbour(direction, new Section(path));

		}

		private Section addNeighbour(Orientation direction, Section section) {
			Section previous = neighbours.put(direction, section);
			if (previous != null) {
				throw new IllegalPathExcption();
			}
			section.neighbours.put(direction.opposite(), this);
			return section;

		}

		public Section getNeighbour(Orientation direction) {
			return neighbours.get(direction);

		}

		public Path getPath() {
			return path;
		}

		public Field<Section> getField() {
			return field;
		}

		public void setField(Field<? extends FieldContent<Section>> field) {
			this.field = (Field<Section>) field;
			
		}




	}

	private final Section start;
	private Section end;
	private Orientation currentDirection;

	public Path(Orientation startingDirection) {
		currentDirection = startingDirection;
		start = new Section(this);
		end = start;
	}

	public Path(Orientation startingDirection, Section section) {
		currentDirection = startingDirection;
		start = section;
		end = start;
	}

	public Path go(Direction direction) {
		currentDirection = currentDirection.turn(direction);
		end = end.addNeighbour(currentDirection);
		return this;
	}

	public Path createPath(Direction direction) {
		Orientation orientation = currentDirection.turn(direction);
		return new Path(orientation, end.addNeighbour(orientation));
	}

	public Section getStart() {
		return start;
	}

	public Section getEnd() {
		return end;
	}

	public void connect(Orientation direction, Section section) {
		end.addNeighbour(direction, section);

	}

	public Orientation getCurrentDirection() {
		return currentDirection;
	}

}
