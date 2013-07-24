package postpc.musica;

import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectBroadcasterActivity extends Activity {

	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	Collection<WifiP2pDevice> currentDevices;
	private CommunicationBinder myCom;
	private boolean mIsBound = false;
	private Master_Get_Connection mServ;
	TextView connectionsTextView;
	/*
	 * Service Connection implementation for the activity to
	 * use the binder, when the service is ready
	 */
	private ServiceConnection _scon = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder
				binder) {
			mServ = ((Master_Get_Connection.ServiceBinder)binder).getService();
			mServ.sendTextView(connectionsTextView);
			mIsBound = true;
			myCom.mServ = mServ;
			System.out.println("mServ = " + mServ);
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_broadcaster);
		mIntentFilter = new IntentFilter();
		Toast.makeText(this, "After all speakers\nconnected press\n\"Choose Song\"", Toast.LENGTH_LONG).show();
		mReceiver = new BroadcastReceiverBroadcaster(mManager, mChannel, this);
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		myCom = (CommunicationBinder) getApplication();
		myCom.mChannel = mChannel;
		myCom.mManager = mManager;
		myCom.mReceiver = mReceiver;
		
		Button b = (Button)findViewById(R.id.chooseSong);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				
				getActivity().startActivity(intent);
				
			}
		});
		connectionsTextView = (TextView)findViewById(R.id.numOfConnections);
		Intent intent = new Intent(this,Master_Get_Connection.class);
		bindService(intent,_scon,Context.BIND_AUTO_CREATE);
		mManager.removeGroup(mChannel, new RemoveGroupListener());

	}
	
	private Activity getActivity(){
		return this;
	}
	private class RemoveGroupListener implements ActionListener{

		@Override
		public void onFailure(int arg0) {
			mManager.cancelConnect(mChannel, new CancelConnectionsListener());
		}

		@Override
		public void onSuccess() {
			mManager.cancelConnect(mChannel, new CancelConnectionsListener());
			
			
		}
		
	}
	
	private class CreateGroupListener implements ActionListener{

		@Override
		public void onFailure(int arg0) {
			startWifiActivity();
		}

		@Override
		public void onSuccess() {
			startWifiActivity();
			
			
		}
		
	}
	
	private class CancelConnectionsListener implements ActionListener{

		@Override
		public void onFailure(int arg0) {
			mManager.createGroup(mChannel, new CreateGroupListener());
		}

		@Override
		public void onSuccess() {
			mManager.createGroup(mChannel, new CreateGroupListener());
		}	
	}
	
	
	
	private void startWifiActivity (){
		unregisterReceiver(mReceiver);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		registerReceiver(mReceiver, mIntentFilter);
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
		super.onResume();
		mReceiver = new BroadcastReceiverBroadcaster(mManager, mChannel, this);
		registerReceiver(mReceiver, mIntentFilter);

	}

	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	protected void onDestroy()
	{
		super.onDestroy();
		//Unbound with service, destroys service if no one else touched it
		if(mIsBound)
		{
		
			unbindService(_scon);
			mIsBound = false;
		}
		
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}


}
