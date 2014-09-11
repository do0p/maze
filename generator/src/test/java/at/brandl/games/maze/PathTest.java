package at.brandl.games.maze;

import static at.brandl.games.commons.Direction.AHEAD;
import static at.brandl.games.commons.Direction.LEFT;
import static at.brandl.games.commons.Direction.RIGHT;
import static at.brandl.games.commons.Orientation.EAST;
import static at.brandl.games.commons.Orientation.NORTH;
import static at.brandl.games.commons.Orientation.SOUTH;
import static at.brandl.games.commons.Orientation.WEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import at.brandl.games.maze.Path.IllegalPathExcption;
import at.brandl.games.maze.Path.Section;

public class PathTest {

	@Test
	public void start() {
		Section first = new Section();
		Path path = new Path(NORTH, first);
		assertSame(first, path.getStart());

		path.go(AHEAD);
		assertSame(first, path.getStart());
	}

	@Test
	public void end() {
		Section first = new Section();
		Path path = new Path(NORTH, first);
		assertSame(first, path.getEnd());

		path.go(AHEAD);
		assertSame(first.getNeighbour(NORTH), path.getEnd());
	}

	@Test
	public void neighbours() {
		Path path = new Path(NORTH);
		Section start = path.getStart();
		assertNull(start.getNeighbour(NORTH));
		assertNull(start.getNeighbour(EAST));
		assertNull(start.getNeighbour(WEST));
		assertNull(start.getNeighbour(SOUTH));

		Section end = path.go(AHEAD).getEnd();
		assertSame(end, start.getNeighbour(NORTH));
		assertNull(start.getNeighbour(EAST));
		assertNull(start.getNeighbour(WEST));
		assertNull(start.getNeighbour(SOUTH));

		assertSame(start, end.getNeighbour(SOUTH));
		assertNull(end.getNeighbour(EAST));
		assertNull(end.getNeighbour(WEST));
		assertNull(end.getNeighbour(NORTH));

		Section middle = end;
		end = path.go(LEFT).getEnd();
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
		Path path = new Path(NORTH);

		Path leftPath = path.createPath(LEFT);
		assertSame(path.getEnd(), leftPath.getStart().getNeighbour(EAST));
	}
	
	@Test(expected = IllegalPathExcption.class)
	public void junctionOnExistingJUnction() {
		Path path = new Path(NORTH);

		path.createPath(LEFT);
		path.createPath(LEFT);
	}
	
	@Test(expected = IllegalPathExcption.class)
	public void pathOnExistingJunction() {
		Path path = new Path(NORTH);

		path.createPath(LEFT)		;
		path.go(LEFT);
	}
	
	@Test
	public void connect() {
		Path path = new Path(NORTH);
		Section section = new Section();
		path.connect(NORTH, section);
		assertSame(section, path.getEnd().getNeighbour(NORTH));
		assertSame(path.getEnd(), section.getNeighbour(SOUTH));

	}
	
	@Test 
	public void currentDirection()  {
		Path path = new Path(NORTH);
		assertEquals(NORTH, path.getCurrentDirection());
		assertEquals(NORTH, path.go(AHEAD).getCurrentDirection());
		assertEquals(WEST, path.go(LEFT).getCurrentDirection());
		assertEquals(NORTH, path.go(RIGHT).getCurrentDirection());

	}

}
