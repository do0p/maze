package at.brandl.games.commons;

import static at.brandl.games.commons.Direction.EAST;
import static at.brandl.games.commons.Direction.NORTH;
import static at.brandl.games.commons.Direction.SOUTH;
import static at.brandl.games.commons.Direction.WEST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;



public class Board<T> {
	public static class Field<T> {

		private final Map<Direction, Board.Field<T>> neighbours = new HashMap<Direction, Board.Field<T>>();
		private final int row;
		private final int column;
		private T content;

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

		public void setContent(T content) {
			if(!isEmpty()){
				throw new IllegalStateException("field already contains a section." );
			}
			this.content = content;
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

		public Map<Direction, Field<T>> getNeighbours() {
			return neighbours ;
		}
		
		public Map<Direction, Field<T>> getNonEmptyNeighbours() {
			Map<Direction, Field<T>> nonEmptyNeighbours = new HashMap<Direction, Board.Field<T>>();
			for(Entry<Direction, Field<T>> entry : neighbours.entrySet()) {
				if(!entry.getValue().isEmpty()){
					nonEmptyNeighbours.put(entry.getKey(), entry.getValue());
				}
			}
			return nonEmptyNeighbours;
		}
		
		private void addNeighbour(Direction direction, Field<T> field) {
			Field<T> previous = neighbours.put(direction, field);
			if (previous != null) {
				throw new IllegalBoardExcption();
			}
			field.neighbours.put(direction.opposite(), this);

		}

	}

	private final Map<Integer, Map<Integer, Field<T>>> fields = new HashMap<Integer, Map<Integer, Field<T>>>();

	public Board(int numColumns, int numRows) {

		for (int row = 0; row < numRows; row++) {
			Map<Integer, Field<T>> rowValues = new HashMap<Integer, Board.Field<T>>();
			for (int column = 0; column < numColumns; column++) {
				Field<T> field = new Field<T>(row, column);
				if(column > 0) {
					field.addNeighbour(WEST, rowValues.get(column - 1));
				}
				if(row > 0) {
					field.addNeighbour(NORTH, fields.get(row -1).get(column));
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
		for (Map<Integer, Field<T>> row : fields.values()) {
			for (Field<T> field : row.values()) {
				board.append("|");
				board.append(field);
			}
			board.append("|\n");
		}
		return board.toString();
	}

	public Collection<Direction> getBorders(Field<T> field) {
		Collection<Direction> borders = new ArrayList<Direction>();
		if (field.getRow() == 0) {
			borders.add(NORTH);
		}

		if (field.getRow() == fields.size() - 1) {
			borders.add(EAST);
		}
		if (field.getColumn() == fields.get(0).size() - 1) {
			borders.add(SOUTH);
		}
		if (field.getColumn() == 0) {
			borders.add(WEST);
		}
		return borders;
	}
	


	
}
