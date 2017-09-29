package com.pavi_developing.myaustralialogin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by VivekPhull on 9/29/2017.
 */

public class MappedReports extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews(getApplicationContext());
    }

    private void initializeViews(Context context) {
        setContentView(R.layout.activity_mapped_reports);


    }
}