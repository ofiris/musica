package postpc.musica;

import java.io.IOException;
import java.net.Socket;

import postpc.musica.CommunicationBinder.ReaderWriterPair;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public abstract class ParentActivity extends Activity {
	CommunicationBinder myCom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		myCom = (CommunicationBinder) getApplication();
		try {
			closeStreamsAndSockets();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		closeWifiDirect();
	}
	private void closeWifiDirect() {
		if (myCom.mReceiver != null)
			unregisterReceiver(myCom.mReceiver);
		if (myCom.mManager == null || myCom.mChannel == null)
			return;
		myCom.mManager.stopPeerDiscovery(myCom.mChannel, null);
		myCom.mManager.cancelConnect(myCom.mChannel, null);
		myCom.mManager.removeGroup(myCom.mChannel, null);
	}
	private void closeStreamsAndSockets() throws IOException {
		
		if (myCom.reader != null){
			myCom.reader.close();
		}
		if (myCom.writer != null){
			myCom.writer.close();
		}
		
		if ( myCom.connections == null) return;
		for (ReaderWriterPair pair : myCom.connections.values()){
			pair.reader.close();
			pair.writer.close();
		}
		for (Socket s: myCom.connections.keySet()){
			s.close();
		}
		
	}
	
	


}
