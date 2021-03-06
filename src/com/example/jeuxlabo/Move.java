package com.example.jeuxlabo;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.Window;

public class Move extends Activity implements SensorEventListener {
	protected PowerManager.WakeLock vWakeLock;
//public class Move extends Activity  {
    /** Called when the activity is first created. */
     // Just a RANDOM ID to recognize a Message later 
    protected static final int GUIUPDATEIDENTIFIER = 0x101; 
    SensorManager sensorManager; 
    Thread myRefreshThread = null; 

    /* Our 'ball' is located within this View */ 
    BounceView myBounceView = null; 

    Handler myGUIUpdateHandler = new Handler() { 

         // @Override 
         public void handleMessage(Message msg) { 
              switch (msg.what) { 
                   case Move.GUIUPDATEIDENTIFIER: 
                        /* Repaint the BounceView 
                         * (where the ball is in) */ 
                       myBounceView.invalidate(); 
                        break; 
              } 
              super.handleMessage(msg); 
         } 
    }; 

    /** Called when the activity is first created. */ 
    @SuppressWarnings("deprecation")
	@Override 
    public void onCreate(Bundle icicle) { 
         super.onCreate(icicle); 
         // Set fullscreen 
         this.requestWindowFeature(Window.FEATURE_NO_TITLE); 

         // Create a 
         this.myBounceView = new BounceView(this); 
         this.setContentView(this.myBounceView); 
         sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
         //updateOrientation(0, 0, 0);
         /* create a Thread that will 
          * periodically send messages 
          * to our Handler */ 
         new Thread(new RefreshRunner()).start();
         // L'application bloque la mise en veille (voir onDestroy pour la relacher)
         final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         this.vWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
         this.vWakeLock.acquire();
    } 

    class RefreshRunner implements Runnable { 
         // @Override 
         public void run() { 
              while (!Thread.currentThread().isInterrupted()) { 
                   // Send Message to the Handler which will call the invalidate() method of the BOucneView 
                   Message message = new Message(); 
                   message.what = Move.GUIUPDATEIDENTIFIER; 
                   Move.this.myGUIUpdateHandler.sendMessage(message); 

                   try { 
                        Thread.sleep(50); // a 10th of a second 
                   } catch (InterruptedException e) { 
                        Thread.currentThread().interrupt(); 
                   } 
              } 
         } 
    } 


    protected void onResume() 
    { 
      super.onResume(); 
      sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST); 
    } 

    protected void onStop() 
    { 
        sensorManager.unregisterListener(this); 
      super.onStop(); 
    } 
    
    // surchage de onDestroy qui est appel� quand on quitte l'application
    // ici on relache l'interdiction de mise en veille
    public void onDestroy() {
    	// on remet la veille ecran comme avant
        this.vWakeLock.release();
        super.onDestroy();
    }


     public void onAccuracyChanged(Sensor sensor, int accuracy) { 
     } 

    public void onSensorChanged(SensorEvent event) { 
        myBounceView.mPitch=event.values[SensorManager.DATA_Z];
        myBounceView.mHeading=event.values[SensorManager.DATA_Y];
        
         //updateOrientation(event.values[SensorManager.DATA_X], event.values[SensorManager.DATA_Y], event.values[SensorManager.DATA_Z]); 
          
    } 



}