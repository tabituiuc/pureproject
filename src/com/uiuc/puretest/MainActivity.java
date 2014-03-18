package com.uiuc.puretest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button serverSide;
	Button clientSide;
	
	Button foo;
	
	int a;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		serverSide = (Button) findViewById(R.id.enterServer);
		clientSide = (Button) findViewById(R.id.enterClient);
		
		
		serverSide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startServer();
				
			}
		});
		clientSide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startClient();
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void startServer() {
		
		Intent server = new Intent(this, ServerActivity.class);
		startActivity(server);
	}
	
	private void startClient() {
		Intent client = new Intent(this, ClientActivity.class);
		startActivity(client);
	}

}
