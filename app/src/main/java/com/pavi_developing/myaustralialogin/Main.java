package com.pavi_developing.myaustralialogin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    TwitterLoginButton twitterLoginButton;
    Button twitterLogoutButton;

    AccessTokenTracker accessTokenTracker;
    CallbackManager callbackManager;
    Intent tweetIntent;
    TwitterSession session;

    boolean twitterLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(Main.this, Home.class));

        initializeInstance(this);
        initializeView();
        initializeVariables();

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
            public void onError(FacebookException error) { }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                updateWithToken(result.data.getAuthToken());
            }

            @Override
            public void failure(TwitterException exception) { }
        });

        twitterLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                twitterLoggedIn = false;
                switchTwitterButtonVisibility();
                login_status_view.setText("Please Login.");
            }
        });

    }

    private void initializeVariables() {
        twitterLoggedIn = false;
        callbackManager=CallbackManager.Factory.create();
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        /* Tweet /
        tweetIntent = new ComposerActivity.Builder(this)
                .session(session)
                .text("My First Tweet")
                .hashtags("#myAussieLogin @PhullVivek")
                .createIntent();
        /**/
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };
        if(session!=null)
            updateWithToken(session.getAuthToken());
    }

    private void initializeInstance(Context context) {
        FacebookSdk.sdkInitialize(context);
        Twitter.initialize(context);
    }

    private void initializeView() {
        setContentView(R.layout.activity_main);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLogoutButton = (Button) findViewById(R.id.twitter_logout_button);
        fb_login_button=(LoginButton)findViewById(R.id.fb_login_button);
        login_status_view =(TextView)findViewById(R.id.fblogin_status_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
    private void updateWithToken(AccessToken currentAccessToken) {
        String msg = "Please Login.";
        if (currentAccessToken != null) {
            msg = "Logged in.";
            Intent i = new Intent(Main.this, Home.class);
            startActivity(i);
        }
        login_status_view.setText(msg);
    }
    private void updateWithToken(AuthToken currentAccessToken) {
        String msg = "Please Login.";
        twitterLoggedIn = false;
        if (currentAccessToken != null) {
            msg = "Logged in.";
            twitterLoggedIn = true;
            Intent i = new Intent(Main.this, Home.class);
            startActivity(i);
        }
        login_status_view.setText(msg);
        switchTwitterButtonVisibility();
    }

    private void switchTwitterButtonVisibility() {
        twitterLoginButton.setVisibility((twitterLoggedIn)? View.GONE:View.VISIBLE);
        twitterLogoutButton.setVisibility((twitterLoggedIn)?View.VISIBLE:View.GONE);
    }
}
