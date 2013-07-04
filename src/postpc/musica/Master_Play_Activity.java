package postpc.musica;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

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

public class Master_Play_Activity extends Activity {

	private boolean mIsBound = false;
	private MusicService mServ;
	boolean playing = false;
	HashMap <Socket,PrintWriter> connections;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CommunicationBinder myCom = (CommunicationBinder) getApplication();
		connections =myCom.connections;
		setContentView(R.layout.activity_master_play);
		/*
		 * Bind service call, starts the service if it didn't start yet
		 */
		Intent intent1 = new Intent(this,MusicService.class);
		bindService(intent1,_scon,Context.BIND_AUTO_CREATE);
		mIsBound = true;

		final Button button = (Button) findViewById(R.id.button1);



		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(playing==false && mServ!=null)
				{
					//informSlaves();
					button.setText("Pause!");
					mServ.resumeMusic();
					playing=true;
					

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

	private void informSlaves() {
		for (PrintWriter pw : connections.values()){
			pw.println("start playing");
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


}
