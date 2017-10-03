package com.pavi_developing.myaustralialogin;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

public class Final extends AppCompatActivity {
    Button button2;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report Submitted");

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
//               startActivity(new Intent(Final.this,Home.class));
               finish();
           }
       });
        findViewById(R.id.tweetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //  Tweet /
                startActivity( new ComposerActivity.Builder(getApplicationContext())
                    .session(TwitterCore.getInstance().getSessionManager().getActiveSession())
                    .text("Incident Report: "+getIntent().getExtras().getString("description"))
                    .image((Uri) getIntent().getExtras().get("imageUri"))
                    .hashtags("#"+getIntent().getExtras().getString("tag")+" #myAussieLogin @PhullVivek")
                    .createIntent()
                );
            }
        });
    }
}
