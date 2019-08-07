package com.example.vitaliy.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitaliy.myapplication.Model.DBHelper;
//import com.example.vitaliy.myapplication.Service.MultiRequest;
import com.example.vitaliy.myapplication.Service.MultiRequest;
import com.example.vitaliy.myapplication.Service.SingletonDB;
import com.example.vitaliy.myapplication.Service.SingletonRequestQueue;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button placementBtn, complexTypesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placementBtn = (Button)findViewById(R.id.placementBtn);

        placementBtn.setOnClickListener(this);
        complexTypesBtn = (Button)findViewById(R.id.complexTypesBtn);

        complexTypesBtn.setOnClickListener(this);

        Button allComplexes = (Button)findViewById(R.id.showAllComplexesBtn);
        allComplexes.setOnClickListener(this);

        Button addNewComplexFromMainBtn = (Button)findViewById(R.id.addNewComplexFromMainBtn);
        addNewComplexFromMainBtn.setOnClickListener(this);

        Button settingsBtn = (Button)findViewById(R.id.settings);
        settingsBtn.setOnClickListener(this);

        Button exitBtn = (Button)findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);

        Button sendInfoToServer = (Button)findViewById(R.id.sendInfoToServer);
        sendInfoToServer.setOnClickListener(this);

    }

    public void onClick(View v){
        if(v.getId()==R.id.placementBtn){
            Intent intent = new Intent(getApplicationContext(),PlacementActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.complexTypesBtn){
            Intent intent = new Intent(getApplicationContext(),ComplexTypesActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.showAllComplexesBtn){
            Intent intent = new Intent(getApplicationContext(),ShowComplexesActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.addNewComplexFromMainBtn){
            Intent intent = new Intent(getApplicationContext(),BindComplexActivity.class);
            intent.putExtra("parent", "MainActivity");
            startActivity(intent);
        }
        if(v.getId()==R.id.settings){
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.sendInfoToServer){
            Intent intent = new Intent(getApplicationContext(),ServerConnectionActivity.class);
            startActivity(intent);
        }
//        if(v.getId()==R.id.settings){
////            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.106/trialpost",
////                    new Response.Listener<String>() {
////                        @Override
////                        public void onResponse(String response) {
////                            try {
////                                JSONObject reader = new JSONObject(response);
////                                Toast.makeText(getApplicationContext(),
////                                        reader.getString("key1"),
////                                        Toast.LENGTH_LONG).show();
////                            } catch (JSONException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    },
////                    new Response.ErrorListener() {
////                        @Override
////                        public void onErrorResponse(VolleyError error) {
////                            // Handle error
////                        }
////                    })
////            {
////                @Override
////                protected Map<String, String> getParams()
////                {
////                    Map<String, String>  params = new HashMap<String, String>();
////                    params.put("key1", "value1");
////                    params.put("key2", "value2");
////
////                    return params;
////                }
////            };
//
//
////
//            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            File file = new File(storageDir.getAbsolutePath()+File.separator+"ARM_11416504437.jpg");
//            MultiRequest request = new MultiRequest("",
//                    "http://192.168.0.108/upload?XDEBUG_SESSION_START",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
////                            try {
//////                                JSONObject reader = new JSONObject(response);
////                                Toast.makeText(getApplicationContext(),
////                                        "ALL DONE",
////                                        Toast.LENGTH_LONG).show();
////                            } catch (JSONException e) {
////                                e.printStackTrace();
////                            }
//                            Toast.makeText(getApplicationContext(),
//                                    "ALL DONE",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // Handle error
//                        }
//                    },
//                    file);
//
//            SingletonRequestQueue.getInstance(this).addToRequestQueue(request);
//        }
        if(v.getId()==R.id.exitBtn){
            finishAffinity();
            finish();
        }
    }
}
