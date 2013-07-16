package postpc.musica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import postpc.musica.Master_Play_Activity.PlayTask;

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
import android.widget.TextView;

public class SlavePlayActivity extends ParentActivity{
	private boolean mIsBound = false;
	private MusicService mServ;
	boolean playing = false;
	BufferedReader br;
	PrintWriter pw;
	Timer timer = new Timer();

	/*
	 * Service Connection implementation for the activity to
	 * use the binder, when the service is ready
	 */
	private ServiceConnection Scon =new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder
				binder) {
			mServ = ((MusicService.ServiceBinder)binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slave_play);

		/*
		 * Bind service call, starts the service if it didn't start yet
		 */
		Intent intent = new Intent(this,MusicService.class);
		intent.putExtra("right", 100);
		intent.putExtra("left", 100);
		bindService(intent,Scon,Context.BIND_AUTO_CREATE);
		mIsBound = true;
		br = ((CommunicationBinder)getApplication()).reader;
		pw = ((CommunicationBinder)getApplication()).writer;
		final TextView rep = (TextView) findViewById(R.id.text);

		ReadFromMaster r = new ReadFromMaster(this, null);
		r.execute();

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
				messageDelayTime=Math.max(90,bestMessageDelayTime);
				System.out.println("timer "+(masterTime - System.currentTimeMillis() - bestClockDifference - messageDelayTime));
				timer.schedule(new PlayTask(), masterTime - System.currentTimeMillis() - bestClockDifference - messageDelayTime);
				/*long tmp = Long.parseLong(br.readLine());
				pw.println(System.currentTimeMillis());
				pw.flush();
				String tmpStr1 = br.readLine();
				messageDelayTime1 = Long.parseLong(tmpStr1);
				System.out.println("Str1 " + tmpStr1);

				tmp = Long.parseLong(br.readLine());
				pw.println(System.currentTimeMillis());
				pw.flush();
				String tmpStr2 = br.readLine();
				messageDelayTime2 = Long.parseLong(tmpStr2);
				System.out.println("Str2 " + tmpStr2);


				tmp = Long.parseLong(br.readLine());
				pw.println(System.currentTimeMillis());

				String tmpStr3 = br.readLine();
				messageDelayTime3 = Long.parseLong(tmpStr3);
				System.out.println("Str3 " + tmpStr3);		

				//parsingTime = System.currentTimeMillis();
				avg1 = Long.parseLong(br.readLine()) - System.currentTimeMillis();
				//parsingTime = System.currentTimeMillis() - parsingTime;
				avg2 = Long.parseLong(br.readLine()) - System.currentTimeMillis();

				avg3 = Long.parseLong(br.readLine()) - System.currentTimeMillis();
				avg4 = Long.parseLong(br.readLine()) - System.currentTimeMillis();

				avg5 = Long.parseLong(br.readLine()) - System.currentTimeMillis();

				averageDifference = (avg4 +avg5)/2;
				masterTime = Long.parseLong(br.readLine());
				messageDelayTime = (long) (messageDelayTime1 + messageDelayTime2 + messageDelayTime3)/3;
				timer.schedule(new PlayTask(), masterTime - System.currentTimeMillis() - averageDifference - messageDelayTime);*/
				return "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			return null;
		}

		@Override
		protected void onPostExecute(String receivedMsg) {


			System.out.println("clock " + bestClockDifference);
			System.out.println("delay used "+ messageDelayTime);
			System.out.println("real delay "+ bestMessageDelayTime);
		}


	}	
	class PlayTask extends TimerTask {
		public void run() {
			mServ.resumeMusic();
			playing=true;
			timer.cancel(); //Terminate the timer thread
		}
	}

	protected void onDestroy()
	{
		//Unbound with service, destroys service if no one else touched it
		if(mIsBound)
		{
			unbindService(Scon);
			mIsBound = false;
		}
		super.onDestroy();
	}


}
