package postpc.musica;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.youtube.player.YouTubePlayerView;

import postpc.musica.CommunicationBinder.ReaderWriterPair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//TODO now use the youTubeOne. 
public class MasterPlayActivity extends ParentActivity {

	
	private String youTubeId;
	private Button playButton;
	private boolean playing  = false;
	YouTubePlayerView youTubeView ;
	MusicYouTubeControl yCtrl;
	HashMap<Socket, ReaderWriterPair> connections;

	Intent intent1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_play);
		
		playButton = (Button) findViewById(R.id.button1);
		youTubeId = getIntent().getExtras().getString("youTubeId");
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		yCtrl = new MusicYouTubeControl(this, (TextView)playButton, youTubeId, youTubeView);
		
		CommunicationBinder myCom = (CommunicationBinder) getApplication();
		connections =myCom.connections;
		/*
		 * Bind service call, starts the service if it didn't start yet
		 */


		communicationTask = new CommunicateWithSlaves(); 
		//communicationTask.execute();

		playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(playing==false)
				{
					yCtrl.inPlayMode();
					communicationTask.execute();
					//TODO add resume
				}
				else{
					//TODO use communicationTask
					yCtrl.pauseMusic();
					playing=false;
				}
			}


		});
	}

	CommunicateWithSlaves communicationTask;
	long timeToStart;
	Timer t;
	class CommunicateWithSlaves extends AsyncTask<Void, Void, String>{




		private void informSlaves() {
			
			try {
				for (ReaderWriterPair pair : connections.values()){
					long delay =100;
					int maxDelay = 20;
					int counter = 0;
					
					while(counter <20){
						delay = System.nanoTime();
						pair.writer.println(System.currentTimeMillis());

						pair.reader.readLine();

						delay = (System.nanoTime() - delay)/1000000;
						pair.writer.println(delay);
						pair.reader.readLine();
						counter++;

					}
					
				}
				timeToStart = System.currentTimeMillis() + 6000; //TODO changed, was 2000
				for (ReaderWriterPair pair : connections.values()){
					pair.writer.println(timeToStart);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			for (int i=0;i<5;i++){
				for (ReaderWriterPair pair : connections.values()){
					pair.writer.println(System.currentTimeMillis());
				}
			}
			 */


			/*
			for (ReaderWriterPair pair : connections.values()){
				pair.writer.println(timeToStart);
			}
			 */
			/*
			(new CalculateDelayThread(100)).start();
			(new CalculateDelayThread(500)).start();
			(new CalculateDelayThread(1000)).start();
			(new NotifyThread(1500)).start();
			(new NotifyThread(2000)).start();
			(new NotifyThread(2500)).start();
			(new NotifyThread(3000)).start();
			(new NotifyThread(3500)).start();


			(new NotifyTimeToStartThread(4000)).start();
			 */
		}

		@Override
		protected String doInBackground(Void... params) {

			informSlaves();
			t = new Timer();
			t.schedule(new PlayTask(), timeToStart- System.currentTimeMillis());

			return null;
		}


	}
	protected void onDestroy()
	{
		super.onDestroy();
	}

	class PlayTask extends TimerTask {
		public void run() {
			mHandler.obtainMessage(1).sendToTarget();
			t.cancel(); //Terminate the timer thread
		}
	}
	public Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	yCtrl.resumeMusic();
			playing=true; //this is the textview
	    }
	};
	
	
	class CalculateDelayThread extends Thread {
		int sleep;
		public CalculateDelayThread (int sleep){
			this.sleep = sleep;
		}
		public void run() {
			try {
				sleep(sleep); // 5000 mil secs = 5 secs . sleeps thread
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					for (ReaderWriterPair pair : connections.values()){
						long messageTime;
						long dif;
						pair.writer.println((messageTime = System.currentTimeMillis()));
						pair.writer.flush();
						pair.reader.readLine();
						long retreivedTime =System.currentTimeMillis();

						dif =  retreivedTime > messageTime ? retreivedTime - messageTime : 0;
						pair.writer.println(dif);
						System.out.println(retreivedTime +" "+ messageTime +"  " + dif);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class NotifyThread extends Thread {
		int sleep;
		public NotifyThread (int sleep){
			this.sleep = sleep;
		}
		public void run() {
			try {
				sleep(sleep); // 5000 mil secs = 5 secs . sleeps thread
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				for (ReaderWriterPair pair : connections.values()){
					pair.writer.println(System.currentTimeMillis());
				}
			}
		}
	}

	class NotifyTimeToStartThread extends Thread {
		int sleep;
		public NotifyTimeToStartThread  (int sleep){
			this.sleep = sleep;
		}
		public void run() {
			try {
				sleep(sleep); // 5000 mil secs = 5 secs . sleeps thread
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				for (ReaderWriterPair pair: connections.values()){
					pair.writer.println(timeToStart);
				}
			}
		}
	}

}
