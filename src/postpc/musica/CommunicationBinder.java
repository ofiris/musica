package postpc.musica;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import android.app.Application;

public class CommunicationBinder extends Application {
	public HashMap <Socket,PrintWriter> connections;
	public BufferedReader reader;
	public Socket readerSocket;

 }

