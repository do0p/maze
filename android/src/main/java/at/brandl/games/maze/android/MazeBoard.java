package at.brandl.games.maze.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;
import at.brandl.games.maze.android.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MazeBoard extends Activity implements OnSeekBarChangeListener,
		OnClickListener {

	private static final int DEFAULT_MAZE_SIZE = 12;
	private static final int MIN_MAZE_SIZE = 5;
	private static final int MAX_MAZE_SIZE = 24;
	private MazeView mazeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_maze_board);

		mazeView = new MazeView(getBaseContext(), DEFAULT_MAZE_SIZE);
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
				mazeView.setFieldColor(Color.WHITE);
			} else {
				mazeView.setFieldColor(Color.BLACK);
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
