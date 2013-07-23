package postpc.musica;

import java.util.Timer;
import java.util.TimerTask;

import postpc.musica.MasterPlayActivity.PlayTask;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class DebugYouTubePlayActivity extends YouTubeBaseActivity implements View.OnClickListener{

	private String youTubeId;
	private Button playButton;
	private boolean playing  = false;
	YouTubePlayerView youTubeView ;
	MusicYouTubeControl yCtrl;
	private Handler mHandler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_you_tube_play);
		playButton = (Button) findViewById(R.id.button1);
		playButton.setOnClickListener(this);

		youTubeId = getIntent().getExtras().getString("youTubeId");
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		yCtrl = new MusicYouTubeControl(this, playButton, youTubeId, youTubeView);
		
		mHandler = new Handler();
		mHandler.postDelayed(firstPlay, 6000);

	}
	
	Runnable firstPlay = new Runnable()
	{
	     @Override 
	     public void run() {
	    	 yCtrl.resumeMusic();
	    	 playing=true;
	     }
	};


	@Override
	public void onClick(View v) {
		if (playing == false) {
			yCtrl.resumeMusic();
			playing = true;
			return;
		} 
		if (playing == true) {
			yCtrl.pauseMusic();
			playing = false;
			return;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.you_tube_play, menu);
		return true;
	}



}
