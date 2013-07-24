package postpc.musica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import postpc.musica.CommunicationBinder.ReaderWriterPair;
import postpc.musica.MusicService.ServiceBinder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Master_Get_Connection extends Service {

	private final IBinder mBinder = new ServiceBinder();
	
	private HashMap<Socket, CommunicationBinder.ReaderWriterPair> connections;
	private Intent intent;
	private ServerSocket serverSocket;
	private TextView connectionsTextView;
	private boolean inPlayMode = false;
	public Master_Get_Connection() { 
		
		
	}

	/*
	 * This special binder returns the service object in
	 * its method getService()
	 * so the activity can use the service public methods
	 */
	public class ServiceBinder extends Binder {
		Master_Get_Connection getService()
		{
			return Master_Get_Connection.this;
		}
	}

	/*
	 *  implement the onBind() callback method for the bound service.
	 *  This method returns an IBinder object that defines the
	 *  programming interface that clients can use to 
	 *  interact with the service.
	 */
	public IBinder onBind(Intent intent)
	{
		this.intent= intent;  
		return mBinder;
	}

	@Override
	public void onCreate (){
		super.onCreate();
		CommunicationBinder myCom = (CommunicationBinder) getApplication();
		myCom.connections = new HashMap<Socket, ReaderWriterPair>();
		connections = myCom.connections;
		(new CreateCommunicationGroupOwner(null,null)).execute();
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

	@Override
	public void onDestroy ()
	{
		super.onDestroy();
		//TODO
	}

	ContinueCommunicationGroupOwner thread;
	private void continueListening() {
		System.out.println("listening again");
		thread = new ContinueCommunicationGroupOwner(null, null);
		thread.execute();

	}
	
	public class CreateCommunicationGroupOwner extends AsyncTask<Void, Void, Void> {

		/**
		 * @param context
		 * @param statusText
		 */
		public CreateCommunicationGroupOwner(Context context, View statusText) {
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				serverSocket = new ServerSocket(8989);
				serverSocket.setSoTimeout(1000);
				System.out.println("connection made");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void v) {
			continueListening(); 
			
		}

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
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ReaderWriterPair pair = new CommunicationBinder.ReaderWriterPair(reader, writer);
				connections.put(socket, pair);
				
				System.out.println("connection made connections length is:" + connections.size());
			}catch (SocketException e){
				return null;
			}catch (IOException e) {
				//e.printStackTrace();
				return null;
			}
			
			return socket;
		}
		@Override
		protected void onPostExecute(Socket socket) {
			//if (socket != null)
			//Toast.makeText(getApplicationContext(), "connect", Toast.LENGTH_SHORT).show();
			if (!inPlayMode){
				connectionsTextView.setText("Number of speakers connected: " + connections.size());
				continueListening();
			}
			
		}
	}
	
	public void startedPlayMode(){
		inPlayMode = true;
		for (ReaderWriterPair pair: connections.values()){
			//TODO
			pair.writer.println("go to next intent");
			pair.writer.flush();
		}
		
		
	}

	public void sendTextView(TextView connectionsTextView) {
		this.connectionsTextView =  connectionsTextView;
		
	}

}
