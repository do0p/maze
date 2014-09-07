package at.brandl.games.commons;

public class FieldNotFoundException extends RuntimeException {

	public FieldNotFoundException(int row, int column) {
		super("No field with row " + row +  " and column " + column + ".");
	}

	private static final long serialVersionUID = 9029363192147747391L;

}
