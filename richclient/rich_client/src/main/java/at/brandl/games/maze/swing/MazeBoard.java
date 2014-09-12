package at.brandl.games.maze.swing;

import static at.brandl.games.commons.Orientation.EAST;
import static at.brandl.games.commons.Orientation.NORTH;
import static at.brandl.games.commons.Orientation.SOUTH;
import static at.brandl.games.commons.Orientation.WEST;

import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import at.brandl.games.commons.Board;
import at.brandl.games.commons.Board.Field;
import at.brandl.games.commons.Orientation;
import at.brandl.games.maze.generator.MazeGenerator;
import at.brandl.games.maze.generator.MazeGenerator.NoPathFoundException;
import at.brandl.games.maze.generator.Path;
import at.brandl.games.maze.generator.Path.Section;

public class MazeBoard extends JFrame implements ChangeListener {

	private static final long serialVersionUID = 5973000999795490532L;

	private static final int BOARD_SIZE = 700;
	private static final int MARGIN = 15;
	private static final int BORDER_WIDTH = 2;

	private static final int MIN_MAZE_SIZE = 10;
	private static final int MAX_MAZE_SIZE = 50;
	private static final int DEFAULT_MAZE_SIZE = 15;
	
	private static int mazeSize = DEFAULT_MAZE_SIZE;

	private static class MazeCellRenderer implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			@SuppressWarnings("unchecked")
			Field<Section> content = (Field<Section>) value;
			JComponent cell = new Box(BoxLayout.LINE_AXIS);
			cell.setBorder(createBorder(content.getContent()));
			cell.setSize(MazeBoard.getFieldSize() , getFieldSize());
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

	private Box box;

	public MazeBoard() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		box = createBox();
		add(box);

		JTable maze = createMaze();

		box.add(maze);
		Box controls = createControls();
		box.add(controls);
		pack();
		setVisible(true);

	}

	private JTable createMaze() {
		JTable table = createTable();
		table.setModel(createDataModel());
		layoutTable(table);
		return table;
	}
	
	public void stateChanged(ChangeEvent event) {
		JSlider source = (JSlider)event.getSource();
		if(!source.getValueIsAdjusting()){
			mazeSize = source.getValue();
			JTable maze = createMaze();
			box.remove(0);
			box.add(maze, 0);
			pack();
		}
	}

	private Box createControls() {
		Box box = new Box(BoxLayout.PAGE_AXIS);
		box.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN,
				MARGIN));
		JSlider difficulty = new JSlider(MIN_MAZE_SIZE, MAX_MAZE_SIZE, DEFAULT_MAZE_SIZE);
		difficulty.addChangeListener(this);
		box.add(difficulty);
		return box;
	}

	private Box createBox() {
		Box box = new Box(BoxLayout.PAGE_AXIS);
		box.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN,
				MARGIN));
		return box;
	}

	private JTable createTable() {
		JTable table = new JTable(mazeSize, mazeSize);

		table.setShowGrid(false);
		table.setRowHeight(getFieldSize());
		table.setDefaultRenderer(Object.class, new MazeCellRenderer());

		
		return table;
	}

	private void layoutTable(JTable table) {
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = columns.nextElement();
			column.setMinWidth(getFieldSize());
			column.setMaxWidth(getFieldSize());
			column.setPreferredWidth(getFieldSize());
		}
		table.doLayout();
	}
	
	private TableModel createDataModel() {
		TableModel dataModel = new DefaultTableModel(mazeSize, mazeSize);
		Board<Section> board = new Board<Path.Section>(mazeSize, mazeSize);
		MazeGenerator mazeGenerator = new MazeGenerator(board);

		mazeGenerator.setStart(0, mazeSize / 2);
		mazeGenerator.setEnd(mazeSize - 1, mazeSize / 2);
		try {
			mazeGenerator.generate();
		} catch (NoPathFoundException e) {
			System.exit(1);
		}

		for (int row = 0; row < mazeSize; row++) {
			for (int column = 0; column < mazeSize; column++) {
				dataModel.setValueAt(board.getField(row, column), row, column);
			}
		}
		return dataModel;
	}
	
	private static int getFieldSize() {
		return BOARD_SIZE / mazeSize;
	}


}
