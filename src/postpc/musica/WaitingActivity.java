package postpc.musica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */


public class WaitingActivity extends Activity {
	private View layoutView;

	//for client	
	private Socket socket;
	private BufferedReader reader;

	private WifiP2pInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		info = getIntent().getParcelableExtra("info");
		setContentView(R.layout.activity_waiting_for_song);
		String [] host = {info.groupOwnerAddress.getHostAddress()};
		Toast.makeText(this, "I am not the group owner", Toast.LENGTH_LONG).show();
		new CreateCommunicationClient(this, layoutView).execute(host);
		
	}

	private void readFromUserClient() {
		ColorChangerMessangerInClient in = new ColorChangerMessangerInClient(this, layoutView);
		in.execute();

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



	

	public class ColorChangerMessangerInClient extends AsyncTask<Void, Void, String> {

		/**
		 * @param context
		 * @param statusText
		 */
		public ColorChangerMessangerInClient(Context context, View view) {
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
			System.out.println(receivedMsg);
			readFromUserClient();
		}
	}


}
