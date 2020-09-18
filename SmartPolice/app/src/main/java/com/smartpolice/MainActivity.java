package com.smartpolice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smartpolice.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button scanBtn,btnContravention;
    String plateNumber;

    TextView plateNoTextView, ownerTextView,telTextView,categoryTextView,
            totalDelaysTextView,penaltyAmoTextView,totalAmoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);

        //CAR'S INFORMATION
        plateNoTextView = findViewById(R.id.plateNoTextView);
        ownerTextView = findViewById(R.id.ownerTextView);
        telTextView = findViewById(R.id.telTextView);
        categoryTextView = findViewById(R.id.categoryTextView);

        //CAR'S STATUS
        totalDelaysTextView = findViewById(R.id.totalDelaysTextView);
        penaltyAmoTextView = findViewById(R.id.penaltyAmoTextView);
        totalAmoTextView = findViewById(R.id.totalAmoTextView);
        init();
    }

    @Override
    public void onClick(View v) {
    scanCode();
    }

    private void init(){
        plateNoTextView = (TextView) findViewById(R.id.plateNoTextView);

        btnContravention = (Button)findViewById(R.id.btnContravention);

        btnContravention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                plateNumber =  plateNoTextView.getText().toString();

                submitPlateNum();
                moveToContravention();
            }
        });
    }



    //QR CODE SCANNER START===================================================================================

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result !=null) {
            if (result.getContents() != null) {


                retrieveQrInfo(result.getContents());

//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(result.getContents());
//                builder.setTitle("Scanning Result");
//                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int which) {
//                        scanCode();
//                    }
//                }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int which) {
//                        finish();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();

            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_SHORT).show();
            }
        }else {
             super.onActivityResult(requestCode, resultCode, data);
            }
        }


    //END OF QR CODE SCANNER===============================================



    private void retrieveQrInfo(final String qr_result){

        String urlRetrievePlayerInfo = "http://172.20.10.3/SmartConnect/CarInfo.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlRetrievePlayerInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("FFAFAFA",response);
                        // JSON DATA
//                        Toast.makeText(PlayerList.this,response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject responseJsonObject = new JSONObject(response);

                            //CAR'S INFORMATION
                            String owner = responseJsonObject.getString("owner");
                            String tel = responseJsonObject.getString("tel");
                            String category = responseJsonObject.getString("category");
                            String plateno = responseJsonObject.getString("plateno");


                            ownerTextView.setText(owner);
                            telTextView.setText(tel);
                            categoryTextView.setText(category);
                            plateNoTextView.setText(plateno);


                            //CAR STATUS
                            String delays = responseJsonObject.getString("delays");
                            String penalty = responseJsonObject.getString("penalty");
                            String total = responseJsonObject.getString("total");

                            totalDelaysTextView.setText(delays);
                            penaltyAmoTextView.setText(penalty);
                            totalAmoTextView.setText(total);


                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this,"Error, Please try again!",Toast.LENGTH_SHORT).show();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"ERROR found while submitting your form ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("REQUEST", "retrieveQrInfo");
                params.put("plateno", qr_result);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }


    private void submitPlateNum(){

        String urlSubmitPlateNum = "http://172.20.10.3/SmartConnect/I_loved_her.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlSubmitPlateNum,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(MainActivity.this,response, Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"ERROR found while submitting your form ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("REQUEST", "submitPlateNum");
                params.put("plateNumber", plateNumber);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

         }
    public void moveToContravention() {
        startActivity(new Intent(getApplicationContext(),ContraventionActivity.class));
        finish();
    }

    }


