package at.brandl.games.commons;

public enum Orientation {
	NORTH, EAST, SOUTH, WEST;

	public Orientation turn(Direction direction) {
		switch (this) {
		case NORTH:
			switch(direction) {
			case LEFT:
				return WEST;
			case AHEAD:
				return this;
			case RIGHT:
				return EAST;
			}
		case EAST:
			switch(direction) {
			case LEFT:
				return NORTH;
			case AHEAD:
				return this;
			case RIGHT:
				return SOUTH;
			}
		case SOUTH:
			switch(direction) {
			case LEFT:
				return EAST;
			case AHEAD:
				return this;
			case RIGHT:
				return WEST;
			}
		case WEST:
			switch(direction) {
			case LEFT:
				return SOUTH;
			case AHEAD:
				return this;
			case RIGHT:
				return NORTH;
			}
		default:
			throw new AssertionError("unknown Orientation " + this);
		}
	}
	
	public Orientation opposite() {
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
			throw new AssertionError("unknown Orientation " + this);
		}
	}
}
