package com.example.cse535part1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity {

    MediaController mediaControls;
    VideoView simpleVideoView;
    ActivityResultLauncher<Intent> startCamera;
    public static String param;
    public static int pos;
    public RecordActivity recordActivity;
    OkHttpClient okHttpClient;
    Uri videoUri;

    String[] fileNames= {"LightOn","LightOff","FanOn","FanOff","FanUp","FanDown","SetThermo","Num0","Num1","Num2","Num3","Num4"
            ,"Num5","Num6","Num7","Num8","Num9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        recordActivity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Bundle myBundle = getIntent().getExtras();
        if(myBundle!= null) {
            param = myBundle.getString("value");
            pos = myBundle.getInt("pos");
        }
        startCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {

                            System.out.println("Video caught");
                            Intent data = result.getData();
                            if(data!=null){
                                videoUri = data.getData();
                                String path = videoUri.getPath();
                                System.out.println(path);
                                if(simpleVideoView==null){
                                    simpleVideoView = findViewById(R.id.showCapturedVideo);
                                }
                                if(mediaControls==null){
                                    mediaControls = new MediaController(recordActivity);
                                    mediaControls.setAnchorView(simpleVideoView);
                                }
                                simpleVideoView.setMediaController(mediaControls);
                                simpleVideoView.setVideoURI(videoUri);
                                simpleVideoView.start();
                                System.out.println("video set");
                                simpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    public void onCompletion(MediaPlayer media) {
                                        media.reset();
                                        simpleVideoView.setVideoURI(videoUri);

                                    }
                                });
                            }
                        }
                    }
                }
        );
        practiceVideo();

        Button button = (Button) findViewById(R.id.sendVideo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                server();

            }
        });

        Button button2 = (Button) findViewById(R.id.replayVideo);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                simpleVideoView.start();
            }
        });


    }

    private void server(){
        okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.0.99:5000/").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecordActivity.this, "server down", Toast.LENGTH_SHORT).show();
                        System.out.println("does not work");
                    }
                });
            }

            @Override
            public void onResponse(
                    @NotNull Call call,
                    @NotNull Response response)
                    throws IOException {
                System.out.println("works");
                System.out.println(response.body().string());
                sendToServer();
                    }
        });
    }


    private void sendToServer() throws IOException {
        System.out.println("starting to send");
        String realPath=getRealPathFromURI(recordActivity,videoUri);
        File videoFile = new File(realPath);
        byte[] bytesArray = new byte[(int) videoFile.length()];
        FileInputStream fis = new FileInputStream(videoFile);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();
        RequestBody videoBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileNames[pos]+"_PRACTICE_", RequestBody.create(MediaType.parse("video/*mp4"), bytesArray))
                .build();


        Request request = new Request.Builder().url("http://192.168.0.99:5000/debug")
                .post(videoBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NotNull Call call,
                    @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println("failure");
                        Intent intent= new Intent(RecordActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body().string().equals("received")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("success");
                            Intent intent= new Intent(RecordActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    private void practiceVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY ,1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startCamera.launch(intent);
        }
    }


    //getRealPathFromUrI from source below
    //https://stackoverflow.com/questions/17546101/get-real-path-for-uri-android
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }




}