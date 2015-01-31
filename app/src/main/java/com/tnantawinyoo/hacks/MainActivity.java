package com.tnantawinyoo.hacks;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "CamTestActivity";

    protected ArrayList<Prompt> prompts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);
        openCamera();
        loadPrompt();
        String prompt = pickPrompt();
        FrameLayout layout = (FrameLayout) findViewById(R.id.layout);
        mView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout rLayout = new RelativeLayout(layout.getContext());
        TextView thisPrompt = new TextView(layout.getContext());
        thisPrompt.setText(prompt);
        thisPrompt.setBackgroundColor(0xFF000000);
        thisPrompt.setTextColor(0xFFFFFFFF);
        thisPrompt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        thisPrompt.setId(1);
        thisPrompt.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        thisPrompt.setTextSize(20.0F);
        rLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        Button thisButton = new Button(layout.getContext());
        thisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prompt = pickPrompt();
                TextView thisPrompt = (TextView) findViewById(1);
                thisPrompt.setText(prompt);
                camera.stopPreview();
                camera.takePicture(null, null, mPicture);
                Log.d(TAG,"GGGG");


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

    @SuppressWarnings( "deprecation" )
    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG,"swag");
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                camera.stopPreview();
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                camera.startPreview();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
        }
    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @SuppressWarnings( "deprecation" )
    public Camera camera;
    public CameraView mView;
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
            Camera.Parameters mParameters = camera.getParameters();
            Camera.Size bestSize = null;
            List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
            bestSize = sizeList.get(0);

            for(int i = 1; i < sizeList.size(); i++){
                if((sizeList.get(i).width * sizeList.get(i).height) >
                        (bestSize.width * bestSize.height)){
                    bestSize = sizeList.get(i);
                }
            }

            mParameters.setPreviewSize(bestSize.width, bestSize.height);
            camera.setParameters(mParameters);
            camera.startPreview();

        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            try {
                camera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                camera.release();
            }
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            camera.stopPreview();
            camera.release();

        }
    }



    protected void openCamera(){
        camera = Camera.open(0);
        camera.stopPreview();
        camera.setDisplayOrientation(90);
        camera.startPreview();
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

    class Prompt{
        public String action;
        public int id;
        public Prompt(String action, int id){
            this.action = action;
            this.id = id;
        }
    }

    protected void loadPrompt(){
        Prompt prompt1 = new Prompt("Silly face", 1);
        Prompt prompt2 = new Prompt("This works", 2);
        Prompt prompt3 = new Prompt("This really works",3);
        prompts.add(prompt1);
        prompts.add(prompt2);
        prompts.add(prompt3);
    }
    protected String pickPrompt(){
        Random rand = new Random();
        int sizeOfArrayList = prompts.size();
        int randomInt = rand.nextInt(sizeOfArrayList);
        return prompts.get(randomInt).action;
    }
}
