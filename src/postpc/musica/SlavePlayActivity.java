package postpc.musica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;

import postpc.musica.MasterPlayActivity.PlayTask;
import postpc.musica.R.color;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SlavePlayActivity extends YouTubeBaseActivity{
	private String youTubeId;
	private TextView playText;
	private boolean playing  = false;
	YouTubePlayerView youTubeView ;
	MusicYouTubeControl yCtrl;
	BufferedReader br;
	Button stop;
	PrintWriter pw;
	Timer timer = new Timer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slave_play);
		
		playText = (TextView) findViewById(R.id.slave_play); //TODO For debug
		//setTextView();
		youTubeId = getIntent().getExtras().getString("youTubeId");//TODO add from send
		//youTubeId = Consts.quckPlayVideo;
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		yCtrl = new MusicYouTubeControl(this, playText,null, youTubeId, youTubeView);

		br = ((CommunicationBinder)getApplication()).reader;
		pw = ((CommunicationBinder)getApplication()).writer;
		stop = (Button)findViewById(R.id.buttonSlave);
		ReadFromMaster r = new ReadFromMaster(this, null);
		r.execute();

	}
	


	private void setTextView() {
	     int[] color = {Color.DKGRAY,Color.CYAN};
	     float[] position = {0, 1};
	     TileMode tile_mode = TileMode.MIRROR; // or TileMode.REPEAT;
	     LinearGradient lin_grad = new LinearGradient(0, 0, 0, 50,color,position, tile_mode);
	     Shader shader_gradient = lin_grad;
	     playText.getPaint().setShader(shader_gradient);
		
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public class ReadFromMaster extends AsyncTask<Void, Void, String> {

		/**
		 * @param context
		 * @param statusText
		 */
		long myTime;
		long masterTime;
		long messageDelayTime,bestMessageDelayTime;
		long clockDifference,bestClockDifference;
		public ReadFromMaster(Context context, View view) {
		}

		@Override
		protected String doInBackground(Void... params) {
			clockDifference = 0;
			messageDelayTime = 0;
			bestMessageDelayTime = 9999999;
			
			int counter = 0;
			try {
				while(counter <  20){
					
					clockDifference = Long.parseLong(br.readLine()) - System.currentTimeMillis();
					pw.println();
					messageDelayTime = (long) ( Long.parseLong(br.readLine()));
					System.out.println(messageDelayTime);
					if (messageDelayTime < bestMessageDelayTime){
						bestMessageDelayTime = messageDelayTime;
						bestClockDifference = clockDifference;
					}				
					counter++;
					pw.println("");
				}
				
				masterTime = Long.parseLong(br.readLine());
				yCtrl.inPlayMode();
				System.out.println("timer "+(masterTime - System.currentTimeMillis() - bestClockDifference - messageDelayTime));
				return "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			return null;
		}

		@Override
		protected void onPostExecute(String receivedMsg) {

			timer.schedule(new PlayTask(), masterTime - System.currentTimeMillis() - bestClockDifference);
			System.out.println("clock " + bestClockDifference);
			System.out.println("delay used "+ messageDelayTime);
			System.out.println("real delay "+ bestMessageDelayTime);
		}


	}	
	class PlayTask extends TimerTask {
		public void run() {
			mHandler.obtainMessage(1).sendToTarget();
			timer.cancel(); //Terminate the timer thread
		}
		public Handler mHandler = new Handler() {
		    public void handleMessage(Message msg) {
		    	yCtrl.resumeMusic();
		    	playText.setVisibility(View.GONE);
		    	stop.setVisibility(View.VISIBLE);
		    	stop.setEnabled(true);
		    	stop.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();
						
					}
				});
				playing=true; //this is the textview
		    }
		};
	}

	protected void onDestroy()
	{
		super.onDestroy();
	}


}
