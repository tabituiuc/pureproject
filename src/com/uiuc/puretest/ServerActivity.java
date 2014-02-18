package com.uiuc.puretest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class ServerActivity extends Activity {
	 private TextView serverStatus;
	 protected ImageView imageView;
	 
	    // DEFAULT IP
	 public static String SERVERIP = "10.10.0.1";
	 
	    // DESIGNATE A PORT
	 public static final int SERVERPORT = 8080;
	 
	 private Handler handler = new Handler();
	 
	private ServerSocket serverSocket;
	private Bitmap out = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
        serverStatus = (TextView) findViewById(R.id.server_status);
        imageView = (ImageView) findViewById(R.id.outputImage);
        SERVERIP = getLocalIpAddress();
 
        Thread fst = new Thread(new ServerThread());
        fst.start();
	}
	public class ServerThread implements Runnable {
		 
        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Connected.");
                            }
                        });
 
                        try {
                            out = BitmapFactory.decodeStream(client.getInputStream());
                            if (out != null) {
                            	handler.post(new Runnable() {
                            		public void run(){
                            			imageView.setImageBitmap(out);
                            		}
                            	});
                             break;
                            }
                        
                            
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }
 
    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() &&inetAddress instanceof Inet4Address) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server, menu);
		return true;
	}
	
	protected void onStop() {
        super.onStop();
        try {
             // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
             serverSocket.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

}
