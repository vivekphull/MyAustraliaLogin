package com.pavi_developing.myaustralialogin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PrepareReport extends AppCompatActivity {

    private int
            REQUEST_CAMERA = 0,
            SELECT_FILE = 1,
            PLACE_PICKER_REQ=2;
    private String
            image_json,
            address_json,
            description,
            tagclass,
            userChoosenTask,
            TAG = "PreRep/";
    private boolean externalStoragePermission;

    ImageView ivImage;
    TextView
    textView,
    getTagstext;
    Button button;

    EditText editText;
    JSONObject jsonreport;
    Switch aSwitch;
    private VisualRecognition visualService;
    Bitmap bitmap;
    Toolbar toolbar;
    public static AmazonClientManager clientManager = null;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePermissions();
        initializeViews();
    }


    private void initializePermissions() {
        externalStoragePermission = false;

        // EXTERNAL STORAGE ACCESS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            else if( ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
            else
                externalStoragePermission = true;
        }
        Log.d(TAG+"initPerm", "Permissions Initialized");
    }

    private void initializeClickListeners() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetTags getTags=new GetTags(PrepareReport.this,getTagstext,button,visualService);
                getTags.execute(bitmap);
                PlacePicker.IntentBuilder builder= new PlacePicker.IntentBuilder();
                try {
                    Log.i("PreReq/GetLoc", "Trying for location");
                    startActivityForResult(builder.build(PrepareReport.this), PLACE_PICKER_REQ);
                } catch (GooglePlayServicesRepairableException gpsre) {
                    gpsre.printStackTrace();
                    Log.i("PreReq/GetLoc", gpsre.toString());
                } catch (GooglePlayServicesNotAvailableException gpsnae) {
                    Log.i("PreReq/GetLoc", gpsnae.toString());
                    gpsnae.printStackTrace();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description=editText.getText().toString();
                tagclass=getTagstext.getText().toString();

                insertDataToStatus(address_json, String.valueOf(aSwitch.isChecked()), description, image_json);
                try {
                    jsonreport.put("TAG",tagclass);
                    jsonreport.put("ADDRESS",address_json);
                    jsonreport.put("ACTIVE",aSwitch.isChecked());
                    jsonreport.put("DESCRIPTION",description);
                    jsonreport.put("IMAGE",image_json);
                    Log.e("JSON test",jsonreport.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(PrepareReport.this,Final.class);
                startActivity(intent);
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void initializeVariables() {
        jsonreport=new JSONObject();
        clientManager = new AmazonClientManager(this);
        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_SYDNEY);
        visualService = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        visualService.setApiKey(getString(R.string.visualrecognitionApi_key));

        initializeClickListeners();
    }

    private void initializeViews() {
        if(!allPermissionsAreGranted())
            return;

        setContentView(R.layout.activity_prepare_report);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PrepareReport");
        textView=(TextView)findViewById(R.id.getplace);

        getTagstext=(TextView)findViewById(R.id.tagstext);
        getTagstext.setVisibility(View.GONE);
        editText=(EditText)findViewById(R.id.description);
        aSwitch=(Switch)findViewById(R.id.privacy);
        button=(Button)findViewById(R.id.report_button);
        aSwitch.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        ivImage = (ImageView) findViewById(R.id.ivImage);

        initializeVariables();
    }

    private void insertDataToStatus(final String address, final String identity, final String description, final String image) {
        RequestQueue engine = Volley.newRequestQueue(this);
        String url = "http://13.229.108.76:1000/api/status";
        final Response.Listener<String> onSuccess = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Data Inserted !", Toast.LENGTH_LONG).show();
            }
        };
        final Response.ErrorListener onFailure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Toast.makeText(getApplicationContext(), "Data Not Inserted...", Toast.LENGTH_LONG).show();
                Log.e("API", String.valueOf(response));
            }
        };
        StringRequest post = new StringRequest(Request.Method.POST, url, onSuccess, onFailure){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("address", address);
                params.put("identity", identity);
                params.put("description", description);
//                params.put("image", image);
                return params;
            }
        };
        engine.add(post);
    }

    private boolean allPermissionsAreGranted() {
        boolean granted = true;
        granted = (externalStoragePermission)?granted:externalStoragePermission;
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;

            default:
                switch (permissions[requestCode]) {
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        externalStoragePermission = (grantResults[requestCode]==0);
                        if(!externalStoragePermission)
                            finish();
                        break;
                }
        }
        Log.i("PreRep/onReqPerResult", "requestCode: "+requestCode+"\nPermission: "+permissions[requestCode]+"\ngrant: "+grantResults[requestCode]);

        initializeViews();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(getApplicationContext());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        startActivityForResult(Intent.createChooser(new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT), "Select File"),SELECT_FILE);
    }

    private void cameraIntent() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Log.d("galery :"," image-1");
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA) {
                Log.d("camera :"," image-2");
                onCaptureImageResult(data);
            } else if (requestCode ==PLACE_PICKER_REQ) {
                Log.d("Location :"," loc-1");
                Place place=PlacePicker.getPlace(data,this);
                String address=String.format("%s",place.getAddress());
                address_json=address;
                aSwitch.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                textView.setText(address);
            }
        }
        Log.i("PreReq/GetLoc", "\n\nreqCode: "+requestCode+"\nresCode: "+resultCode+"\ndata: "+data);

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byte[] byteArray = bytes .toByteArray();
        image_json = Base64.encodeToString(byteArray, Base64.DEFAULT);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
        textView.setVisibility(View.VISIBLE);
        BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
        bitmap = drawable.getBitmap();
        bitmap=resizeBitmapForWatson(bitmap,1200);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                image_json = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
        textView.setVisibility(View.VISIBLE);
        BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
        bitmap = drawable.getBitmap();
        bitmap=resizeBitmapForWatson(bitmap,1200);
    }
    private class ValidateCredentialsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            visualService.getClassifiers().execute();
            return null;
        }
    }
    private Bitmap resizeBitmapForWatson(Bitmap originalImage, float maxSize) {

        Log.e("TAGS",""+"enter resize");
        int originalHeight = originalImage.getHeight();
        int originalWidth = originalImage.getWidth();
        int boundingDimension = (originalHeight > originalWidth) ? originalHeight : originalWidth;
        float scale = maxSize / boundingDimension;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        originalImage = Bitmap.createBitmap(originalImage, 0, 0, originalWidth, originalHeight, matrix, true);
        return originalImage;
    }
    private class ClassifyTagsResult{
        private final String finaltag;

        private ClassifyTagsResult(String s) {
         finaltag=s;
        }
    }

    private class DynamoDBManagerTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {
            String coun;
            String des;
            String loc;
            String uid;

        DynamoDBManagerTask(String coun,String des,String loc, String uid){
            this.coun=coun;
            this.des=des;
            this.loc=loc;
            this.uid=uid;
        }
        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DatabaseManager.getTestTableStatus();

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);


            if (types[0] == DynamoDBManagerType.INSERT_REPORT) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DatabaseManager.insertreport(coun,des,loc,uid);
                }
            }

            return result;
        }

        protected void onPostExecute(DynamoDBManagerTaskResult result) {

            if (result.getTableStatus().equalsIgnoreCase("ACTIVE")
                    && result.getTaskType() == DynamoDBManagerType.INSERT_REPORT) {
                Toast.makeText(PrepareReport.this,
                        "Report submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private enum DynamoDBManagerType {
        GET_TABLE_STATUS, CREATE_TABLE, INSERT_REPORT
    }

    private class DynamoDBManagerTaskResult {
        private DynamoDBManagerType taskType;
        private String tableStatus;

        public DynamoDBManagerType getTaskType() {
            return taskType;
        }

        public void setTaskType(DynamoDBManagerType taskType) {
            this.taskType = taskType;
        }

        public String getTableStatus() {
            return tableStatus;
        }

        public void setTableStatus(String tableStatus) {
            this.tableStatus = tableStatus;
        }
    }
}

