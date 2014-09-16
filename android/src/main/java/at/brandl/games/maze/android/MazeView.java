package at.brandl.games.maze.android;

import static at.brandl.games.commons.Orientation.EAST;
import static at.brandl.games.commons.Orientation.NORTH;
import static at.brandl.games.commons.Orientation.SOUTH;
import static at.brandl.games.commons.Orientation.WEST;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import at.brandl.games.commons.Board;
import at.brandl.games.commons.Board.Field;
import at.brandl.games.maze.generator.MazeGenerator;
import at.brandl.games.maze.generator.MazeGenerator.NoPathFoundException;
import at.brandl.games.maze.generator.Path.Section;
import static at.brandl.games.maze.generator.Path.Target.*;

public class MazeView extends View {

	private static final int TOP_BORDER = 1;
	private static final int RIGHT_BORDER = 2;
	private static final int BOTTOM_BORDER = 4;
	private static final int LEFT_BORDER = 8;

	private static final int BORDER_WIDTH = 5;
	private static final int BORDER_COLOR = Color.BLACK;
	private static final int FIELD_COLOR = Color.WHITE;
	private static final int VISITED_FIELD_COLOR = Color.BLUE;
	private static final int SUCCESS_FIELD_COLOR = Color.GREEN;
	private static final int HORIZONTAL_PADDING = 10;
	private static final int VERTICAL_PADDING = 240;

	private static final int MAZE_WIDTH = 16;
	private static final int MAZE_HEIGHT = 16;
	private static final int START_ROW = MAZE_HEIGHT - 1;
	private static final int START_COLUMN = MAZE_WIDTH / 2;
	private static final int END_ROW = 0;
	private static final int END_COLUMN = START_COLUMN;

	private LayerDrawable layerDrawable;
	private Board<Section> board;
	private int size;


	public MazeView(Context context) {
		super(context);

		board = createBoard();
		size = calcFieldSize(context);
		layerDrawable = new LayerDrawable(createShapes());

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean updated = false;
		if (MotionEvent.ACTION_MOVE == event.getAction()
				|| MotionEvent.ACTION_DOWN == event.getAction()) {

			for (int h = 0; h < event.getHistorySize(); h++) {
				float x = event.getHistoricalX(0, h);
				float y = event.getHistoricalY(0, h);
				updated |= updateField(x, y);
			}

			float x = event.getX(0);
			float y = event.getY(0);
			updated |= updateField(x, y);

			if (updated) {
				invalidate();
			}
		}
		return true;
	}

	private boolean updateField(float x, float y) {
		int row = calcRow(y);
		int column = calcColumn(x);
		if (row >= 0 && column >= 0) {
			Field<Section> boardField = board.getField(row, column);
			if (!boardField.isVisited()) {
				boolean update = false;
				
				if (isStart(boardField)) {
					update = true;
				} else {

					Section section = boardField.getContent();

					for (Section neighbour : section.getNeighbours().values()) {
						if (neighbour.getField().isVisited()) {
							update = true;
							break;
						}
					}
				}
				if (update) {
					boardField.setVisited(true);
					if(isEnd(boardField)){
						Section section ;
						Section nextSection = boardField.getContent();
						do {
							section = nextSection;
							Field<Section> field = section.getField();
							updateField(field.getRow(), field.getColumn(), SUCCESS_FIELD_COLOR);
							nextSection = section.getTarget(START);
						} while(!nextSection.equals(section));
					}else{
					updateField(row, column, VISITED_FIELD_COLOR);
					}
					return true;
				}
			}
		}
		return false;
	}

	private void updateField(int row, int column, int color) {
		ShapeDrawable viewField = getField(row, column);
		Paint paint = viewField.getPaint();
		paint.setColor(color);
	}

	private boolean isEnd(Field<Section> field) {
		return field.getColumn() == END_COLUMN && field.getRow() == END_ROW;
	}

	private boolean isStart(Field<Section> field) {
		return field.getColumn() == START_COLUMN && field.getRow() == START_ROW;
	}

	private ShapeDrawable getField(int row, int column) {

		int fieldIndex = calcFieldIndex(row, column);

		return (ShapeDrawable) layerDrawable.getDrawable(fieldIndex);
	}

	private int calcColumn(float x) {
		int column = calcField(x - HORIZONTAL_PADDING);
		return column >= 0 && column < MAZE_WIDTH ? column : -1;
	}

	private int calcRow(float y) {
		int row = calcField(y - VERTICAL_PADDING);
		return row >= 0 && row < MAZE_HEIGHT ? row : -1;
	}

	private int calcField(float v) {
		return (int) Math.floor(v / size);
	}

	private int calcFieldIndex(int row, int column) {
		if (row >= 0 && row < MAZE_HEIGHT && column >= 0 && column < MAZE_WIDTH) {
			return row * MAZE_WIDTH + column + 1;
		}
		return -1;
	}

	private ShapeDrawable[] createShapes() {
		int numShapes = MAZE_WIDTH * MAZE_HEIGHT + 1;
		ShapeDrawable[] rectangles = new ShapeDrawable[numShapes];

		int i = 0;
		rectangles[i++] = createBackground();

		for (int row = 0; row < MAZE_HEIGHT; row++) {
			for (int column = 0; column < MAZE_WIDTH; column++) {
				rectangles[i++] = createField(row, column, FIELD_COLOR);
			}
		}
		return rectangles;
	}

	private ShapeDrawable createBackground() {
		return drawRectangle(HORIZONTAL_PADDING, VERTICAL_PADDING, MAZE_WIDTH
				* size, MAZE_HEIGHT * size, BORDER_COLOR, 0);
	}

	private ShapeDrawable createField(int row, int column, int color) {
		int left = HORIZONTAL_PADDING + column * size;
		int top = VERTICAL_PADDING + row * size;

		Field<Section> field = board.getField(row, column);
		ShapeDrawable rectangle = drawRectangle(left, top, size, size, color,
				calcBorders(field.getContent()));

		return rectangle;
	}

	protected void onDraw(Canvas canvas) {
		layerDrawable.draw(canvas);
	}

	private int calcFieldSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		int width = (metrics.widthPixels - 2 * HORIZONTAL_PADDING) / MAZE_WIDTH;
		int height = (metrics.heightPixels - 2 * VERTICAL_PADDING)
				/ MAZE_HEIGHT;

		return Math.min(width, height);
	}

	private Board<Section> createBoard() {
		Board<Section> board = new Board<Section>(MAZE_WIDTH, MAZE_HEIGHT);
		MazeGenerator mazeGenerator = new MazeGenerator(board);
		mazeGenerator.setStart(START_ROW, START_COLUMN);
		mazeGenerator.setEnd(END_ROW, END_COLUMN);
		try {
			mazeGenerator.generate();
		} catch (NoPathFoundException e) {
			throw new RuntimeException(e);
		}
		return board;
	}

	private int calcBorders(Section section) {
		int result = TOP_BORDER + LEFT_BORDER + RIGHT_BORDER + BOTTOM_BORDER;
		if (section.hasNeighbour(NORTH)) {
			result -= TOP_BORDER;
		}
		if (section.hasNeighbour(EAST)) {
			result -= RIGHT_BORDER;
		}
		if (section.hasNeighbour(SOUTH)) {
			result -= BOTTOM_BORDER;
		}
		if (section.hasNeighbour(WEST)) {
			result -= LEFT_BORDER;
		}
		return result;
	}

	private ShapeDrawable drawRectangle(int left, int top, int width,
			int height, int color, int borders) {

		if (border(borders, TOP_BORDER)) {
			top += BORDER_WIDTH;
			height -= BORDER_WIDTH;
		}
		if (border(borders, RIGHT_BORDER)) {
			width -= BORDER_WIDTH;
		}
		if (border(borders, BOTTOM_BORDER)) {
			height -= BORDER_WIDTH;
		}
		if (border(borders, LEFT_BORDER)) {
			left += BORDER_WIDTH;
			width -= BORDER_WIDTH;
		}

		ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
		mDrawable.getPaint().setColor(color);
		mDrawable.setBounds(left, top, left + width, top + height);
		return mDrawable;
	}

	private boolean border(int borders, int border) {
		return (border & borders) == border;
	}

}
