package com.pavi_developing.myaustralialogin;

/**
 * Created by Hp-core.i5 on 30-08-2017.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hp-core.i5 on 29-08-2017.
 */

public class GetTags extends AsyncTask<Bitmap,Integer,VisualClassification> {
    VisualRecognition visualService;
    Context context;
    TextView textView;
    Button button;
    String tags;
    GetTags(Context context,TextView textView,Button button,VisualRecognition visualService){
        this.context=context;
        this.textView=textView;
        this.button=button;
        this.visualService=visualService;
    }
    @Override
    protected VisualClassification doInBackground(Bitmap... voids) {
        Bitmap bitmap=voids[0];

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        try {
            File tempPhoto = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            FileOutputStream out = new FileOutputStream(tempPhoto);
            out.write(bytes.toByteArray());
            out.close();

            ClassifyImagesOptions classifyImagesOptions = new ClassifyImagesOptions.Builder().images(tempPhoto).build();
            VisualClassification classification = visualService.classify(classifyImagesOptions).execute();
            Log.e("TAGS",""+classification);
            tempPhoto.delete();

            return classification;
        } catch (Exception ex) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(VisualClassification classification) {
        Log.d("GetTag/onPostExe", "Classification: "+classification);
        if (classification == null)
            return;
        List<ImageClassification> classifications = classification.getImages();
     //   for (int i = 0; i < classifications.size(); i++) {
            List<VisualClassifier> classifiers = classifications.get(0).getClassifiers();
      //      if (classifiers == null) break;
        //    for (int j = 0; j < classifiers.size(); j++) {
                List<VisualClassifier.VisualClass> visualClasses = classifiers.get(0).getClasses();
              //  if (visualClasses == null) break;
                for (VisualClassifier.VisualClass visualClass : visualClasses) {
                    String formattedScore = String.format(Locale.US, "%.0f", visualClass.getScore() * 100);

                    tags=visualClass.getName()+"-"+formattedScore;
                    textView.setText(tags);
                    Log.e("TAGS Class name : ",""+tags);
                    Log.e("TAGS Score : ",""+formattedScore);
                    break;
                }
           // }
      //  }
    }
}
