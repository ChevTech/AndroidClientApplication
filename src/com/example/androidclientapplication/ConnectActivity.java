package com.example.androidclientapplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class ConnectActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect);			
		}

	public void connect(View view){
		Intent intent = new Intent(this, Client.class);
		EditText ServIP = (EditText) findViewById(R.id.IP);
		EditText ServPrt = (EditText) findViewById(R.id.PORT);
		intent.putExtra("ServerIP", ServIP.getText().toString());
		intent.putExtra("ServerPort", Integer.parseInt(ServPrt.getText().toString()));
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connect, menu);
		return true;
	}

}
