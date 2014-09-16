package at.brandl.games.commons;

import static at.brandl.games.commons.Orientation.EAST;
import static at.brandl.games.commons.Orientation.NORTH;
import static at.brandl.games.commons.Orientation.SOUTH;
import static at.brandl.games.commons.Orientation.WEST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;



public class Board<T extends FieldContent<T>> {
	


	public static class FieldNotFoundException extends RuntimeException {

		public FieldNotFoundException(int row, int column) {
			super("No field with row " + row + " and column " + column + ".");
		}

		private static final long serialVersionUID = 9029363192147747391L;

	}

	public static class IllegalBoardExcption extends RuntimeException {

		private static final long serialVersionUID = 5950890814540190627L;

	}

	public static class Field<T extends FieldContent<T>> {

		private final Map<Orientation, Board.Field<T>> neighbours = new HashMap<Orientation, Board.Field<T>>();
		private final int row;
		private final int column;
		private T content;
		private boolean visited;

		public boolean isVisited() {
			return visited;
		}

		public void setVisited(boolean visited) {
			this.visited = visited;
		}

		private Field(int row, int column) {
			this.row = row;
			this.column = column;
		}

		public boolean isEmpty() {
			return content == null;
		}

		public T getContent() {
			return content;
		}

		public void clear() {
			content = null;
		}
		
		public void setContent(T content) {
			if (!isEmpty()) {
				throw new IllegalStateException(
						"field already contains a section.");
			}
			this.content = content;
			content.setField(this);
		}

		@Override
		public String toString() {
			return isEmpty() ? "/" : "X";
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}


		public Map<Orientation, Field<T>> getNeighbours() {

			return neighbours;
		}

		public Map<Orientation, Field<T>> getNonEmptyNeighbours() {
			return getNeighbours(false);
		}
		
		public Map<Orientation, Field<T>> getEmptyNeighbours() {
			return getNeighbours(true);
		}

		private Map<Orientation, Field<T>> getNeighbours(boolean empty) {
			Map<Orientation, Field<T>> nonEmptyNeighbours = new HashMap<Orientation, Board.Field<T>>();
			for (Entry<Orientation, Field<T>> entry : neighbours.entrySet()) {

				if (entry.getValue().isEmpty() == empty) {
					nonEmptyNeighbours.put(entry.getKey(), entry.getValue());
				}
			}
			return nonEmptyNeighbours;
		}

		private void addNeighbour(Orientation orientation, Field<T> field) {
			Field<T> previous = neighbours.put(orientation, field);

			if (previous != null) {
				throw new IllegalBoardExcption();
			}
			field.neighbours.put(orientation.opposite(), this);

		}

		

	}

	private final Map<Integer, Map<Integer, Field<T>>> fields = new HashMap<Integer, Map<Integer, Field<T>>>();

	public Board(int numColumns, int numRows) {

		for (int row = 0; row < numRows; row++) {
			Map<Integer, Field<T>> rowValues = new HashMap<Integer, Board.Field<T>>();
			for (int column = 0; column < numColumns; column++) {
				Field<T> field = new Field<T>(row, column);
				if (column > 0) {
					field.addNeighbour(WEST, rowValues.get(column - 1));
				}
				if (row > 0) {
					field.addNeighbour(NORTH, fields.get(row - 1).get(column));
				}
				rowValues.put(column, field);
			}
			fields.put(row, rowValues);
		}
	}

	public Field<T> getField(int row, int column) {
		Field<T> field;
		try {
			field = fields.get(row).get(column);
		} catch (NullPointerException e) {
			field = null;
		}
		if (field == null) {
			throw new FieldNotFoundException(row, column);
		}
		return field;
	}

	@Override
	public String toString() {
		StringBuilder board = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			Map<Integer, Field<T>> row = fields.get(i);
			for (int j = 0; j < row.size(); j++) {
				board.append("|");
				board.append(row.get(j));
			}
			board.append("|\n");
		}
		return board.toString();
	}

	public Collection<Orientation> getBorders(Field<T> field) {
		Collection<Orientation> borders = new ArrayList<Orientation>();
		if (field.getRow() == 0) {
			borders.add(NORTH);
		}

		if (field.getColumn() == fields.get(0).size() - 1) {
			borders.add(EAST);
		}
		
		if (field.getRow() == fields.size() - 1) {
			borders.add(SOUTH);
		}

		if (field.getColumn() == 0) {
			borders.add(WEST);
		}
		return borders;
	}


	public void clear() {
		for (Map<Integer, Field<T>> row : fields.values()) {
			for (Field<T> field : row.values()) {
				field.clear();
			}
		}
	}

	public Collection<Field<T>> getEmptyFields() {
		
		Collection<Field<T>> emptyFields = new LinkedList<Board.Field<T>>();
		for (Map<Integer, Field<T>> row : fields.values()) {
			for (Field<T> field : row.values()) {
				if(field.isEmpty()) {
					emptyFields.add(field);
				}
			}
		}
		return emptyFields;
	}
	

}
