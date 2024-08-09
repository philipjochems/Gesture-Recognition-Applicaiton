package com.example.cse535part1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;


public class PracticeActivity extends AppCompatActivity {

    VideoView myVideoView;
    MediaController mediaControls;
    String param;
    int pos;
    int[] myRawFiles= {R.raw.hlighton, R.raw.hlightoff,R.raw.hfanon,R.raw.hfanoff,R.raw.hincreasefanspeed,R.raw.hdecreasefan,R.raw.hsetthermo,
            R.raw.h0,R.raw.h1,R.raw.h2,R.raw.h3,R.raw.h4,R.raw.h5,R.raw.h6,R.raw.h7,R.raw.h8,R.raw.h9,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        Bundle myBundle = getIntent().getExtras();
        param = null;
        pos=-1;
        if(myBundle!= null) {
            param = myBundle.getString("value");
            pos = myBundle.getInt("pos");
        }

        int rawFile=myRawFiles[pos];


        myVideoView = (VideoView) findViewById(R.id.practiceVideo);
        if (mediaControls == null) {
            // create an object of media controller class
            mediaControls = new MediaController(this);
            mediaControls.setAnchorView(myVideoView);
        }
        myVideoView.setMediaController(mediaControls);
        myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + rawFile));
        myVideoView.start();
        myVideoView.setMediaController(null);
        myVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myVideoView.start();
            }
        });
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer media) {
                media.reset();
                myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + rawFile));

            }
        });

       Button button = (Button) findViewById(R.id.practiceButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(PracticeActivity.this, RecordActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("value", param);
                myBundle.putInt("pos",pos);
                intent.putExtras(myBundle);
                startActivity(intent);

            }
        });

        Button button2 = (Button) findViewById(R.id.replayPractice);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myVideoView.start();
            }
        });





    }
}