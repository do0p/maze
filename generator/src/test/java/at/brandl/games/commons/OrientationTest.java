package at.brandl.games.commons;
import static at.brandl.games.commons.Direction.*;
import static at.brandl.games.commons.Orientation.*;

import org.junit.Assert;
import org.junit.Test;

public class OrientationTest {

	@Test
	public void left() {
		Assert.assertEquals(NORTH, EAST.turn(LEFT));
		Assert.assertEquals(EAST, SOUTH.turn(LEFT));
		Assert.assertEquals(SOUTH, WEST.turn(LEFT));
		Assert.assertEquals(WEST, NORTH.turn(LEFT));
	}

	@Test
	public void rigth() {
		Assert.assertEquals(NORTH, WEST.turn(RIGHT));
		Assert.assertEquals(EAST, NORTH.turn(RIGHT));
		Assert.assertEquals(SOUTH, EAST.turn(RIGHT));
		Assert.assertEquals(WEST, SOUTH.turn(RIGHT));
	}
	

	@Test
	public void ahead() {
		Assert.assertEquals(NORTH, NORTH.turn(AHEAD));
		Assert.assertEquals(EAST, EAST.turn(AHEAD));
		Assert.assertEquals(SOUTH, SOUTH.turn(AHEAD));
		Assert.assertEquals(WEST, WEST.turn(AHEAD));
	}
	
	@Test
	public void opposite() {
		Assert.assertEquals(NORTH, SOUTH.opposite());
		Assert.assertEquals(EAST, WEST.opposite());
		Assert.assertEquals(SOUTH, NORTH.opposite());
		Assert.assertEquals(WEST, EAST.opposite());
	}
	
}
