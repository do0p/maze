package at.brandl.games.maze.android;

import static at.brandl.games.commons.Orientation.EAST;
import static at.brandl.games.commons.Orientation.NORTH;
import static at.brandl.games.commons.Orientation.SOUTH;
import static at.brandl.games.commons.Orientation.WEST;
import static at.brandl.games.maze.generator.Path.Target.START;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import at.brandl.games.commons.Board;
import at.brandl.games.commons.Board.Field;
import at.brandl.games.maze.generator.Path.Section;

public class MazeView extends View {

	public static interface Configuration {
		int getBorderWidth();

		int getBorderColor();

		int getFieldColor();

		int getVisitedFieldColor();

		int getSuccessFieldColor();

		float getMazeRatio();

		int getMazeSize();

	}

	private static final int TOP_BORDER = 1;
	private static final int RIGHT_BORDER = 2;
	private static final int BOTTOM_BORDER = 4;
	private static final int LEFT_BORDER = 8;

	private int borderWidth;
	private int borderColor;
	private int fieldColor;
	private int visitedFieldColor;
	private int successFieldColor;

	private LayerDrawable layerDrawable;
	private Board<Section> board;
	private int size;

	private int mazeWidth;
	private int mazeHeight;

	private DisplayMetrics metrics;
	private boolean gameOver;
	private Field<Section> start;
	private Field<Section> end;

	public MazeView(Context context, Configuration config) {
		super(context);

		this.borderWidth = config.getBorderWidth();
		this.borderColor = config.getBorderColor();
		this.fieldColor = config.getFieldColor();
		this.visitedFieldColor = config.getVisitedFieldColor();
		this.successFieldColor = config.getSuccessFieldColor();
		getDisplayMetrics(context);

	}

	private void getDisplayMetrics(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		metrics = new DisplayMetrics();
		display.getMetrics(metrics);
	}

	private void calcMazeSizes() {

		int width = metrics.widthPixels / mazeWidth;
		int height = metrics.heightPixels / mazeHeight;
		size = Math.min(width, height);
	}

	private void drawBoard() {

		setLayoutParams(new FrameLayout.LayoutParams(size * mazeWidth, size
				* mazeHeight));

		layerDrawable = new LayerDrawable(createShapes());
		updateField(start, visitedFieldColor);
		updateField(end, successFieldColor);
		gameOver = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (gameOver) {
			return true;
		}

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
						if (neighbour.getField() != null
								&& neighbour.getField().isVisited()) {
							update = true;
							break;
						}
					}
				}
				if (update) {
					boardField.setVisited(true);
					if (isEnd(boardField)) {
						gameOver = true;
						Section section;
						Section nextSection = boardField.getContent();
						do {
							section = nextSection;
							Field<Section> field = section.getField();
							updateField(field, successFieldColor);
							nextSection = section.getTarget(START);
						} while (!nextSection.equals(section));
					} else {
						updateField(boardField, visitedFieldColor);
					}
					return true;
				}
			}
		}
		return false;
	}

	private void updateField(Field<Section> field, int color) {
		ShapeDrawable viewField = getField(field.getRow(), field.getColumn());
		Paint paint = viewField.getPaint();
		paint.setColor(color);
	}

	private boolean isEnd(Field<Section> field) {
		return end.equals(field);
	}

	private boolean isStart(Field<Section> field) {
		return start.equals(field);
	}

	private ShapeDrawable getField(int row, int column) {

		int fieldIndex = calcFieldIndex(row, column);

		return (ShapeDrawable) layerDrawable.getDrawable(fieldIndex);
	}

	private int calcColumn(float x) {
		int column = calcField(x);
		return column >= 0 && column < mazeWidth ? column : -1;
	}

	private int calcRow(float y) {
		int row = calcField(y);
		return row >= 0 && row < mazeHeight ? row : -1;
	}

	private int calcField(float v) {
		return (int) Math.floor(v / size);
	}

	private int calcFieldIndex(int row, int column) {
		if (row >= 0 && row < mazeHeight && column >= 0 && column < mazeWidth) {
			return row * mazeWidth + column + 1;
		}
		return -1;
	}

	private ShapeDrawable[] createShapes() {
		int numShapes = mazeWidth * mazeHeight + 1;
		ShapeDrawable[] rectangles = new ShapeDrawable[numShapes];

		int i = 0;
		rectangles[i++] = createBackground();

		for (int row = 0; row < mazeHeight; row++) {
			for (int column = 0; column < mazeWidth; column++) {
				rectangles[i++] = createField(row, column, fieldColor);
			}
		}
		return rectangles;
	}

	private ShapeDrawable createBackground() {
		return drawRectangle(0, 0, mazeWidth * size, mazeHeight * size,
				borderColor, 0);
	}

	private ShapeDrawable createField(int row, int column, int color) {
		int left = column * size;
		int top = row * size;

		Field<Section> field = board.getField(row, column);
		ShapeDrawable rectangle = drawRectangle(left, top, size, size, color,
				calcBorders(field.getContent()));

		return rectangle;
	}

	protected void onDraw(Canvas canvas) {
		if (layerDrawable != null) {
			layerDrawable.draw(canvas);
		}
	}

	public void setBoard(Board<Section> board) {
		this.board = board;
		mazeWidth = board.getWidth();
		mazeHeight = board.getHeight();
		start = board.getStart();
		end = board.getEnd();

		calcMazeSizes();
		drawBoard();
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
			top += borderWidth;
			height -= borderWidth;
		}
		if (border(borders, RIGHT_BORDER)) {
			width -= borderWidth;
		}
		if (border(borders, BOTTOM_BORDER)) {
			height -= borderWidth;
		}
		if (border(borders, LEFT_BORDER)) {
			left += borderWidth;
			width -= borderWidth;
		}

		ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
		mDrawable.getPaint().setColor(color);
		mDrawable.setBounds(left, top, left + width, top + height);
		return mDrawable;
	}

	private boolean border(int borders, int border) {
		return (border & borders) == border;
	}

	public void changeFieldColor(int color) {
		fieldColor = color;
		for (Field<Section> field : board.getFields()) {
			if (!field.isVisited() && !isEnd(field) && !isStart(field)) {
				updateField(field, fieldColor);
			}
		}

	}

}
