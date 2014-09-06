package at.brandl.maze.generator;

public enum Direction {
	NORTH, EAST, SOUTH, WEST;

	public Direction left() {
		switch (this) {
		case NORTH:
			return WEST;
		case EAST:
			return NORTH;
		case SOUTH:
			return EAST;
		case WEST:
			return SOUTH;
		default:
			throw new AssertionError("unknown Direction " + this);
		}
	}
	
	public Direction right() {
		switch (this) {
		case NORTH:
			return EAST;
		case EAST:
			return SOUTH;
		case SOUTH:
			return WEST;
		case WEST:
			return NORTH;
		default:
			throw new AssertionError("unknown Direction " + this);
		}
	}
	
	public Direction opposite() {
		switch (this) {
		case NORTH:
			return SOUTH;
		case EAST:
			return WEST;
		case SOUTH:
			return NORTH;
		case WEST:
			return EAST;
		default:
			throw new AssertionError("unknown Direction " + this);
		}
	}
}
