package at.brandl.games.commons;

import static at.brandl.games.commons.Orientation.EAST;
import static at.brandl.games.commons.Orientation.NORTH;
import static at.brandl.games.commons.Orientation.SOUTH;
import static at.brandl.games.commons.Orientation.WEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Board.FieldNotFoundException;

public class BoardTest {

	private static class Content implements FieldContent<Content> {

		private Field<? extends FieldContent<Content>> field;

		public Field<? extends FieldContent<Content>> getField() {
			return field;
		}

		public void setField(Field<? extends FieldContent<Content>> field) {
			this.field = field;
			
		}


		
	}
	
	private static final int WIDTH = 10;
	private static final int HEIGHT = 12;
	private Board<Content> board;
	
	

	@Before
	public void setUp() {
		board = new Board<Content>(WIDTH, HEIGHT);
	}

	@Test
	public void field() {
		for (int row = 0; row < HEIGHT; row++) {
			for (int column = 0; column < WIDTH; column++) {
				Field<Content> field = board.getField(row, column);
				assertEquals(row, field.getRow());
				assertEquals(column, field.getColumn());

			}
		}
	}

	@Test
	public void neighbours() {
		int row = 1;
		int column = 1;
		Map<Orientation, Field<Content>> neighbours = board.getField(row, column)
				.getNeighbours();

		assertEquals(4, neighbours.size());
		assertNorthNeighbour(neighbours, row, column);
		assertEastNeighbour(neighbours, row, column);
		assertSouthNeighbour(neighbours, row, column);
		assertWestNeighbour(neighbours, row, column);
	}

	@Test
	public void neighboursInNorthWest() {
		int row = 0;
		int column = 0;
		Map<Orientation, Field<Content>> neighbours = board.getField(row, column)
				.getNeighbours();

		assertEquals(2, neighbours.size());
		assertEastNeighbour(neighbours, row, column);
		assertSouthNeighbour(neighbours, row, column);

	}

	@Test
	public void neighboursInSouthEeast() {
		int row = HEIGHT - 1;
		int column = WIDTH - 1;
		Map<Orientation, Field<Content>> neighbours = board.getField(row, column)
				.getNeighbours();

		assertEquals(2, neighbours.size());
		assertNorthNeighbour(neighbours, row, column);
		assertWestNeighbour(neighbours, row, column);
	}

	@Test 
	public void nonEmptyNeighbours() {
		int row = 1;
		int column = 1;
		Field<Content> field = board.getField(row, column);
		Map<Orientation, Field<Content>> neighbours = field
				.getNeighbours();

		Field<Content> northNeighbour = neighbours.get(NORTH);
		northNeighbour.setContent(new Content());
		
		Map<Orientation, Field<Content>> nonEmptyNeighbours = field.getNonEmptyNeighbours();
		assertEquals(1, nonEmptyNeighbours.size());
		assertNorthNeighbour(nonEmptyNeighbours, row, column);
		
	}
	
	@Test
	public void section() {
		Field<Content> field = board.getField(0, 0);
		assertTrue(field.isEmpty());
		Content content = new Content();
		field.setContent(content);
		assertFalse(field.isEmpty());
		assertSame(content, field.getContent());
	}

	@Test(expected = IllegalStateException.class)
	public void setSectionTwice() {
		Field<Content> field = board.getField(0, 0);
		field.setContent(new Content());
		field.setContent(new Content());
	}

	@Test(expected = FieldNotFoundException.class)
	public void fieldNotFound() {
		board.getField(HEIGHT, WIDTH);
	}

	@Test
	public void northWest() {
		Collection<Orientation> borders = board.getBorders(board.getField(0, 0));
		assertEquals(2, borders.size());
		assertTrue(borders.contains(NORTH));
		assertTrue(borders.contains(WEST));
	}

	@Test
	public void southEast() {
		Collection<Orientation> borders = board.getBorders(board.getField(
				HEIGHT - 1, WIDTH - 1));
		assertEquals(2, borders.size());
		assertTrue(borders.contains(EAST));
		assertTrue(borders.contains(SOUTH));
	}

	private void assertWestNeighbour(Map<Orientation, Field<Content>> neighbours, int row,
			int column) {
		Field<Content> westNeighbour = neighbours.get(WEST);
		assertEquals(row, westNeighbour.getRow());
		assertEquals(column - 1, westNeighbour.getColumn());
	}

	private void assertNorthNeighbour(Map<Orientation, Field<Content>> neighbours,
			int row, int column) {
		Field<Content> northNeighbour = neighbours.get(NORTH);
		assertEquals(row - 1, northNeighbour.getRow());
		assertEquals(column, northNeighbour.getColumn());
	}

	private void assertSouthNeighbour(Map<Orientation, Field<Content>> neighbours,
			int row, int column) {
		Field<Content> southNeighbour = neighbours.get(SOUTH);
		assertEquals(row + 1, southNeighbour.getRow());
		assertEquals(column, southNeighbour.getColumn());
	}

	private void assertEastNeighbour(Map<Orientation, Field<Content>> neighbours, int row,
			int column) {
		Field<Content> eastNeighbour = neighbours.get(EAST);
		assertEquals(row, eastNeighbour.getRow());
		assertEquals(column + 1, eastNeighbour.getColumn());
	}
}
