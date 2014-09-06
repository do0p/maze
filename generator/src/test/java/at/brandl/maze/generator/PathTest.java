package at.brandl.maze.generator;

import static at.brandl.maze.generator.Direction.*;
import static org.junit.Assert.*;

import org.junit.Test;

import at.brandl.maze.generator.Path.Section;

public class PathTest {

	@Test
	public void simplePath() {
		Section first = new Section();
		Path path = new Path(NORTH, first);
		path.ahead().turnLeft().turnRight();
		
		Section start = path.getStart();
		assertSame(first, start);
		
		Section nextSection = start.getNeighbour(NORTH);
		assertNotNull(nextSection);
		assertNull(start.getNeighbour(EAST));
		assertNull(start.getNeighbour(SOUTH));
		assertNull(start.getNeighbour(WEST));
		
		assertSame(start, nextSection.getNeighbour(SOUTH));
		Section lastSection = nextSection;
		nextSection = lastSection.getNeighbour(WEST);
		assertNotNull(nextSection);
		assertNull(lastSection.getNeighbour(NORTH));
		assertNull(lastSection.getNeighbour(EAST));
		
		assertSame(lastSection, nextSection.getNeighbour(EAST));
		lastSection = nextSection;
		nextSection = lastSection.getNeighbour(NORTH);
		assertNotNull(nextSection);
		assertNull(lastSection.getNeighbour(SOUTH));
		assertNull(lastSection.getNeighbour(WEST));
		
		assertSame(lastSection, nextSection.getNeighbour(SOUTH));
		assertNull(nextSection.getNeighbour(EAST));
		assertNull(nextSection.getNeighbour(WEST));
		assertNull(nextSection.getNeighbour(NORTH));
		assertSame(nextSection, path.getEnd());
	}

}
