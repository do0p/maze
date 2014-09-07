package at.brandl.maze.generator;

import java.util.HashMap;
import java.util.Map;

import at.brandl.maze.generator.Path.Section;

public class Board {
	public static class Field {

		private Section section;

		public boolean isEmpty() {
			return section == null;
		}

		public Section getSection() {
			return section;
		}

		public void setSection(Section section) {
			this.section = section;
		}
		
		@Override
		public String toString() {
			return isEmpty() ? "/" : "X";
		}

	}

	private final Map<Integer, Map<Integer, Field>> fields = new HashMap<Integer, Map<Integer, Field>>();

	public Board(int numColumns, int numRows) {

		for (int i = 0; i < numRows; i++) {
			Map<Integer, Field> row = new HashMap<Integer, Board.Field>();
			for (int j = 0; j < numColumns; j++) {
				row.put(j, new Field());
			}
			fields.put(i, row);
		}
	}

	public Field getField(int row, int column) {
		return fields.get(row).get(column);
	}
	
	@Override
	public String toString() {
		StringBuilder board = new StringBuilder();
		for(Map<Integer, Field> row : fields.values()) {
			for(Field field : row.values()) {
				board.append("|");
				board.append(field);
			}
			board.append("|\n");
		}
		return board.toString();
	}
}
