package com.example.vitaliy.myapplication;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.ComplexInfoServer;
import com.example.vitaliy.myapplication.Entity.InfoForServer;
import com.example.vitaliy.myapplication.Entity.Settings;
import com.example.vitaliy.myapplication.Model.AddressModel;
import com.example.vitaliy.myapplication.Model.ComplexHasComponentModel;
import com.example.vitaliy.myapplication.Model.ComplexModel;
import com.example.vitaliy.myapplication.Model.ComplexTypeHasComponentModel;
import com.example.vitaliy.myapplication.Model.ComplexTypeModel;
import com.example.vitaliy.myapplication.Model.ComponentModel;
import com.example.vitaliy.myapplication.Model.ObjectModel;
import com.example.vitaliy.myapplication.Model.RoomModel;
import com.example.vitaliy.myapplication.Service.MultiRequest;
import com.example.vitaliy.myapplication.Service.SingletonRequestQueue;
import com.example.vitaliy.myapplication.Service.SingletonSettings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerConnectionActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendToServer;
    private ObjectModel objectModel;
    private AddressModel addressModel;
    private RoomModel roomModel;
    private ComponentModel componentModel;
    private ComplexTypeModel complexTypeModel;
    private ComplexModel complexModel;
    private ComplexTypeHasComponentModel complexTypeHasComponentModel;
    private ComplexHasComponentModel complexHasComponentModel;
    private int countWithPhoto;
    private int sendedCount;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = SingletonSettings.getInstance(this).getSettings();
        setContentView(R.layout.activity_server_connection);

        sendToServer = (Button) findViewById(R.id.sendToServer);
        sendToServer.setOnClickListener(this);

        objectModel = new ObjectModel(this);
        addressModel = new AddressModel(this);
        roomModel = new RoomModel(this);
        componentModel = new ComponentModel(this);
        complexTypeModel = new ComplexTypeModel(this);
        complexModel = new ComplexModel(this);
        complexTypeHasComponentModel = new ComplexTypeHasComponentModel(this);
        complexHasComponentModel = new ComplexHasComponentModel(this);
    }

    private String getJsonInfo() {
        InfoForServer info = new InfoForServer();
        info.deviceId = android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        info.objects = objectModel.getAllObjectsForServer();
        info.addresses = addressModel.getAllAddressesForServer();
        info.rooms = roomModel.getAllRoomsForServer();
        info.components = componentModel.getAllComponentsForServer();
        info.complexTypes = complexTypeModel.getAllComplexTypesForServer();
        info.complexes = complexModel.getAllComplexesForServer();
        info.complexTypeComponents = complexTypeHasComponentModel.getAllForServer();
        info.complexComponents = complexHasComponentModel.getAllForServer();
        for(ComplexInfoServer item : info.complexes)
        {
            System.out.println("ComplexInfoServer id: "+item.id);
        }

        Gson gson = new Gson();
        return gson.toJson(info);
    }

    private String getMessage()
    {
        sendedCount++;
        return ""+sendedCount+"/"+countWithPhoto;
    }

    private void synchronize()
    {
        dataUsedCount++;
        if(dataUsedCount == 2)
        {
            System.out.println("synchronized");
            complexModel.synchronize();
            objectModel.synchronize();
            addressModel.synchronize();
            roomModel.synchronize();
            complexTypeModel.synchronize();
            componentModel.synchronize();

        }
    }

    private int dataUsedCount;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendToServer) {
            dataUsedCount = 0;
            Toast.makeText(getApplicationContext(),
                    "Загружается информация о комплексах",
                    Toast.LENGTH_SHORT).show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+settings.serverIp+"/uploadInfo?XDEBUG_SESSION_START=ECLIPSE_DBGP",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(),
                                    "Информация загружена, загрузка фотографий",
                                    Toast.LENGTH_SHORT).show();
                            synchronize();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("get some error: "+error.getMessage());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    System.out.println("before info getJsonInfo");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("info", getJsonInfo());
                    System.out.println("after info getJsonInfo");
                    return params;
                }
            };
            System.out.println("before addToRequestQueue");
            SingletonRequestQueue.getInstance(this).addToRequestQueue(stringRequest);
            sendedCount = 0;
            countWithPhoto = complexModel.getCountWithPhoto();
            System.out.println("post info send");
            if(countWithPhoto == 0)
            {
                synchronize();
                Toast.makeText(getApplicationContext(),
                        "Нет фото для загрузки",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<ComplexInfoServer> complexes = complexModel.getAllComplexesForServer();
            for(int i=0;i<complexes.size();i++)
            {
                String photoPath = complexes.get(i).photoPath;
                if(photoPath == null)
                    continue;
                if(photoPath.equals(""))
                    continue;
                File file = new File(photoPath);
                MultiRequest request = new MultiRequest(android.provider.Settings.Secure.getString(getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID),
                        complexes.get(i).id,
                        "http://"+settings.serverIp+"/upload?XDEBUG_SESSION_START",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(),
                                        ServerConnectionActivity.this.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                if(sendedCount == countWithPhoto)
                                {
                                    synchronize();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
                            }
                        },
                        file);

                SingletonRequestQueue.getInstance(this).addToRequestQueue(request);
            }
        }
    }
}
