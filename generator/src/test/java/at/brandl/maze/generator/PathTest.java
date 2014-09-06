package at.brandl.maze.generator;

import static at.brandl.maze.generator.Direction.EAST;
import static at.brandl.maze.generator.Direction.NORTH;
import static at.brandl.maze.generator.Direction.SOUTH;
import static at.brandl.maze.generator.Direction.WEST;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import at.brandl.maze.generator.Path.Section;

public class PathTest {

	@Test
	public void start() {
		Section first = new Section();
		Path path = new Path(NORTH, first);
		assertSame(first, path.getStart());

		path.ahead();
		assertSame(first, path.getStart());
	}

	@Test
	public void end() {
		Section first = new Section();
		Path path = new Path(NORTH, first);
		assertSame(first, path.getEnd());

		path.ahead();
		assertSame(first.getNeighbour(NORTH), path.getEnd());
	}

	@Test
	public void neighbours() {
		Path path = new Path(NORTH, new Section());
		Section start = path.getStart();
		assertNull(start.getNeighbour(NORTH));
		assertNull(start.getNeighbour(EAST));
		assertNull(start.getNeighbour(WEST));
		assertNull(start.getNeighbour(SOUTH));

		Section end = path.ahead().getEnd();
		assertSame(end, start.getNeighbour(NORTH));
		assertNull(start.getNeighbour(EAST));
		assertNull(start.getNeighbour(WEST));
		assertNull(start.getNeighbour(SOUTH));

		assertSame(start, end.getNeighbour(SOUTH));
		assertNull(end.getNeighbour(EAST));
		assertNull(end.getNeighbour(WEST));
		assertNull(end.getNeighbour(NORTH));

		Section middle = end;
		end = path.turnLeft().getEnd();
		assertSame(start, middle.getNeighbour(SOUTH));
		assertSame(end, middle.getNeighbour(WEST));
		assertNull(middle.getNeighbour(EAST));
		assertNull(middle.getNeighbour(NORTH));

		assertSame(middle, end.getNeighbour(EAST));
		assertNull(end.getNeighbour(SOUTH));
		assertNull(end.getNeighbour(WEST));
		assertNull(end.getNeighbour(NORTH));

	}


	@Test
	public void junction() {
		Path path = new Path(NORTH, new Section());

		Path leftPath = path.createPathLeft();
		assertSame(path.getEnd(), leftPath.getStart().getNeighbour(EAST));
	}
	
	@Test(expected = IllegalPathExcption.class)
	public void junctionOnExistingJUnction() {
		Path path = new Path(NORTH, new Section());

		path.createPathLeft();
		path.createPathLeft();
	}
	
	@Test(expected = IllegalPathExcption.class)
	public void pathOnExistingJunction() {
		Path path = new Path(NORTH, new Section());

		path.createPathLeft();
		path.turnLeft();
	}
	
	@Test
	public void connect() {
		Path path = new Path(NORTH, new Section());
		Section section = new Section();
		path.connect(NORTH, section);
		assertSame(section, path.getEnd().getNeighbour(NORTH));
		assertSame(path.getEnd(), section.getNeighbour(SOUTH));
	}

}
