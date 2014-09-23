package at.brandl.games.maze.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;
import at.brandl.games.maze.android.MazeView.Configuration;
import at.brandl.games.maze.android.util.SystemUiHider;

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
		final ViewGroup viewContainer = (ViewGroup) findViewById(R.id.view_container);
		viewContainer.addView(mazeView);

		final ToggleButton lightSwitch = (ToggleButton) findViewById(R.id.light_switch);
		lightSwitch.setChecked(true);
		lightSwitch.setOnClickListener(this);

		final SeekBar mazeSizeBar = (SeekBar) findViewById(R.id.maze_size_bar);
		mazeSizeBar.setProgress(calcLevel(DEFAULT_MAZE_SIZE));
		mazeSizeBar.setOnSeekBarChangeListener(this);
		mazeSizeBar.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.maze_size_bar) {
			mazeView.restart();
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
		mazeView.setMazeSize(calcMazeSize(level));
		mazeView.restart();
	}

	private int calcMazeSize(int level) {

		return MIN_MAZE_SIZE + level * (MAX_MAZE_SIZE - MIN_MAZE_SIZE) / 100;
	}

	private int calcLevel(int mazeSize) {
		return (mazeSize - MIN_MAZE_SIZE) * 100
				/ (MAX_MAZE_SIZE - MIN_MAZE_SIZE);
	}

}
