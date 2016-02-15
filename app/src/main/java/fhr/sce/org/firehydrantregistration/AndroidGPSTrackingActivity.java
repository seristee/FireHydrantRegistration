package fhr.sce.org.firehydrantregistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by seristee on 27/11/2015.
 */
public class AndroidGPSTrackingActivity extends AppCompatActivity {
    Button btnShowLocation;
    Button btnSubmitHydrant;
    Button btnSavePhoto;


    EditText hydrantSerial;
    EditText hydrantType;
    EditText hydrantCondition;
    EditText hydrantComments;
    EditText hydrantLatitude;
    EditText hydrantLongitude;

    // GPSTracker class
    GPSTracker gps;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private ImageView imageView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnSubmitHydrant = (Button) findViewById(R.id.btnSubmitHydrant);
        btnSavePhoto = (Button) findViewById(R.id.photo_button);


        //values
        hydrantSerial = (EditText) findViewById(R.id.serialTxt);
        hydrantType =  (EditText) findViewById(R.id.typeTxt);
        hydrantCondition = (EditText) findViewById(R.id.conditionTxt);
        hydrantComments = (EditText) findViewById(R.id.commentsTxt);
        hydrantLatitude = (EditText) findViewById(R.id.txt_latitude);
        hydrantLongitude = (EditText) findViewById(R.id.txt_longitude);


        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(AndroidGPSTrackingActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    EditText txt_latitude = (EditText) findViewById(R.id.txt_latitude);
                    EditText txt_longitude = (EditText) findViewById(R.id.txt_longitude);

                    txt_latitude.setText(latitude+"");
                    txt_longitude.setText(longitude+"");

                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });

        btnSubmitHydrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //values
                String serial = hydrantSerial.getText().toString();
                String type = hydrantType.getText().toString();
                String condition = hydrantCondition.getText().toString();
                String comments = hydrantComments.getText().toString();
                String latitude = hydrantLatitude.getText().toString();
                String longitude = hydrantLongitude.getText().toString();

                new SaveNewHydrant().execute(serial,type,condition,comments,latitude,longitude);

                //Toast.makeText(getApplicationContext(),getString(R.string.save_success), Toast.LENGTH_LONG).show();
            }
        });

        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }


    class SaveNewHydrant extends AsyncTask<String,String,String> {

        private ProgressDialog pDialog;
        private final static String saveHydrantUrl = "INSERT ADDESS FOR PHP SCRIPT HERE";
        private static final String TAG_SUCCESS = "success";
        String answer  = null;

        HashMap<String, String> params;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AndroidGPSTrackingActivity.this);
            pDialog.setTitle("Loading...");
            pDialog.setMessage("Saving New Hydrant...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... argo) {

            String getSerial = argo[0];
            String getType = argo[1];
            String getCondition = argo[2];
            String getComments = argo[3];
            String getLatitude = argo[4];
            String getLongitude = argo[5];

            params = new HashMap<>();
            params.put("hydrant_serial",getSerial);
            params.put("hydrant_type",getType);
            params.put("hydrant_condition",getCondition);
            params.put("hydrant_comments",getComments);
            params.put("hydrant_latitude",getLatitude);
            params.put("hydrant_longitude",getLongitude);

            System.err.println("OBJECTS =>> " + getSerial + " => " + getType + " => " + getCondition + " => " + getComments + " => " + getLatitude + " => " + getLongitude);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject= jsonParser.makeHttpRequest(saveHydrantUrl,"POST",params);

            Log.d("Create Response", jsonObject.toString());
            try {
                int success = jsonObject.getInt(TAG_SUCCESS);

                if(success == 1){
                    //Toast.makeText(getApplicationContext(),getString(R.string.save_success), Toast.LENGTH_LONG).show();
                    answer =  "true";
                } else {
                    //Toast.makeText(getApplicationContext(),getString(R.string.save_error), Toast.LENGTH_LONG).show();
                    answer =  "false";
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageView = (ImageView) findViewById(R.id.imageView);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            System.err.println(data.getExtras().get("data"));
        }
        imageView.setVisibility(View.VISIBLE);
    }
}
