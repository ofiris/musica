package postpc.musica;

import java.util.Timer;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;

public class MusicYouTubeControl implements YouTubePlayer.OnInitializedListener{

	private String youTubeId;
	int playState = -1; //-1 = not started, 1 = started
	private TextView playButton;
	private YouTubePlayer player;
	private MyPlayerStateChangeListener playerStateChangeListener;
	YouTubePlayerView youTubeView ;
	YouTubeBaseActivity mainActivity;
	private Handler myBuffHandler;

	public MusicYouTubeControl(YouTubeBaseActivity mActivity, TextView pButton, String yId, YouTubePlayerView yView){
		playerStateChangeListener = new MyPlayerStateChangeListener();
		mainActivity = mActivity;
		playButton = pButton;
		youTubeId = yId;
		youTubeView = yView;
		youTubeView.initialize(Consts.DEVELOPER_KEY, this); //will go to onInitializationSuccess
		myBuffHandler = new Handler();

	}

	Runnable bufferLoadRun = new Runnable()
	{
		@Override 
		public void run() {
			if(playState == -1){
				playButton.setText("not loaded yet");
				return;
			}
			//playButton.setText("bufferRun");
			player.play();
			//TODO remove volume
			System.out.println("Post Delayed 4000");
			myBuffHandler.postDelayed(bufferLoad, 4000);
			System.out.println("Post Delayed 4000");
		}
	};
	Runnable bufferLoad = new Runnable()
	{
		@Override 
		public void run() {
			//playButton.setText("buffering");
			playState = 0;
			//player.pause();
			//player.seekToMillis(1);
			//TODO add volume
		}
	};


	public void resumeMusic(){
		if(playState == 0){
			playButton.setText("pause");
			playState = 1;
			player.play();
		}
	}

	public void pauseMusic(){
		if(playState == 1){
			playButton.setText("play");
			playState = 0;
			player.pause();
		}
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult errorReason) {
		String errorMessage = String.format("player Error:", errorReason.toString());
		Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_LONG).show();

	}


	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		this.player = player;
		//player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
		player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

		player.setPlayerStateChangeListener(playerStateChangeListener);// will go to onLoaded
		player.setPlaybackEventListener(playerStateChangeListener);
		if (!wasRestored) {
			System.out.println("wasRestored ? WTF");
			//player.cueVideo(youTubeId);
			player.loadVideo(youTubeId);
		}

	}
	boolean isInPlayMode = false;

	public void inPlayMode(){
		isInPlayMode = true;
	}

	private final class MyPlayerStateChangeListener implements PlayerStateChangeListener, PlaybackEventListener {
		boolean realVideoStarted = false;
		@Override
		public void onBuffering(boolean isBuffering)
		{
			if (isBuffering && realVideoStarted && isInPlayMode){
				
			}
			System.out.println("IsBuffering: " + isBuffering);			
		}

		@Override
		public void onLoading() { }

		@Override
		public void onLoaded(String videoId) {
			if(playState == -1){
				//playButton.setText("loaded -  play");
				myBuffHandler.postDelayed(bufferLoadRun, 4000);
				playState = 0;
			}
		}

		@Override
		public void onAdStarted() {
			//TODO useful
			System.out.println("ad started!!!!!!");
		}

		@Override
		public void onVideoStarted() {
			realVideoStarted = true;
			System.out.println("onVideoStarted");
		}

		@Override
		public void onVideoEnded() {  }

		@Override
		public void onError(ErrorReason reason) {  
			System.out.println("onError: " + reason.toString());			
		}

		@Override
		public void onPaused() {
			// TODO Auto-generated method stub
			System.out.println("onPaused");
		}

		@Override
		public void onPlaying() {
			if (!realVideoStarted)
				return;
			if (!isInPlayMode){
				player.pause();
				player.seekToMillis(0);
			}
			System.out.println("onPlaying");
		}

		@Override
		public void onSeekTo(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopped() {
			System.out.println("onStopped");

		}

	}
}
