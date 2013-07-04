package postpc.musica;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends Activity {

	private  ListView youTubeList;
	private ArrayAdapter<youTubeEntery> listAdapter;
	private int curListPlace=1;
	private String querry = "";
	private HashMap<Socket, PrintWriter> connections;
	private Parcelable info;
	private ServerSocket serverSocket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		createCommunication(savedInstanceState);
		List<youTubeEntery> list = new ArrayList<youTubeEntery>();
		youTubeList = (ListView)findViewById(R.id.youTubeList);     

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
		
		Button backButton = (Button) findViewById(R.id.back);
		final Context mActivity = this;
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				for (PrintWriter pw: connections.values()){
					pw.println("go to next intent");
					pw.flush();
				}
				
				Intent intent = new Intent(mActivity, Master_Play_Activity.class);
				intent.putExtra("song","some song"); //TODO
				mActivity.startActivity(intent);
			}
		});

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	
	private void createCommunication(Bundle b) {
		info = getIntent().getParcelableExtra("info");
		CommunicationBinder myCom = (CommunicationBinder) getApplication();
		myCom.connections = new HashMap<Socket, PrintWriter>();
		connections = myCom.connections;
		Toast.makeText(this, "I am group owner", Toast.LENGTH_LONG).show();
		Void [] params = new Void [1];
		new CreateCommunicationGroupOwner(this, null).execute(params);

	}
	
	public class CreateCommunicationGroupOwner extends AsyncTask<Void, Void, String> {
		
		/**
		 * @param context
		 * @param statusText
		 */
		public CreateCommunicationGroupOwner(Context context, View statusText) {
		}
		@Override
		protected String doInBackground(Void... params) {
			try {
				serverSocket = new ServerSocket(8989);
				System.out.println("connection made");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			continueListening();
		}
		
	}
	private void continueListening() {

		new ContinueCommunicationGroupOwner(this, null).execute();

	}
	public class ContinueCommunicationGroupOwner extends AsyncTask<Void, Void, Socket> {
		/**
		 * @param context
		 * @param statusText
		 */

		public ContinueCommunicationGroupOwner(Context context, View statusText) {
		}
		@Override
		protected Socket doInBackground(Void... params) {
			Socket socket = null;
			try {
				System.out.println("Going to listen to a new socket");
				socket = serverSocket.accept();
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				connections.put(socket, writer);
				System.out.println("connection made connections length is:" + connections.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return socket;
		}
		@Override
		protected void onPostExecute(Socket socket) {
			System.out.println("started post");
			continueListening();
			System.out.println("finshed post");
		}
	}
}
