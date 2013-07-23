package postpc.musica;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import postpc.musica.CommunicationBinder.ReaderWriterPair;

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
import android.widget.TextView;

//TODO now use the youTubeOne. 
public class MasterPlayActivity extends ParentActivity {

	private boolean mIsBound = false;
	private MusicService mServ;
	boolean playing = false;
	HashMap<Socket, ReaderWriterPair> connections;
	/*
	 * Service Connection implementation for the activity to
	 * use the binder, when the service is ready
	 */
	private ServiceConnection _scon = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder
				binder) {
			mServ = ((MusicService.ServiceBinder)binder).getService();
			System.out.println("mServ = " + mServ);
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};
	Intent intent1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_play);
		intent1 = new Intent(this,MusicService.class);
		intent1.putExtra("right", 100);
		intent1.putExtra("left", 100);
		bindService(intent1,_scon,Context.BIND_AUTO_CREATE);
		mIsBound = true;
		CommunicationBinder myCom = (CommunicationBinder) getApplication();
		connections =myCom.connections;
		/*
		 * Bind service call, starts the service if it didn't start yet
		 */


		final Button button = (Button) findViewById(R.id.button1);
		communicationTask = new CommunicateWithSlaves(); 
		//communicationTask.execute();

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(playing==false && mServ!=null)
				{
					communicationTask.execute();
					button.setText("Pause!");

				}
				else if(mServ!=null)
				{
					mServ.pauseMusic();
					playing=false;
					button.setText("Play!");
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
				timeToStart = System.currentTimeMillis() + 2000;
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
		//Unbound with service, destroys service if no one else touched it
		if(mIsBound)
		{
			unbindService(_scon);
			mIsBound = false;
		}
		super.onDestroy();
	}

	class PlayTask extends TimerTask {
		public void run() {
			mServ.resumeMusic();
			playing=true;
			t.cancel(); //Terminate the timer thread
		}
	}

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
