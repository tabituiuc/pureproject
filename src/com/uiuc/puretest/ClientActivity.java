package com.uiuc.puretest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ClientActivity extends Activity {
	
	private ImageView imageView;
	private Bitmap fileRead;
	private InputStream is;
	
	 private EditText serverIp;
	 
	 private Button connectPhones;
	 
	 private String serverIpAddress = "";
	 
	 private boolean connected = false;
	 
	 private Handler handler = new Handler();
	 
	 private Socket socket;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		
		serverIp = (EditText) findViewById(R.id.serverip);
	    connectPhones = (Button) findViewById(R.id.sendButton);
	    connectPhones.setOnClickListener(connectListener);
		
	    imageView = (ImageView) findViewById(R.id.toSend);
		try {
			is = getAssets().open("test.png");
		} catch (IOException e) {
			Log.d("Local", "Image Open Error");
			e.printStackTrace();
		}
		fileRead = BitmapFactory.decodeStream(is);
		imageView.setImageBitmap(fileRead);

	}

	private OnClickListener connectListener = new OnClickListener() {
		 
        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
        }
    };
    
    public class ClientThread implements Runnable {
    	 
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                socket = new Socket(serverAddr, ServerActivity.SERVERPORT);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                       handler.post(new Runnable(){

						@Override
						public void run() {
							
							try {
								fileRead.compress(Bitmap.CompressFormat.PNG, 100, socket.getOutputStream());
							} catch (IOException e) {
								Log.e("ClientActivity", "C: Error");
								e.printStackTrace();
							}
							
							
						}
                    	   
                       });
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client, menu);
		return true;
	}

}
