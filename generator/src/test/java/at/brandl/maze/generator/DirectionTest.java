package at.brandl.maze.generator;

import static at.brandl.maze.generator.Direction.*;
import org.junit.Assert;

import org.junit.Test;

public class DirectionTest {

	@Test
	public void left() {
		Assert.assertEquals(NORTH, EAST.left());
		Assert.assertEquals(EAST, SOUTH.left());
		Assert.assertEquals(SOUTH, WEST.left());
		Assert.assertEquals(WEST, NORTH.left());
	}

	@Test
	public void rigth() {
		Assert.assertEquals(NORTH, WEST.right());
		Assert.assertEquals(EAST, NORTH.right());
		Assert.assertEquals(SOUTH, EAST.right());
		Assert.assertEquals(WEST, SOUTH.right());
	}
	
	@Test
	public void opposite() {
		Assert.assertEquals(NORTH, SOUTH.opposite());
		Assert.assertEquals(EAST, WEST.opposite());
		Assert.assertEquals(SOUTH, NORTH.opposite());
		Assert.assertEquals(WEST, EAST.opposite());
	}
	
}
