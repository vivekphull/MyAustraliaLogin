package com.pavi_developing.myaustralialogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;


public class Main extends AppCompatActivity {

    LoginButton fb_login_button;
    TextView login_status_view;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    TwitterLoginButton twitterLoginButton;
    Intent tweetIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);
        fb_login_button=(LoginButton)findViewById(R.id.fb_login_button);
        login_status_view =(TextView)findViewById(R.id.fblogin_status_view);
        callbackManager=CallbackManager.Factory.create();

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        tweetIntent = new ComposerActivity.Builder(this)
                .session(session)
                .text("My First Tweet")
                .hashtags("#myAussieLogin @PhullVivek")
                .createIntent();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        updateWithToken(AccessToken.getCurrentAccessToken());

        fb_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                login_status_view.setText("Login success");
            }

            @Override
            public void onCancel() {
                login_status_view.setText("Login failed");
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                updateWithToken(result.data.getAuthToken());
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            Intent i = new Intent(Main.this, Home.class);
            startActivity(i);
        } else {
            login_status_view.setText("Please Login");
        }
    }
    private void updateWithToken(AuthToken currentAccessToken) {

        if (currentAccessToken != null) {
            Intent i = new Intent(Main.this, Home.class);
            startActivity(i);
        } else {
            login_status_view.setText("Please Login");
        }
    }
}
