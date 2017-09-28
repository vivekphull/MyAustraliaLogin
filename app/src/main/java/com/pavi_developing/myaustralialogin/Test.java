package com.pavi_developing.myaustralialogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

/**
 * Created by VivekPhull on 9/10/2017.
 */

public class Test extends Activity {

    TwitterLoginButton signInButton;
    Button tweetButton;
    TextView text;
    Intent tweetIntent;
    private String TAG = "Test";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_test);

        signInButton = (TwitterLoginButton) findViewById(R.id.testButton1);
        tweetButton = (Button) findViewById(R.id.testButton2);
        text = (TextView) findViewById(R.id.testText1);

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        tweetIntent = new ComposerActivity.Builder(this)
                .session(session)
                .text("My First Tweet")
                .hashtags("#myAussieLogin @PhullVivek")
                .createIntent();

        switchButtonVisibility(false);
        if(session!=null) {
            text.setText("Logged in as " + session.getUserName());
            switchButtonVisibility(true);
        }

        /* Twitter Authentication Callback */
        signInButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                twitterLogin(result);
            }

            @Override
            public void failure(TwitterException exception) {
                text.setText("Callback Failure...");
                switchButtonVisibility(false);
            }
        });
        /**/

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(tweetIntent);
            }
        });
    }

    private void switchButtonVisibility(boolean isLoggedIn) {
            signInButton.setVisibility((isLoggedIn)?View.GONE:View.VISIBLE);
            tweetButton.setVisibility((isLoggedIn)?View.VISIBLE:View.GONE);
    }

    private void twitterLogin(Result<TwitterSession> result) {
        text.setText("Callback Success !!");
        text.setText("Logged in as "+result.data.getUserName());
        switchButtonVisibility(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login signInButton.
        signInButton.onActivityResult(requestCode, resultCode, data);
    }
}