package com.pavi_developing.myaustralialogin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.R.attr.tag;
import static android.R.id.message;

public class GetLocation extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private Button button1;
    private Button button2;
    int PLACE_PICKER_REQ=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        textView=(TextView)findViewById(R.id.getplace);
        button=(Button)findViewById(R.id.Selectlocation);
        button1=(Button)findViewById(R.id.back_image);
        button2=(Button)findViewById(R.id.next_final);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder= new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(GetLocation.this), PLACE_PICKER_REQ);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(GetLocation.this,GetImage.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(GetLocation.this,PrepareReport.class);
                startActivity(intent);
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==PLACE_PICKER_REQ){
            if(resultCode==RESULT_OK){
                Place place=PlacePicker.getPlace(data,this);
                String address=String.format("%s",place.getAddress());
                textView.setText(address);
            }
        }
    }
}
