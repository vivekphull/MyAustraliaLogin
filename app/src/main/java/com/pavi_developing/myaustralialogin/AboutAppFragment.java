package com.pavi_developing.myaustralialogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.text.Html;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAppFragment extends Fragment {


    public AboutAppFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String text;
        text = "<html><body><br><br><br><h4>myAustralia - App</h4><p align=\"justify\">";
        text+="<br>This app allows you to quicky and easily report issues and provide " +
                "feedback to your local authorities. <br><br>You can report on common issues such as Hard waste, Parking, Street" +
                "Cleaning, Litter, Pothole etc. myAustralia works by matching your location with registered authorities" +
                "in the area and puts your request directly to the relevant authority. <br><br>You can optionally include your " +
                "details with a report. If you do the authority will be able to communicate directly with you to fix the " +
                "issue.<br><br><br><br><br><br><br><br>";
        text+="</p></body></html>";
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        WebView textView=(WebView) view.findViewById(R.id.aboutapp_text);
        textView.loadData(text, "text/html", "utf-8");
        return view;
    }

}
