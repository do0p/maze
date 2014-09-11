package at.brandl.games.maze;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Direction;
import at.brandl.games.commons.FieldContent;
import at.brandl.games.commons.Orientation;

public class Path {

	public static class IllegalPathExcption extends RuntimeException {

		private static final long serialVersionUID = -6275090835845428411L;

	}

	public static enum Target {
		START, END
	}

	public static class Section implements FieldContent<Section> {

		private final Map<Orientation, Section> neighbours = new HashMap<Orientation, Path.Section>();
		private final Map<Target, Section> targets = new HashMap<Target, Path.Section>();

		private Field<Section> field;

		Section() {

		}

		private Section addNeighbour(Orientation direction) {
			return addNeighbour(direction, new Section());

		}

		private Section addNeighbour(Orientation direction, Section section) {
			Section previous = neighbours.put(direction, section);
			if (previous != null) {
				throw new IllegalPathExcption();
			}
			section.neighbours.put(direction.opposite(), this);

			for (Entry<Target, Section> entry : targets.entrySet()) {
				if (section.targets.containsKey(entry)) {
					throw new IllegalStateException(
							"only one section can contain a given target");
				}
				section.setTarget(entry.getKey(), this);
			}

			for (Entry<Target, Section> entry : section.targets.entrySet()) {
				if (!targets.containsKey(entry.getKey())) {
					setTarget(entry.getKey(), section);
				}
			}
			return section;

		}

		public Section getNeighbour(Orientation direction) {
			return neighbours.get(direction);

		}


		public Field<Section> getField() {
			return field;
		}

		@SuppressWarnings("unchecked")
		public void setField(Field<? extends FieldContent<Section>> field) {
			this.field = (Field<Section>) field;

		}

		public Map<Orientation, Section> getNeighbours() {
			return neighbours;
		}
		
		public Section getTarget(Target target) {
			return targets.get(target);
		}


		public void setTarget(Target target, Section section) {
			targets.put(target, section);
			for (Section neighbour : neighbours.values()) {
				if (!neighbour.equals(section)) {
					neighbour.setTarget(target, this);
				}
			}
		}

		public boolean hasTarget(Target target) {
			return targets.containsKey(target) ;
		}

	}

	private final Section start;
	private Section end;
	private Orientation currentDirection;

	
	
	public Path(Orientation startingDirection) {
		this(startingDirection, new Section());
	}

	public Path(Orientation startingDirection, Section start) {
		this(startingDirection, start, start);
	}
	
	public Path(Orientation currentDirection, Section start, Section end) {
		this.currentDirection = currentDirection;
		this.start = start;
		this.end = end;
		
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
