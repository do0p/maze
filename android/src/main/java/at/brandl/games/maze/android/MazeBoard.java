package at.brandl.games.maze.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;
import at.brandl.games.commons.Board;
import at.brandl.games.maze.android.MazeView.Configuration;
import at.brandl.games.maze.android.util.SystemUiHider;
import at.brandl.games.maze.generator.MazeGenerator;
import at.brandl.games.maze.generator.MazeGenerator.ProgressListener;
import at.brandl.games.maze.generator.Path.Section;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MazeBoard extends Activity implements OnSeekBarChangeListener,
		OnClickListener {

	private static final int BORDER_WIDTH = 5;
	private static final int BORDER_COLOR = Color.BLACK;
	private static final int FIELD_COLOR = Color.WHITE;
	private static final int VISITED_FIELD_COLOR = Color.BLUE;
	private static final int SUCCESS_FIELD_COLOR = Color.GREEN;
	private static final float MAZE_RATIO = 1.2f;
	private static final int DEFAULT_MAZE_SIZE = 12;

	private static final int MIN_MAZE_SIZE = 5;
	private static final int MAX_MAZE_SIZE = 24;

	private MazeView mazeView;
	private int mazeSize = DEFAULT_MAZE_SIZE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_maze_board);

		mazeView = new MazeView(getBaseContext(), new Configuration() {

			@Override
			public int getVisitedFieldColor() {
				return VISITED_FIELD_COLOR;
			}

			@Override
			public int getSuccessFieldColor() {
				return SUCCESS_FIELD_COLOR;
			}

			@Override
			public int getMazeSize() {
				return DEFAULT_MAZE_SIZE;
			}

			@Override
			public float getMazeRatio() {
				return MAZE_RATIO;
			}

			@Override
			public int getFieldColor() {
				return FIELD_COLOR;
			}

			@Override
			public int getBorderWidth() {
				return BORDER_WIDTH;
			}

			@Override
			public int getBorderColor() {
				return BORDER_COLOR;
			}
		});
		createBoard();

		final ViewGroup viewContainer = (ViewGroup) findViewById(R.id.view_container);
		viewContainer.addView(mazeView);

		final ToggleButton lightSwitch = (ToggleButton) findViewById(R.id.light_switch);
		lightSwitch.setChecked(true);
		lightSwitch.setOnClickListener(this);

		final SeekBar mazeSizeBar = (SeekBar) findViewById(R.id.maze_size_bar);
		mazeSizeBar.setProgress(calcLevel());
		mazeSizeBar.setOnSeekBarChangeListener(this);
		mazeSizeBar.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int viewId = v.getId();
		if (viewId == R.id.maze_size_bar) {
			createBoard();
		} else if (viewId == R.id.light_switch) {
			ToggleButton lightSwitch = (ToggleButton) findViewById(R.id.light_switch);
			if (lightSwitch.isChecked()) {
				mazeView.changeFieldColor(FIELD_COLOR);
			} else {
				mazeView.changeFieldColor(BORDER_COLOR);
			}
			mazeView.invalidate();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int level = seekBar.getProgress();
		calcMazeSize(level);
		createBoard();
	}

	private void calcMazeSize(int level) {

		mazeSize = MIN_MAZE_SIZE + level * (MAX_MAZE_SIZE - MIN_MAZE_SIZE)
				/ 100;
	}

	private int calcLevel() {
		return (mazeSize - MIN_MAZE_SIZE) * 100
				/ (MAX_MAZE_SIZE - MIN_MAZE_SIZE);
	}

	private void createBoard() {

		new GenerateTask().execute();

	}

	private class GenerateTask extends AsyncTask<Void, Integer, Board<Section>>
			implements ProgressListener {

		private ViewGroup progressView;
		private ProgressBar progressBar;
		private ToggleButton lightSwitch;
		private SeekBar mazeSizeBar;

		private GenerateTask() {
			this.progressView = (ViewGroup) findViewById(R.id.view_progress);
			this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
			this.lightSwitch = (ToggleButton) findViewById(R.id.light_switch);
			this.mazeSizeBar = (SeekBar) findViewById(R.id.maze_size_bar);

		}

		@Override
		protected void onPreExecute() {
			lightSwitch.setEnabled(false);
			mazeSizeBar.setEnabled(false);
			progressBar.setProgress(0);
			progressView.setVisibility(RelativeLayout.VISIBLE);
		}

		@Override
		protected Board<Section> doInBackground(Void... params) {
			Board<Section> board = new Board<Section>(mazeSize,
					(int) (mazeSize * MAZE_RATIO));
			MazeGenerator generator = new MazeGenerator(board);
			generator.addProgressListener(this);
			generator.generate();
			return board;
		}

		@Override
		protected void onPostExecute(Board<Section> result) {
			final ViewGroup viewContainer = (ViewGroup) findViewById(R.id.view_container);
			MazeView mazeView = (MazeView) viewContainer.getChildAt(0);
			mazeView.setBoard(result);
			progressView.setVisibility(View.INVISIBLE);
			lightSwitch.setEnabled(true);
			mazeSizeBar.setEnabled(true);
		}

		@Override
		public void updateProgress(int progress) {
			publishProgress(progress);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressBar.setProgress(values[0]);
		}

	}
}
