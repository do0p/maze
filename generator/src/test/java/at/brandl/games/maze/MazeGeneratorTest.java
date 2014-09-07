package at.brandl.games.maze;

import org.junit.Test;

import at.brandl.games.commons.Board;
import at.brandl.games.maze.MazeGenerator;

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
