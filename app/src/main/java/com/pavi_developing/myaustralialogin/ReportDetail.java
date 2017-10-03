package com.pavi_developing.myaustralialogin;

import android.*;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.Buffer;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Vivek Phull on 10/1/2017.
 */

public class ReportDetail extends Activity {

    private boolean
            internetPermission;
    private String
            userId,
            TAG = "RepDet/";
    private JSONObject
            reportData;
    private int
            score,
            count;

    ImageView
        reportImage,
        reporterImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePermissions();
        initializeViews();
        initialCalls();

    }

    private void initializePermissions() {
        // INTERNET
        if( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        } else
            internetPermission = true;
    }

    private void initializeViews() {
        if(!allPermissionsAreGranted())
            return;

        setContentView(R.layout.activity_report_detail);
        reportImage = (ImageView) findViewById(R.id.imageReportDetail);
//        reportImage = new ImageView(getApplication());
        initializeVariables();
    }

    private void initializeVariables() {
        reportData = new JSONObject();
        score =
        count = 0;
    }

    private void initialCalls() {
        final String url = "http://13.229.108.76:1000/api/status/";

        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, url+getIntent().getExtras().getString("id"), null,
            new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG + "onGetData/onSucc", "Res: " + response);
                reportData = response;
                populateViews();
                Volley.newRequestQueue(getApplicationContext()).add(new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                while(response.length()>0) {
                                    JSONObject obj = (JSONObject) response.remove(0);

                                    try {
                                        if( obj.getJSONObject("user").getString("id").equals(userId) ) {
                                            score += obj.getInt("score");
                                            count++;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    populateScore();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG+"onGetData/onFail", "Error: "+error);
                    }
                }));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG+"onGetData/onFail", "Error: "+error);
            }
        }));
    }

    private void populateScore() {
        ((TextView) findViewById(R.id.scoreTextReportDetail) ).setText("Karma Points:   "+score);
        ((TextView) findViewById(R.id.countTextReportDetail) ).setText("No. of Reports: "+count);

        Call<User> userCall = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, false, false);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                new DownloadImageTask((ImageView) findViewById(R.id.reporterImageReportDetail))
                        .execute(result.data.profileImageUrl);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            bmImage.setMinimumHeight(dm.heightPixels);
            bmImage.setMinimumWidth(dm.widthPixels);
            bmImage.setImageBitmap(result);
        }
    }

    private void populateViews() {
        try {
            userId = reportData.getJSONObject("user").getString("id");
            ((TextView) findViewById(R.id.addressTextReportDetail) ).setText(reportData.getString("address").toUpperCase());
            ((TextView) findViewById(R.id.locationTextReportDetail) ).setText(reportData.getString("geometry").toUpperCase());
            ((TextView) findViewById(R.id.descriptionTextReportDetail) ).setText(reportData.getString("description").toUpperCase());
            ((TextView) findViewById(R.id.tagTextReportDetail) ).setText(reportData.getString("tag").toUpperCase());
            ((TextView) findViewById(R.id.reporterTextReportDetail) ).setText(reportData.getJSONObject("user").getString("name").toUpperCase());

            byte[] byteArray = reportData.getJSONObject("image").getString("data").getBytes();
            Log.i(TAG+"popViews", "Bitmap-image: "+reportData.getJSONObject("image"));
            Log.i(TAG+"popViews", "Bitmap-data: "+reportData.getJSONObject("image").getString("data"));
//            Log.i(TAG+"popViews", "Bitmap-byte: "+byteArray[0]);
//            byteArray = Base64.decode( reportData.getJSONObject("image").getString("data").getBytes(), 0);
//            Log.i(TAG+"popViews", "Bitmap-base: "+ byteArray[0]);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Log.i(TAG+"popViews", "Bitmap: "+bmp);
            if(bmp==null)
                Log.e(TAG+"popViews", "Bitmap: "+BitmapFactory.decodeByteArray(Base64.decode(byteArray, 0), 0, Base64.decode(byteArray, 0).length) );

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            reportImage.setMinimumHeight(dm.heightPixels);
            reportImage.setMinimumWidth(dm.widthPixels);
            reportImage.setImageBitmap(bmp);
//            ((RelativeLayout) findViewById(R.id.imageViewReportDetail) ).addView(reportImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean allPermissionsAreGranted() {
        boolean granted = true;
        granted = (internetPermission)?granted:internetPermission;

        Log.d(TAG+"AllPerGra", "Permissions:\nInternet: "+internetPermission+"\nfinal bool: "+granted);
        return granted;
    }
}
