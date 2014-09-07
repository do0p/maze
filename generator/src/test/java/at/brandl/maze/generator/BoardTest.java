package at.brandl.maze.generator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import at.brandl.maze.generator.Board.Field;

public class BoardTest {

	private static final int WIDTH = 10;
	private static final int HEIGHT = 12;
	private Board board;

	@Before
	public void setUp() {
		board = new Board(WIDTH, HEIGHT);
	}

	@Test
	public void field() {
		for (int row = 0; row < HEIGHT; row++) {
			for (int column = 0; column < WIDTH; column++) {
				Field field = board.getField(row, column);
				assertTrue(field.isEmpty());
			}
		}
		
	}


	
}
