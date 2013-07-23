package postpc.musica;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class CommunicationBinder extends Application {
	public HashMap <Socket,ReaderWriterPair> connections;
	public BufferedReader reader;
	public Socket readerSocket;
	public PrintWriter writer;
	
	public WifiP2pManager mManager;
	public Channel mChannel;
	public BroadcastReceiver mReceiver;
	
	public static class ReaderWriterPair {
		BufferedReader reader;
		PrintWriter writer;
		public ReaderWriterPair (BufferedReader reader, PrintWriter writer){
			this.reader = reader;
			this.writer = writer;
		}
	}
}

