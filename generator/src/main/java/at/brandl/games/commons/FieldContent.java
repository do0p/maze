package at.brandl.games.commons;

import at.brandl.games.commons.Board.Field;

public  interface FieldContent<T> {

	Field<? extends FieldContent<T>> getField();

	void setField(Field<? extends FieldContent<T>> field);

}
