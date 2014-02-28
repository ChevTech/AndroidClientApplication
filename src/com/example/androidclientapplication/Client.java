package com.example.androidclientapplication;

 
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ResourceBundle.Control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// Main Activity
public class Client extends Activity {
	private Socket socket;
	private PrintWriter out;
    private static int SERVERPORT;
    private static String SERVER_IP;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
               
        Intent intent = getIntent();
        
        SERVER_IP = intent.getStringExtra("ServerIP");
        SERVERPORT = intent.getIntExtra("ServerPort", 0);
        
        new Thread(new ClientThread()).start();
       	}
    
    /* Called when the user clicks one of the four directional buttons */    
	public void buttonAction(View view) throws IOException {
		Button upButton = (Button) findViewById(R.id.upButton);
		Button leftButton = (Button) findViewById(R.id.leftButton);
		Button rightButton = (Button) findViewById(R.id.rightButton);
		Button downButton = (Button) findViewById(R.id.downButton);
		
		upButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {	
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						out.println("Up-P");
						return true;
					case MotionEvent.ACTION_MOVE:
						out.println("Up-P");
						break;
					case MotionEvent.ACTION_UP:
						out.println("Up-R");
						return true;
				}
				return false;
			}
		});
		
		leftButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {	
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						return true;
					case MotionEvent.ACTION_MOVE:
						out.println("Left-P");
						break;
					case MotionEvent.ACTION_UP:
						out.println("Left-R");
						return true;
				}
				return false;
			}
		});
		
		rightButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {	
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						return true;
					case MotionEvent.ACTION_MOVE:
						out.println("Right-P");
						break;
					case MotionEvent.ACTION_UP:
						out.println("Right-R");
						return true;
				}
				return true;
			}
		});
		
		downButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {	
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						return true;
					case MotionEvent.ACTION_MOVE:
						out.println("Down-P");
						break;
					case MotionEvent.ACTION_UP:
						out.println("Down-R");
						return true;
				}
				return false;
			}
		});
	}
 
   class ClientThread implements Runnable {
	   @Override
       public void run() {
		   try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
 
                socket = new Socket(serverAddr, SERVERPORT);
 
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
		   
	        try {
				out = new PrintWriter(new BufferedWriter(
				        new OutputStreamWriter(socket.getOutputStream())),
				        true);
			} catch (IOException e) {
				System.out.println("Problem with establishing OutputStream");
				e.printStackTrace();
			}
        }
    }
}
