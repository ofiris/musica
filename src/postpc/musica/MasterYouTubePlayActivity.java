package postpc.musica;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import postpc.musica.CommunicationBinder.ReaderWriterPair;
import postpc.musica.MasterPlayActivity.CommunicateWithSlaves;
import postpc.musica.MasterPlayActivity.PlayTask;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MasterYouTubePlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener{

	private String youTubeId;
	int playState = -1; //-1 = not started, 0 = paused, 1 = playing
	private Button playButton;
	private YouTubePlayer player;
	private MyPlayerStateChangeListener playerStateChangeListener;
	YouTubePlayerView youTubeView ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_you_tube_play);
		playerStateChangeListener = new MyPlayerStateChangeListener();
		playButton = (Button) findViewById(R.id.button1);
		playButton.setOnClickListener(this);

		youTubeId = getIntent().getExtras().getString("youTubeId");
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
	}


	@Override
	public void onClick(View v) {
		if(playState == -1){
			playButton.setText("Loding...");
			//TODO TODO add sync algo...
			youTubeView.initialize(Consts.DEVELOPER_KEY, this); //will go to onInitializationSuccess
			return;
		}
		if (playState == 0) {
			playButton.setText("pause");
			playState = 1;
			player.play();
			return;
		} 
		if (playState == 1) {
			playButton.setText("play");
			playState = 0;
			player.pause();
			return;
		}
	}
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		this.player = player;
		player.setPlayerStateChangeListener(playerStateChangeListener);// will go to onLoaded
		if (!wasRestored) {
			player.cueVideo(youTubeId);
			//player.loadVideo(youTubeId);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.you_tube_play, menu);
		return true;
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult errorReason) {
		String errorMessage = String.format("player Error:", errorReason.toString());
		Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

	}










	private final class MyPlayerStateChangeListener implements PlayerStateChangeListener {
		@Override
		public void onLoading() { }

		@Override
		public void onLoaded(String videoId) {
			//TODO can't pause - if could my be was able to load buffer
			if(playState == -1){
				playButton.setText("loaded -  pause");
				player.play();
				playState = 1;
			}
		}

		@Override
		public void onAdStarted() {
			//TODO useful
			System.out.println("ad started!!!!!!");
		}

		@Override
		public void onVideoStarted() {
			//TODO can't pause too
		}

		@Override
		public void onVideoEnded() {  }

		@Override
		public void onError(ErrorReason reason) {  }

	}

}
