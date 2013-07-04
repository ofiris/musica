package postpc.musica;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

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

public class SlavePlayActivity extends Activity {
	private boolean mIsBound = false;
	private MusicService mServ;
	boolean playing = false;
	BufferedReader br;

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
		bindService(intent,Scon,Context.BIND_AUTO_CREATE);
		mIsBound = true;
		br = ((CommunicationBinder)getApplication()).reader;
		final TextView rep = (TextView) findViewById(R.id.text);
		
		
	
	}
	
	public class ReadFromMaster extends AsyncTask<Void, Void, String> {

		/**
		 * @param context
		 * @param statusText
		 */
		public ReadFromMaster(Context context, View view) {
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				return br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String receivedMsg) {
			mServ.resumeMusic();
			System.out.println(receivedMsg);
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
