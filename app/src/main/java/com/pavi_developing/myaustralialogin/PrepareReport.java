package com.pavi_developing.myaustralialogin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
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
import android.view.WindowManager;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
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
            axis,
            TAG = "PreRep/";
    private boolean externalStoragePermission;
    Uri imageUri;

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
    private ProgressDialog dialog;

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
                insertDataToStatus(address_json, String.valueOf(aSwitch.isChecked()), editText.getText().toString());
//                tagclass=getTagstext.getText().toString();
                try {
                    dialog.show();
                } catch (WindowManager.BadTokenException bte) {
                    Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        initialCalls();
    }

    private void initialCalls() {
        new ValidateCredentialsTask().execute();
    }

    private void initializeVariables() {
        jsonreport=new JSONObject();
        clientManager = new AmazonClientManager(this);
        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_SYDNEY);
        visualService = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        visualService.setApiKey(getString(R.string.visualrecognitionApi_key));


        dialog = new ProgressDialog(this.getApplicationContext());
        dialog.setTitle("Adding your report to our Database...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

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

    private void insertDataToStatus(final String address, final String identity, final String description) {
        JSONObject
                obj = new JSONObject(),
                user = new JSONObject();

        boolean isTwitter = getIntent().getExtras().getBoolean("isTwitter");
        String userName = getIntent().getExtras().getString("userName");
        try {
            user.put("id", userName.split("/")[1]);
            user.put("source", (isTwitter)?"Twitter": "Facebook");
            user.put("name", (isTwitter)?userName.split("/")[0]:userName);

            obj.put("address", address);
            obj.put("identity", identity);
            obj.put("description", description);
            obj.put("active", true);
            obj.put("user", user);
            obj.put("tag", getTagstext.getText().toString().split("-")[0]);
            obj.put("score", getTagstext.getText().toString().split("-")[1]);
            obj.put("geometry", new JSONArray().put(axis.split(",")[0]).put(axis.split(",")[1]));
            obj.put("image", image_json);
            Log.i(TAG+"JSON", "Object: "+obj);
            Volley.newRequestQueue(this).add(new JsonObjectRequest(
                    Request.Method.POST,
                    "http://13.229.108.76:1000/api/status",
                    obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG + "API-S", "Res: " + response);
                            startActivity( new Intent(getApplicationContext(), Final.class)
                                    .putExtra("tag", getTagstext.getText().toString().split("-")[0])
                                    .putExtra("score", getTagstext.getText().toString().split("-")[1])
                                    .putExtra("description", description)
                                    .putExtra("address", address)
                                    .putExtra("imageUri", imageUri)
                            );
                            dialog.dismiss();
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG+"API-F", "Error: "+error);
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Data Not Sent...", Toast.LENGTH_LONG).show();
                        }
                    })
            );
        } catch (JSONException e) {
            Log.e(TAG+"API/JSONExc", "Error: "+e);
            e.printStackTrace();
        }
        Log.d(TAG+"JSON", "Object: "+obj);
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
                onCaptureImageResult(data);
            } else if (requestCode ==PLACE_PICKER_REQ) {
                Place place=PlacePicker.getPlace(data,this);
                Log.e(TAG+"onActRes/LocReq","Data: "+place);
                Log.i(TAG+"onActRes/LocReq","Data: "+place.getLatLng().toString());
                String address=String.format("%s",place.getAddress());
                axis = String.valueOf(place.getLatLng().latitude)+","+String.valueOf(place.getLatLng().longitude);
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
        Log.e(TAG+"onSelFromGalRes", "Data: "+data+"\ntag: "+getTagstext.getText());
        Log.i(TAG+"onSelFromGalRes", "Data: "+data.getData());
        Log.d(TAG+"onSelFromGalRes", "Data: "+data.getDataString());
        imageUri = data.getData();
        Bitmap bm=null;

        try {
            bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            image_json = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d(TAG+"imageJson", "{ image: "+image_json+"}");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ne) {
            Log.e(TAG+"onImageSelected", "Data is NULL");
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

