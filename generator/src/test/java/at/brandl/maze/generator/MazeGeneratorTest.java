package at.brandl.maze.generator;

import org.junit.Test;

import at.brandl.games.commons.Board;

public class MazeGeneratorTest {

	@Test
	public void test() {
		Board board = new Board(10, 10);
		MazeGenerator mazeGenerator = new MazeGenerator(board);
		mazeGenerator.setStart(0, 5);
		mazeGenerator.setEnd(9, 5);
		mazeGenerator.generate();
	}

}
