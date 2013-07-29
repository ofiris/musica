package postpc.musica;

import java.util.Timer;
import java.util.TimerTask;

import postpc.musica.MasterPlayActivity.PlayTask;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;
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

	private YouTubePlayer player;
	private MyPlayerStateChangeListener playerStateChangeListener;
	YouTubePlayerView youTubeView ;
	YouTubeBaseActivity mainActivity;
	private Handler myBuffHandler;
	private Button masterButton;
	private TextView slaveTextView;
	private boolean checkingDelay = false;
	
	public MusicYouTubeControl(YouTubeBaseActivity mActivity, TextView slaveTextView, Button masterButton, String yId, YouTubePlayerView yView){
		this.playerStateChangeListener = new MyPlayerStateChangeListener();
		this.mainActivity = mActivity;
		this.slaveTextView = slaveTextView;
		this.youTubeId = yId;
		this.youTubeView = yView;
		this.youTubeView.initialize(Consts.DEVELOPER_KEY, this); //will go to onInitializationSuccess
		this.myBuffHandler = new Handler();
		this.masterButton = masterButton;
	}
	long playDelay=0;
	Runnable bufferLoadRun = new Runnable()
	{
		@Override 
		public void run() {
			if(playState == -1){
				if (masterButton!=null)masterButton.setText("not loaded yet");
				return;
			}
			//playButton.setText("bufferRun");

			player.play();
			System.out.println("pressed play");

		}
	};

	Timer t;
	class ShowStartButton extends TimerTask {
		public void run() {
			mHandler.obtainMessage(1).sendToTarget();
			//t.cancel(); //Terminate the timer thread
		}
	}
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (masterButton!=null){
				masterButton.setEnabled(true);
				masterButton.setText("Play");
			}
		}
	};
	
	class CalculateDelay extends TimerTask {
		public void run() {
			checkingDelay = true;
			playDelay = System.nanoTime();
			player.play();
		}
	}
	
	


	public void resumeMusic(){
		if(playState == 0){
			playState = 1;
			playDelay = System.nanoTime();
			player.play();
		}
	}

	public void pauseMusic(){
		if(playState == 1){
			if (masterButton!=null)masterButton.setText("play");
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
		player.loadVideo(youTubeId);


	}
	boolean isInPlayMode = false;

	public void inPlayMode(){
		isInPlayMode = true;
	}
	boolean firstPlay = true; 

	public long getPlayDelay(){
		return playDelay;
	}
	private final class MyPlayerStateChangeListener implements PlayerStateChangeListener, PlaybackEventListener {
		boolean realVideoStarted = false;
		@Override
		public void onBuffering(boolean isBuffering)
		{
			gotBuffer = true;
			if (!isBuffering){
				playDelay = System.nanoTime();
			}
			gotBuffer = true;
			System.out.println("IsBuffering: " + isBuffering);			
		}

		@Override
		public void onLoading() { }

		@Override
		public void onLoaded(String videoId) {
			if(playState == -1){
				//playButton.setText("loaded -  play");
				myBuffHandler.postDelayed(bufferLoadRun, 500);
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
			if (nowRealyPlay){
				mainActivity.finish();
			}
			System.out.println("onPaused");
		}
		boolean gotBuffer = false;
		boolean nowRealyPlay = false;
		
		@Override
		public void onPlaying() {
			playDelay = (System.nanoTime() - playDelay)/1000000;
			if (isInPlayMode)
			{
				nowRealyPlay = true;
				System.out.println("real play delay is: " + playDelay);
				Toast.makeText(mainActivity, "the real play delay is: " + playDelay, Toast.LENGTH_SHORT).show();
			}
			else if (checkingDelay){
				Toast.makeText(mainActivity, "the first play delay is: " + playDelay, Toast.LENGTH_SHORT).show();
				System.out.println("The initial delay is:" +playDelay);
				checkingDelay = false;
				if (gotBuffer){
					System.out.println("no good got buffer");
				}
				player.pause();
				player.seekToMillis(0);
				t = new Timer();
				t.schedule(new ShowStartButton(), 1000);
				
			}
			else if (firstPlay){
				player.pause();
				player.seekToMillis(0);
				Timer t = new Timer();
				t.schedule(new ShowStartButton(), 9000);
				firstPlay = false;
			}
			
		}

		@Override
		public void onSeekTo(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopped() {
			if (nowRealyPlay){
				mainActivity.finish();
			}

		}

	}
}
