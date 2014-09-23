package at.brandl.games.maze.generator;

import static at.brandl.games.maze.generator.Path.Target.END;
import static at.brandl.games.maze.generator.Path.Target.START;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import at.brandl.games.commons.Board;
import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Orientation;
import at.brandl.games.maze.generator.Path.Section;
import at.brandl.games.maze.generator.Path.Target;

public class MazeGeneratorTest {

	private static interface RowAndColumnAsserter {
		void assertRowAndColumn(int row, int column);
	}

	private static interface FieldAsserter {
		void assertField(Field<Section> field);
	}

	private static interface SectionAsserter {
		void assertSection(Section section);
	}

	private static final int ROWS = 24;
	private static final int COLUMNS = 28;
	private Board<Section> board;
	private MazeGenerator mazeGenerator;

	@Before
	public void setUp() {

		board = new Board<Section>(COLUMNS, ROWS);
		mazeGenerator = new MazeGenerator(board);
		mazeGenerator.generate();

	}

	@Test
	public void rowAndColumn() {
		forEeachRowAndColumn(new RowAndColumnAsserter() {
			public void assertRowAndColumn(int row, int column) {

				Field<Section> field = board.getField(row, column);
				assertEquals(row, field.getRow());
				assertEquals(column, field.getColumn());

			}
		});
	}

	@Test
	public void fieldNotEmpty() {
		forEeachField(new FieldAsserter() {
			public void assertField(Field<Section> field) {

				assertFalse(field.isEmpty());

			}
		});
	}

	@Test
	public void fieldNeighbourCount() {
		forEeachField(new FieldAsserter() {
			public void assertField(Field<Section> field) {

				int neighboursCount = field.getNeighbours().size();
				int bordersCount = board.getBorders(field).size();
				if (bordersCount == 2) {
					assertEquals(2, neighboursCount);
				} else if (bordersCount == 1) {
					assertEquals(3, neighboursCount);
				} else {
					assertEquals(4, neighboursCount);
				}

			}
		});
	}

	@Test
	public void nonEmptyFieldNeighbours() {
		forEeachField(new FieldAsserter() {
			public void assertField(Field<Section> field) {

				assertEquals(field.getNeighbours(),
						field.getNonEmptyNeighbours());

			}
		});
	}

	@Test
	public void fieldAndNeighbourRelation() {
		forEeachField(new FieldAsserter() {
			public void assertField(Field<Section> field) {

				assertEquals(field.getNeighbours(),
						field.getNonEmptyNeighbours());

			}
		});
	}

	@Test
	public void fieldAndSectionRelation() {
		forEeachField(new FieldAsserter() {
			public void assertField(Field<Section> field) {

				for (Entry<Orientation, Field<Section>> neighbour : field
						.getNeighbours().entrySet()) {
					Field<Section> neighbourField = neighbour.getValue();
					Orientation orientation = neighbour.getKey();
					assertEquals(
							field,
							neighbourField.getNeighbours().get(
									orientation.opposite()));
				}

			}
		});
	}

	@Test
	public void sectionAndNeighbourRelation() {
		forEeachSection(new SectionAsserter() {
			public void assertSection(Section section) {

				for (Entry<Orientation, Section> neighbour : section
						.getNeighbours().entrySet()) {
					Section neighbourSection = neighbour.getValue();
					Orientation orientation = neighbour.getKey();
					assertEquals(
							section,
							neighbourSection.getNeighbours().get(
									orientation.opposite()));
				}
			}
		});
	}

	@Test
	public void pathToStart() {
		forEeachSection(new SectionAsserter() {
			public void assertSection(Section section) {

				Field<Section> startField = getFinalTarget(section, START);
				assertEquals(mazeGenerator.getStart(), startField);

			}
		});
	}

	@Test
	public void pathToEnd() {
		forEeachSection(new SectionAsserter() {
			public void assertSection(Section section) {

				Field<Section> endField = getFinalTarget(section, END);
				assertEquals(mazeGenerator.getEnd(), endField);

			}

		});
	}

	private void forEeachRowAndColumn(RowAndColumnAsserter asserter) {
		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				asserter.assertRowAndColumn(row, column);
			}
		}
	}

	private void forEeachField(FieldAsserter asserter) {
		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				asserter.assertField(board.getField(row, column));
			}
		}
	}

	private void forEeachSection(SectionAsserter asserter) {
		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				asserter.assertSection(board.getField(row, column).getContent());
			}
		}
	}

	private Field<Section> getFinalTarget(Section section, Target target) {
		Section targetSection = section.getTarget(target);
		while (!section.equals(targetSection)) {
			section = targetSection;
			targetSection = section.getTarget(target);
		}
		return targetSection.getField();
	}

}
