package at.brandl.games.maze;

import static at.brandl.games.commons.Orientation.*;

import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import at.brandl.games.commons.Board;
import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Orientation;
import at.brandl.games.maze.Path.Section;

public class MazeBoard extends JFrame {

	private static final long serialVersionUID = 5973000999795490532L;
	private static final int MARGIN = 15;
	private static final int ROWS = 20;
	private static final int COLUMNS = 20;

	private static final int HEIGHT = 700 / ROWS;
	private static final int WIDTH = 700 / COLUMNS;

	private static class MazeCellRenderer implements TableCellRenderer {

		private static final int BORDER_WIDTH = 2;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			@SuppressWarnings("unchecked")
			Field<Section> content = (Field<Section>) value;
			JComponent cell = new Box(BoxLayout.LINE_AXIS);
			cell.setBorder(createBorder(content.getContent()));
			cell.setSize(WIDTH, HEIGHT);
			return cell;
		}

		private Border createBorder(Section section) {
			int top = getBorder(section, NORTH);
			int right = getBorder(section, EAST);
			int bottom = getBorder(section, SOUTH);
			int left = getBorder(section, WEST);

			Color color = UIManager.getColor("Table.gridColor");
			return new MatteBorder(top, left, bottom, right, color);
		}

		private int getBorder(Section section, Orientation orientation) {
			return section.getNeighbour(orientation) != null ? 0 : BORDER_WIDTH;
		}

	}

	public static void main(String args[]) {
		new MazeBoard();
	}

	public MazeBoard() {
		Box box = createBox();
		add(box);

		JTable table = createTable();
		table.setModel(createDataModel());

		box.add(table);
		pack();
		setVisible(true);
	}

	private Box createBox() {
		Box box = new Box(BoxLayout.LINE_AXIS);
		box.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN,
				MARGIN));
		return box;
	}

	private JTable createTable() {
		JTable table = new JTable(ROWS, COLUMNS);

		table.setShowGrid(false);
		table.setRowHeight(HEIGHT);
		table.setDefaultRenderer(Object.class, new MazeCellRenderer());

		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = columns.nextElement();
			column.setMinWidth(WIDTH);
			column.setMaxWidth(WIDTH);
		}

		return table;
	}

	private TableModel createDataModel() {
		TableModel dataModel = new DefaultTableModel(ROWS, COLUMNS);
		Board<Section> board = new Board<Path.Section>(ROWS, COLUMNS);
		MazeGenerator mazeGenerator = new MazeGenerator(board);

		mazeGenerator.setStart(0, COLUMNS / 2);
		mazeGenerator.setEnd(ROWS - 1, COLUMNS / 2);
		mazeGenerator.generate();

		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				dataModel.setValueAt(board.getField(row, column), row, column);
			}
		}
		return dataModel;
	}
}
