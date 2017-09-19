package com.pavi_developing.myaustralialogin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by VivekPhull on 9/10/2017.
 */

public class PostActivity extends Activity {

    private RequestQueue engine; // Engine
    private String url = "http://54.169.235.153:1000/api/council";
    private TextView data; // anything to view data on GET
    private EditText newData; // anything to send data on POST
    private StringRequest getCouncilData; // Request Sample
    private ArrayList<String> list;

    public ArrayList getCouncilData(){
        list = new ArrayList<>();
        engine.add(getCouncilData);
        return list;
    }

    public void addCouncilData(JsonObject param){
        postRequest(param.get("name").toString(), param.get("logo").toString(), param.get("email").toString());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        data = (TextView) findViewById(R.id.post_text);
        newData = (EditText) findViewById(R.id.post_text_name);

        engine = Volley.newRequestQueue(getApplicationContext());
        getCouncilData = new StringRequest(Request.Method.GET, url, onSuccess, onFailure); // initialize GET API

        findViewById(R.id.post_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRequest("Test 3", "Logo", "Email");
            }
        });
//        engine.add(getCouncilData);
    }

    private final void postRequest(final String data1, final String data2, final String data3) {
        StringRequest insertCouncilData = new StringRequest(Request.Method.POST, url, onPostSuccess, onPostFailure){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                String data = newData.getText().toString();
                params.put("name", data1);
                params.put("logo", data2);
                params.put("email", data3);
                return params;
            }
        };

        engine.add(insertCouncilData);
    }

    private final Response.Listener<String> onSuccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            data.setText(response);
            Log.e("Data Response", response);
            list.add(response);
        }
    };

    private final Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            data.setText("App Crashed");
            Log.e("Data Response", "onCrash: "+error);
        }
    };

    private final Response.Listener<String> onPostSuccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            engine.add(getCouncilData);
        }
    };

    private final Response.ErrorListener onPostFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            data.setText("App Crashed");
            Log.e("Data Response", "onCrash: "+error);
        }
    };

}
