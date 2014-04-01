package com.example.androidclientapplication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ResourceBundle.Control;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** Main Activity

    Represents the first activity a user sees. The user enters the IP-address
    of the server running on the Raspberry Pi and the port number that the
    server is listening at.

    Presents a simple UI providing text views for the user to provide the IP-address
    and port number as well as a button whose action method attempts to connect to the server.
    If connection is successful, the second activity starts, otherwise displays an error message
    and asks the user to re-enter the IP-address and port number.

 */
public class Client extends Activity{

	private static int SERVERPORT;
	private static String SERVER_IP;
	private static final String TAG = "ClientActivity";
	private static final int delay = 100;
	
	private Handler mHandler = new Handler();

	private Runnable forward = new Runnable(){
		public void run(){
			new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Up-P");
			mHandler.postAtTime(this, SystemClock.uptimeMillis()+delay);
		}
	};

	private Runnable reverse = new Runnable(){
		public void run(){
			new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Down-P");
			mHandler.postAtTime(this, SystemClock.uptimeMillis()+delay);
		}
	};
	
	private Runnable right = new Runnable(){
		public void run(){
			new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Right-P");
			mHandler.postAtTime(this, SystemClock.uptimeMillis()+delay);
		}
	};
	
	private Runnable left = new Runnable(){
		public void run(){
			new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Left-P");
			mHandler.postAtTime(this, SystemClock.uptimeMillis()+delay);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if(savedInstanceState != null){
			SERVER_IP = savedInstanceState.getString("IP");
			SERVERPORT = savedInstanceState.getInt("PORT");

		}else{		
			Intent intent = getIntent();
			SERVER_IP = intent.getStringExtra(
					"ServerIP");
			SERVERPORT = intent.getIntExtra("ServerPort", 0);
		}
	}


	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		Log.i(TAG, "onSaveInstanceState");
		savedInstanceState.putInt("PORT", SERVERPORT);
		savedInstanceState.putString("IP", SERVER_IP);
	}
	/*
	public void disconnectAction(View view) throws IOException{
		Button disconnectButton = (Button) findViewById(R.id.disconnectButton);
		disconnectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
	 */

	/* Called when the user clicks one of the four directional buttons */
	public void buttonAction(View view) throws IOException {

		// Assigns the four directional buttons to Button objects
		Button upButton = (Button) findViewById(R.id.forwardButton);
		Button leftButton = (Button) findViewById(R.id.leftButton);
		Button rightButton = (Button) findViewById(R.id.rightButton);
		Button downButton = (Button) findViewById(R.id.reverseButton);

		// Interface definition for a callback to be invoked when a touch event is dispatched to this view
		// * Need separate onTouchListeners for each button
		upButton.setOnTouchListener(new OnTouchListener() {

			// Function called when a touch event is dispatched to a view
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Up-P");
					mHandler.removeCallbacks(forward);
					mHandler.postAtTime(forward, SystemClock.uptimeMillis()+delay);
				}else if (action == MotionEvent.ACTION_UP){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Up-R");
					mHandler.removeCallbacks(forward);
				}
				return false;
			}	
		});

		leftButton.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Left-P");
					mHandler.removeCallbacks(left);
					mHandler.postAtTime(left, SystemClock.uptimeMillis()+delay);
				}else if (action == MotionEvent.ACTION_UP){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Left-R");
					mHandler.removeCallbacks(left);
				}
				return false;
			}	
		});

		rightButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Right-P");
					mHandler.removeCallbacks(right);
					mHandler.postAtTime(right, SystemClock.uptimeMillis()+delay);
				}else if (action == MotionEvent.ACTION_UP){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Right-R");
					mHandler.removeCallbacks(right);
				}
				return false;
			}	
		});

		downButton.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Down-P");
					mHandler.removeCallbacks(reverse);
					mHandler.postAtTime(reverse, SystemClock.uptimeMillis()+delay);
				}else if (action == MotionEvent.ACTION_UP){
					new ClientAsyncTask().execute(SERVER_IP, String.valueOf(SERVERPORT), "Down-R");
					mHandler.removeCallbacks(reverse);
				}
				return false;
			}	
		});
	}

	// Private inner class to set up ClientThread

	/** Starting the thread causes the run method to be called in a separately executing thread.
	    Function attempts to create a stream socket and connect it to the user-specified port number
	    at the user-specified IP address */
	private class ClientAsyncTask extends AsyncTask<String,Void,Void> {

		// Create the DatagramSocket
		DatagramSocket socket;

		@Override
		protected Void doInBackground(String... connectionParams) {

			String server_ip    = connectionParams[0];
			String server_port  = connectionParams[1];
			String direction    = connectionParams[2];

			try{
				InetAddress IP = InetAddress.getByName(server_ip);
				byte[] message = direction.getBytes();

				// Create the socket
				socket = new DatagramSocket();

				// Create the Packet
				DatagramPacket packet = new DatagramPacket(message, message.length, IP, Integer.parseInt(server_port));

				// Send the Packet
				socket.send(packet);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute() {
			socket.close();
		}
	}
}
