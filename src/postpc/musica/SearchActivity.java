package postpc.musica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import postpc.musica.CommunicationBinder.ReaderWriterPair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends ParentActivity {

	private  ListView youTubeList;
	private ArrayAdapter<youTubeEntery> listAdapter;
	private int curListPlace=1;
	private String querry = "";
	private boolean mIsBound = false; //TODO remove
	private Master_Get_Connection mServ;
	/*
	 * Service Connection implementation for the activity to
	 * use the binder, when the service is ready
	 */
	private ServiceConnection _scon = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder
				binder) {
			mServ = ((Master_Get_Connection.ServiceBinder)binder).getService();
			mIsBound = true;
			System.out.println("mServ = " + mServ);
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		List<youTubeEntery> list = new ArrayList<youTubeEntery>();
		youTubeList = (ListView)findViewById(R.id.youTubeList);     
		
		
		
		
		mIsBound = true;
		listAdapter = new listAdapt(this, list);
		youTubeList.setAdapter(listAdapter);
		registerForContextMenu(youTubeList);

		Button searchButton = (Button) findViewById(R.id.searchButton);
		final EditText textIn = (EditText)findViewById(R.id.input);

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listAdapter.clear();
				String in= textIn.getText().toString();
				String inForUrl = "";
				try {
					inForUrl = java.net.URLEncoder.encode(in, "utf8");
				} catch (UnsupportedEncodingException e) {
					System.out.println("error: " + e.toString());
				}
				curListPlace = 1;
				int numOfRes = Consts.searchNum;
				querry = "http://gdata.youtube.com/feeds/api/videos?q="+inForUrl;
				String fullQuerry=querry + "&alt=json&start-index="+curListPlace+
						"&max-results="+(curListPlace + numOfRes);
				new getSearchList(listAdapter).execute(fullQuerry);
			}
		});
		Button nextButton = (Button) findViewById(R.id.next);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listAdapter.clear();
				int numOfRes = Consts.searchNum;
				curListPlace += numOfRes;
				String fullQuerry=querry + "&alt=json&start-index="+curListPlace+
						"&max-results="+(curListPlace + numOfRes);
				new getSearchList(listAdapter).execute(fullQuerry);
			}
		});
		Button prevButton = (Button) findViewById(R.id.prev);
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(curListPlace == 1) return;
				listAdapter.clear();
				int numOfRes = Consts.searchNum;
				curListPlace -= numOfRes;
				String fullQuerry=querry + "&alt=json&start-index="+curListPlace+
						"&max-results="+(curListPlace + numOfRes);
				new getSearchList(listAdapter).execute(fullQuerry);
			}
		});
		
		//TODO add in click for list
		
		//TODO this botton is for testing
		Button backButton = (Button) findViewById(R.id.back);
		final Context mActivity = this;
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mServ.startedPlayMode();
				Intent intent = new Intent(mActivity, MasterYouTubePlayActivity.class);
				intent.putExtra("youTubeId","ZKEaypYJbb4"); //TODO 
				mActivity.startActivity(intent);
				
			}
		});
		Intent intent = new Intent(this,Master_Get_Connection.class);
		bindService(intent,_scon,Context.BIND_AUTO_CREATE);

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
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
	
}
