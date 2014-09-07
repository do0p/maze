package at.brandl.games.commons;

import static at.brandl.games.commons.Direction.EAST;
import static at.brandl.games.commons.Direction.NORTH;
import static at.brandl.games.commons.Direction.SOUTH;
import static at.brandl.games.commons.Direction.WEST;
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

	private static final int WIDTH = 10;
	private static final int HEIGHT = 12;
	private Board<Object> board;

	@Before
	public void setUp() {
		board = new Board<Object>(WIDTH, HEIGHT);
	}

	@Test
	public void field() {
		for (int row = 0; row < HEIGHT; row++) {
			for (int column = 0; column < WIDTH; column++) {
				Field<Object> field = board.getField(row, column);
				assertEquals(row, field.getRow());
				assertEquals(column, field.getColumn());

			}
		}
	}

	@Test
	public void neighbours() {
		int row = 1;
		int column = 1;
		Map<Direction, Field<Object>> neighbours = board.getField(row, column)
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
		Map<Direction, Field<Object>> neighbours = board.getField(row, column)
				.getNeighbours();

		assertEquals(2, neighbours.size());
		assertEastNeighbour(neighbours, row, column);
		assertSouthNeighbour(neighbours, row, column);

	}

	@Test
	public void neighboursInSouthEeast() {
		int row = HEIGHT - 1;
		int column = WIDTH - 1;
		Map<Direction, Field<Object>> neighbours = board.getField(row, column)
				.getNeighbours();

		assertEquals(2, neighbours.size());
		assertNorthNeighbour(neighbours, row, column);
		assertWestNeighbour(neighbours, row, column);
	}

	@Test 
	public void nonEmptyNeighbours() {
		int row = 1;
		int column = 1;
		Field<Object> field = board.getField(row, column);
		Map<Direction, Field<Object>> neighbours = field
				.getNeighbours();

		Field<Object> northNeighbour = neighbours.get(NORTH);
		northNeighbour.setContent(new Object());
		
		Map<Direction, Field<Object>> nonEmptyNeighbours = field.getNonEmptyNeighbours();
		assertEquals(1, nonEmptyNeighbours.size());
		assertNorthNeighbour(nonEmptyNeighbours, row, column);
		
	}
	
	@Test
	public void section() {
		Field<Object> field = board.getField(0, 0);
		assertTrue(field.isEmpty());
		Object content = new Object();
		field.setContent(content);
		assertFalse(field.isEmpty());
		assertSame(content, field.getContent());
	}

	@Test(expected = IllegalStateException.class)
	public void setSectionTwice() {
		Field<Object> field = board.getField(0, 0);
		field.setContent(new Object());
		field.setContent(new Object());
	}

	@Test(expected = FieldNotFoundException.class)
	public void fieldNotFound() {
		board.getField(HEIGHT, WIDTH);
	}

	@Test
	public void northWest() {
		Collection<Direction> borders = board.getBorders(board.getField(0, 0));
		assertEquals(2, borders.size());
		assertTrue(borders.contains(NORTH));
		assertTrue(borders.contains(WEST));
	}

	@Test
	public void southEast() {
		Collection<Direction> borders = board.getBorders(board.getField(
				HEIGHT - 1, WIDTH - 1));
		assertEquals(2, borders.size());
		assertTrue(borders.contains(EAST));
		assertTrue(borders.contains(SOUTH));
	}

	private void assertWestNeighbour(Map<Direction, Field<Object>> neighbours, int row,
			int column) {
		Field<Object> westNeighbour = neighbours.get(WEST);
		assertEquals(row, westNeighbour.getRow());
		assertEquals(column - 1, westNeighbour.getColumn());
	}

	private void assertNorthNeighbour(Map<Direction, Field<Object>> neighbours,
			int row, int column) {
		Field<Object> northNeighbour = neighbours.get(NORTH);
		assertEquals(row - 1, northNeighbour.getRow());
		assertEquals(column, northNeighbour.getColumn());
	}

	private void assertSouthNeighbour(Map<Direction, Field<Object>> neighbours,
			int row, int column) {
		Field<Object> southNeighbour = neighbours.get(SOUTH);
		assertEquals(row + 1, southNeighbour.getRow());
		assertEquals(column, southNeighbour.getColumn());
	}

	private void assertEastNeighbour(Map<Direction, Field<Object>> neighbours, int row,
			int column) {
		Field<Object> eastNeighbour = neighbours.get(EAST);
		assertEquals(row, eastNeighbour.getRow());
		assertEquals(column + 1, eastNeighbour.getColumn());
	}
}
