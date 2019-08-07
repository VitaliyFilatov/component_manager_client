package com.example.vitaliy.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.vitaliy.myapplication.Entity.Address;
import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.TableModel;
import com.example.vitaliy.myapplication.Model.AddressModel;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.Model.ObjectModel;
import com.example.vitaliy.myapplication.Model.RoomModel;
import com.example.vitaliy.myapplication.ViewManager.KeyboardHider;
import com.example.vitaliy.myapplication.ViewManager.TableOwner;

import java.util.ArrayList;

public class PlacementActivity extends BaseActivity implements View.OnClickListener {

    private ObjectModel objectModel;
    private RoomModel roomModel;
    private AddressModel addressModel;

    private Spinner spinnerObjectList, spinnerAddressList;
    private EditText inputCabName;
    private ImageButton addObject, addAddress;
    private ArrayList<Address> addresses;
    private ArrayList<CommonInfo> objects;
    private ArrayList<String> objectNames;
    private TableOwner tableOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);
        attachKeyboardListeners((ViewGroup)findViewById(R.id.rootPlacement));
        objectModel = new ObjectModel(this);
        roomModel = new RoomModel(this);
        addressModel = new AddressModel(this);

        tableOwner = new TableOwner((TableLayout) findViewById(R.id.allCabinetsTable));
        objects = objectModel.getAllObjects();
        objectNames = new ArrayList<String>();
        for(int i=0;i<objects.size();i++)
        {
            objectNames.add(objects.get(i).name);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, objectNames);
        inputCabName = (EditText)findViewById(R.id.inputCabName);
        Button addCabBtn = (Button)findViewById(R.id.addCabBtn);
        addCabBtn.setOnClickListener(this);
        addObject = (ImageButton)findViewById(R.id.addObject);
        addObject.setOnClickListener(this);
        addAddress = (ImageButton)findViewById(R.id.addAddress);
        addAddress.setOnClickListener(this);
        Button deleteCabinet = (Button)findViewById(R.id.deleteCabinet);
        deleteCabinet.setOnClickListener(this);
        Button backInPlcmManager = (Button)findViewById(R.id.backInPlcmManager);
        backInPlcmManager.setOnClickListener(this);
        tableOwner.bindButton(deleteCabinet);
        spinnerObjectList = (Spinner)findViewById(R.id.spinnerObjectList);
        spinnerObjectList.setAdapter(dataAdapter);
        spinnerObjectList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                PlacementActivity.this.updateAddressList(objects.get(position).id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void updateAddressList(int objectId)
    {
        spinnerAddressList = (Spinner)findViewById(R.id.spinnerAddressList);
        addresses = addressModel.getAddressesByObjectId(objectId);
        if(addresses.size() == 0)
        {
            clearCabs();
        }
        ArrayList<String> strAddr = new ArrayList<String>();
        for(int i=0;i < addresses.size();i++)
        {
            Address current = addresses.get(i);
            strAddr.add(current.city + ", " + current.street + ", " + current.building);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, strAddr);
        spinnerAddressList.setAdapter(adapter);
        spinnerAddressList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                PlacementActivity.this.updateCabs();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void updateCabs()
    {
        tableOwner.setTableModel(roomModel.getCabinetsByAddressId(addresses.get(spinnerAddressList.getSelectedItemPosition()).id));
        tableOwner.updateTable();
    }

    private void clearCabs()
    {
        tableOwner.setTableModel(new ArrayList<CommonInfo>());
        tableOwner.updateTable();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.addCabBtn){
            int address_id = addresses.get(spinnerAddressList.getSelectedItemPosition()).id;
            roomModel.addNewCabToAddress(address_id, inputCabName.getText().toString());
            updateCabs();
            KeyboardHider.hideKeyboardFrom(this, inputCabName);
            inputCabName.setText("");
            return;
        }
        if(view.getId() == R.id.deleteCabinet)
        {
            if(tableOwner.getLastRow() > -1)
            {
                Bundle cabinet = tableOwner.getCurrentModel();
                if(cabinet != null)
                {
                    if(cabinet.containsKey("id"))
                    {
                        int id = Integer.parseInt(cabinet.getString("id"));
                        roomModel.deleteCabinet(id);
                        updateCabs();
                    }
                }
            }
            return;
        }
        if(view.getId() == R.id.addObject)
        {
            Intent intent = new Intent(getApplicationContext(),ObjectManagerActivity.class);
            startActivity(intent);
        }
        if(view.getId() == R.id.addAddress)
        {
            CommonInfo object = objects.get(spinnerObjectList.getSelectedItemPosition());
            Bundle bundle = new Bundle();
            bundle.putInt("id", object.id);
            bundle.putString("name", object.name);
            Intent intent = new Intent(getApplicationContext(),AddressManagerActivity.class);
            intent.putExtra("object", bundle);
            startActivity(intent);
        }
        if(view.getId() == R.id.backInPlcmManager)
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        ScrollView allCabsScroll = (ScrollView)findViewById(R.id.allCabsScroll);
        allCabsScroll.setVisibility(View.GONE);
        LinearLayout rootPlacement = (LinearLayout)findViewById(R.id.rootPlacement);
        rootPlacement.setWeightSum(55f);
    }

    @Override
    protected void onHideKeyboard() {
        ScrollView allCabsScroll = (ScrollView)findViewById(R.id.allCabsScroll);
        allCabsScroll.setVisibility(View.VISIBLE);
        LinearLayout rootPlacement = (LinearLayout)findViewById(R.id.rootPlacement);
        rootPlacement.setWeightSum(110f);
    }
}
