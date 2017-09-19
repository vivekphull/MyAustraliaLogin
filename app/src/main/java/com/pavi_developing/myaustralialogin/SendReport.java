package com.pavi_developing.myaustralialogin;

/**
 * Created by Hp-core.i5 on 31-08-2017.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class SendReport extends AsyncTask<String,Void,String> {

    String check="";
    Context context;
    AlertDialog alertDialog;

    SendReport(Context ctx){
        context=ctx;
    }
    @Override
    protected String  doInBackground(String... params) {
        String type= params[0];
        String repor_url="http://";
            repor_url+= "54.169.235.153";
            repor_url+=":1000/api/council";
        //String repor_url="http://10.201.8.56/D:/MWS_VIVEK/MyAustralia1/myA1/app.js";
        if (type.equals("send")){
            try {
                String department= params[1];
                String address= params[2];
                String identity= params[3];
                String description= params[4];
                String image= params[5];
                Log.e("DATA","\n\nAddress: "+address+"\nIdentity: "+identity+"\nDescription: "+description);
//                Log.e("DESCRIPTION",description);
//                Log.e("Image",image);
                URL url=new URL(repor_url);
                HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data= URLEncoder.encode("department","UTF-8")+"="+URLEncoder.encode(department,"UTF-8")+"&"
                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(address,"UTF-8")+"&"
                        +URLEncoder.encode("logo","UTF-8")+"="+URLEncoder.encode(identity,"UTF-8")+"&"
                        +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(description,"UTF-8")+"&"
                        +URLEncoder.encode("contactNumber","UTF-8")+"="+URLEncoder.encode(image,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
//                Log.e("Image","testing");
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                String result="";
                String line;
                while ((line=bufferedReader.readLine())!=null){
                    result+=line;
                }
                Log.e("DOG TESTING 1",result);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onPostExecute(String result) {
        Log.e("Data onPost", result);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}


