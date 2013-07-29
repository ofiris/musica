package postpc.musica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */


public class WaitingActivity extends ParentActivity {
	private View layoutView;

	//for client	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private CreateCommunicationClient communicate;
	private WifiP2pInfo info;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_waiting_for_song);
		info = ((CommunicationBinder)getApplication()).info;
		String [] host = {info.groupOwnerAddress.getHostAddress()};
		communicate = new CreateCommunicationClient(this, layoutView);
		communicate.execute(host);
		TextView txtView = (TextView) findViewById(R.id.wait_for_song);
		String str = "Waiting for\n " + ((CommunicationBinder)getApplication()).owner + " to\nchoose\na\nsong";
		txtView.setText(str);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (socket !=null){
			readFromUserClient();
		}
	}
	

	private void readFromUserClient() {
		ReadFromMaster in = new ReadFromMaster(this, layoutView);
		in.execute();

	}
	
	private void goToPlayIntent(String receivedMsg) {
		Intent intent = new Intent(this, SlavePlayActivity.class);
		intent.putExtra("youTubeId",receivedMsg); //TODO
		this.startActivity(intent);
	}


	public class CreateCommunicationClient extends AsyncTask<String, Void, Void> {
		/**
		 * @param context
		 * @param statusText
		 */
		public CreateCommunicationClient(Context context, View statusText) {
		}
		@Override
		protected Void doInBackground(String... params) {
			socket = new Socket();
			try {
				socket.bind(null);
				String host = params[0];
				socket.connect((new InetSocketAddress(host, 8989)),0); //TODO fals here when was ysed and now master is not connected
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				CommunicationBinder cb = (CommunicationBinder) getApplication();
				cb.reader = reader;
				cb.writer = new PrintWriter(socket.getOutputStream(),true);
				cb.readerSocket = socket;
				System.out.println("connection made");
			} catch (IOException e) {

				System.out.println("in catch");
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			readFromUserClient();
		}

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
				return reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String receivedMsg) {
			//System.out.println(receivedMsg);
			communicate = null;
			goToPlayIntent(receivedMsg);
		}


	}


}
