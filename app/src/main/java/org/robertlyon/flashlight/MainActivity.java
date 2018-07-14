package org.robertlyon.flashlight;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean mLightIsOn = false; //
    private boolean mIsStrobeOn = false;
    private boolean mHasCameraFlash;

    ImageButton toggleStrobe;

    Runnable runnable;
    Handler handler;

    public void turnOnOff(View v)
    {
        ImageButton onButton = (ImageButton) v;

        //Checks to see if the device has a camera
        //If not then a toast message is displayed to the user
        if(mHasCameraFlash)
        {
            mLightIsOn = !mLightIsOn;

            if(mLightIsOn)
            {
                try {
                    CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("Info", "Failed to get cam IDList");
                }
                onButton.setImageDrawable(getDrawable(R.drawable.button_down));
            }
            else
            {
                try {
                    CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, false);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.i("Info", "Failed to get cam IDList");
                }
                onButton.setImageDrawable(getDrawable(R.drawable.button_up));
            }
        }
        else
        {
            Toast.makeText(this,"Your device does not have a flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    public void strobeOnOff(View v)
    {
        toggleStrobe = (ImageButton) v;
        if(mLightIsOn) {
            mIsStrobeOn = !mIsStrobeOn;
            if (mIsStrobeOn) {
                toggleStrobe.setImageDrawable(getDrawable(R.drawable.strobe_button_down));
                runnable.run();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            handler = new Handler();
            runnable = new Runnable() {
                boolean strobe = true;
                @Override
                public void run() {
                    try {
                        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraId, strobe);
                        strobe = !strobe;
                        if(mLightIsOn && mIsStrobeOn) {
                            handler.postDelayed(this, 500);
                        }
                        else {
                            if (mLightIsOn)
                            {
                                cameraManager.setTorchMode(cameraId, true);
                            }
                            else
                            {
                                cameraManager.setTorchMode(cameraId, false);
                            }
                            mIsStrobeOn = false;
                            toggleStrobe.setImageDrawable(getDrawable(R.drawable.strobe_button_up));
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.i("Info", "Failed to get cam IDList");
                    }

                }
            };
        mHasCameraFlash = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
