package com.pavi_developing.myaustralialogin;

import android.os.AsyncTask;

/**
 * Created by VivekPhull on 9/10/2017.
 */

public class TestAPI extends AsyncTask<String, String, String> {

    String
            url = "http://54.169.235.153:1000/api/council",
            name = "Test3",
            logo = "Test Logo",
            email = "Test Email";

    @Override
    protected String doInBackground(String[] params) {
        return "";
    }
}
