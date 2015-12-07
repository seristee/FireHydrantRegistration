package fhr.sce.org.firehydrantregistration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by seristee on 27/11/2015.
 */
public class AndroidGPSTrackingActivity extends AppCompatActivity {
    Button btnShowLocation;
    Button btnSubmitHydrant;
    private ProgressDialog pDialog;

    private EditText hydrantSerial,hydrantType,hydrantCondition,hydrantComments,hydrantLattitude,hydrantLongitude;

    // GPSTracker class
    GPSTracker gps;

    final static String saveHydrantUrl = "http://208.94.176.125:81/fhydrants/insert_hydrant.php";

    JSONParser jsonParser = new JSONParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnSubmitHydrant = (Button) findViewById(R.id.btnSubmitHydrant);

        //values
        hydrantSerial = (EditText) findViewById(R.id.serialTxt);
        hydrantType =  (EditText) findViewById(R.id.typeTxt);
        hydrantCondition = (EditText) findViewById(R.id.conditionTxt);
        hydrantComments = (EditText) findViewById(R.id.commentsTxt);
        hydrantLattitude = (EditText) findViewById(R.id.txt_latitude);
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
                String serial = hydrantSerial.getText().toString();
                String type = hydrantType.getText().toString();
                String condition = hydrantCondition.getText().toString();
                String comments = hydrantComments.getText().toString();
                String lattitude = hydrantLattitude.getText().toString();
                String longitude = hydrantLongitude.getText().toString();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("hydrant_serial",serial);
                params.put("hydrant_type",type);
                params.put("hydrant_condition",condition);
                params.put("hydrant_comments",comments);
                params.put("hydrant_lattitude",lattitude);
                params.put("hydrant_longitude",longitude);
                System.err.println("PARAMS =>> " + params.toString());

                try {
                    JSONObject jsonObject = jsonParser.makeHttpRequest(saveHydrantUrl,"POST",params);
                } catch (Exception err){
                    err.printStackTrace();
                }
            }
        });
    }


}
