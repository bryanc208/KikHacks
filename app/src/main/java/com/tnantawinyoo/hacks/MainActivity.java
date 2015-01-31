package com.tnantawinyoo.hacks;

import android.app.ActionBar;
import android.app.Application;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.hardware.Camera.PictureCallback;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);
        openCamera();
        FrameLayout layout = (FrameLayout) findViewById(R.id.layout);
        mView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout rLayout = new RelativeLayout(layout.getContext());
        TextView thisPrompt = new TextView(layout.getContext());
        thisPrompt.setText("I'm a swag");
        thisPrompt.setBackgroundColor(0xFF000000);
        thisPrompt.setTextColor(0xFFFFFFFF);
        thisPrompt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        thisPrompt.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        thisPrompt.setTextSize(20.0F);
        rLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        Button thisButton = new Button(layout.getContext());
        thisButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
        rLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        thisButton.setText("Go!");
        thisButton.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rLayout.addView(thisButton);
        layout.addView(mView);
        layout.addView(thisPrompt);
        layout.addView(rLayout);
    }
    private PictureCallback mPicture = new PictureCallback() {

        protected File getOutputMediaFile(){
            File pictureFile = new File(getApplicationContext().getFilesDir(), "image.png");
            Log.v(STORAGE_SERVICE, getApplicationContext().getFilesDir().toString());
            return pictureFile;
        }
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            Log.v(STORAGE_SERVICE,"Swag");
            if (pictureFile == null){
                //Log.d(STORAGE_SERVICE, "Error creating media file, check storage permissions: " +
                  //      e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(STORAGE_SERVICE, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(STORAGE_SERVICE, "Error accessing file: " + e.getMessage());
            }
        }
    };
    protected Camera mCamera;
    protected CameraView mView;
    protected class CameraView extends SurfaceView implements SurfaceHolder.Callback{
        private SurfaceHolder mHolder;
        public CameraView(Context context){
            super(context);
            mHolder = this.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            setFocusable(true);

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Camera.Parameters mParameters = mCamera.getParameters();
            Camera.Size bestSize = null;

            List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
            bestSize = sizeList.get(0);

            for(int i = 1; i < sizeList.size(); i++){
                if((sizeList.get(i).width * sizeList.get(i).height) >
                        (bestSize.width * bestSize.height)){
                    bestSize = sizeList.get(i);
                }
            }

            mParameters.setPreviewSize(bestSize.width, bestSize.height);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();

        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                mCamera.release();
            }
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            mCamera.stopPreview();
            mCamera.release();

        }
    }



    protected void openCamera(){
        mCamera = Camera.open(0);
        mCamera.stopPreview();
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
        mView = new CameraView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
