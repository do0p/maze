package at.brandl.games.maze.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import at.brandl.games.maze.android.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MazeBoard extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_maze_board);

		final View contentView = createView();
		final ViewGroup viewContainer = (ViewGroup) findViewById(R.id.view_container);
		viewContainer.addView(contentView);
		

	
	}

	private View createView() {
		return new MazeView(getBaseContext());
	}


}
