package com.uiuc.puretest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ClientActivity extends Activity 
implements LoaderCallbackInterface {
	
	private ImageView imageView;
	private Bitmap fileRead;
	private InputStream is;
	
	 private EditText serverIp;
	 
	 private Button connectPhones;
	 
	 private String serverIpAddress = "";
	 
	 private boolean connected = false;
	 
	 private Handler handler = new Handler();
	 
	 private Socket socket;
	 
	 private boolean openCVLoaded = false;
	 
	 private BitmapFactory.Options bmpFactoryOptions;
	 
	 private CascadeClassifier cascadeClassifier;
	
	 private BaseLoaderCallback mOpenCVCallback = new BaseLoaderCallback(this) {
		 @Override
		 public void onManagerConnected(int status) {
		     switch (status) {
		         case LoaderCallbackInterface.SUCCESS:
		         {
		        	 try {
		                 // Copy the resource into a temp file so OpenCV can load it
		                 InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
		                 File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
		                 File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
		                 FileOutputStream os = new FileOutputStream(mCascadeFile);
		      
		      
		                 byte[] buffer = new byte[4096];
		                 int bytesRead;
		                 while ((bytesRead = is.read(buffer)) != -1) {
		                     os.write(buffer, 0, bytesRead);
		                 }
		                 is.close();
		                 os.close();
		      
		      
		                 // Load the cascade classifier
		                 cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
		             } catch (Exception e) {
		                 Log.e("OpenCVActivity", "Error loading cascade", e);
		             }
		        	 openCVLoaded = true;
		        	 
		         } break;
		         default:
		         {
		             super.onManagerConnected(status);
		         } break;
		     }
		 }
		 };
		 
		 private OnClickListener connectListener = new OnClickListener() {
			 
		        @Override
		        public void onClick(View v) {
		        	fileRead = manipulateImage(fileRead);
					imageView.setImageBitmap(fileRead);
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
										while(!openCVLoaded){
											Log.e("OpenCV", "OpenCVNotLoaded");
										}
										fileRead = manipulateImage(fileRead);
										imageView.setImageBitmap(fileRead);
										//send image
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
		    
    private Bitmap manipulateImage(Bitmap input){
    	
    	Mat matVer = new Mat();
    	Mat grayscaleImage = new Mat(input.getHeight(), input.getWidth(), CvType.CV_8UC4);
		Utils.bitmapToMat(input, matVer);
		Mat matVer2 = matVer.clone();
        Imgproc.cvtColor(matVer, grayscaleImage, Imgproc.COLOR_RGBA2RGB);
        
        
        MatOfRect faces = new MatOfRect();
 
      
        // Use the classifier to detect faces
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(grayscaleImage, faces);
        }
 
 
        // If there are any faces found, draw a rectangle around it
        Rect[] facesArray = faces.toArray();
        Rect face = facesArray[0];
        for (int i = 0; i <facesArray.length; i++)
            	face = facesArray[i];
        		//Core.rectangle(matVer, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
       
        
        matVer2 = new Mat(matVer, face);
        Bitmap input2 = Bitmap.createBitmap(matVer2.cols(), matVer2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matVer2, input2);
        
        
        return input2;
		//Rect faceRect = new Rect();
		// some code to detect face
		
    };
		    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		
		serverIp = (EditText) findViewById(R.id.serverip);
	    connectPhones = (Button) findViewById(R.id.sendButton);
	    connectPhones.setOnClickListener(connectListener);
	    bmpFactoryOptions = new BitmapFactory.Options();
	    bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    
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

	@Override
	public void onResume(){
		 super.onResume();
		 OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mOpenCVCallback);
	}

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client, menu);
		return true;
	}

	@Override
	public void onManagerConnected(int status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPackageInstall(int operation,
			InstallCallbackInterface callback) {
		// TODO Auto-generated method stub
		
	}
	


}
