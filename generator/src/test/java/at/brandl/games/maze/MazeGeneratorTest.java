package at.brandl.games.maze;

import org.junit.Test;

import at.brandl.games.commons.Board;

import at.brandl.games.maze.Path.Section;

public class MazeGeneratorTest {

	private static final int ROWS = 40;
	private static final int COLUMNS = 40;

	@Test
	public void test() {
		Board<Section> board = new Board<Section>(ROWS, COLUMNS);
		MazeGenerator mazeGenerator = new MazeGenerator(board);
		mazeGenerator.setStart(0, COLUMNS / 2);
		mazeGenerator.setEnd(ROWS - 1, COLUMNS / 2);

		mazeGenerator.generate();
	}

}
