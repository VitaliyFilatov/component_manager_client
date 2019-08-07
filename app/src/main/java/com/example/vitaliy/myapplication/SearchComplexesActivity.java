package com.example.vitaliy.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.vitaliy.myapplication.Entity.Address;
import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.Filter;
import com.example.vitaliy.myapplication.Model.AddressModel;
import com.example.vitaliy.myapplication.Model.ComplexTypeModel;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.Model.FilterModel;
import com.example.vitaliy.myapplication.Model.ObjectModel;
import com.example.vitaliy.myapplication.Model.RoomModel;
import com.example.vitaliy.myapplication.ViewManager.KeyboardHider;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class SearchComplexesActivity extends AppCompatActivity implements View.OnClickListener {

    private ComplexTypeModel complexTypeModel;
    private ObjectModel objectModel;
    private AddressModel addressModel;
    private RoomModel roomModel;
    private FilterModel filterModel;
    private Filter filter;

    private Spinner searchSpinnerComponentTypesList,
            searchSpinnerObjectsList,
            searchSpinnerAddressesList,
            searchSpinnerRoomsList,
            searchWithEmptyCodes;

    private EditText searchInputAppendixET,
            searchInputIPET,
            searchInputAddressIdentityET;
    private ArrayList<CommonInfo> componentTypes,
            objects,
            rooms;
    private ArrayList<Address> addresses;
    private EditText inputComponentCode;
    private Button searchByComponentCodeBtn,searchComplexes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_complexes);
        complexTypeModel = new ComplexTypeModel(this);
        objectModel = new ObjectModel(this);
        addressModel = new AddressModel(this);
        roomModel = new RoomModel(this);
        filterModel = new FilterModel(this);
        filter = filterModel.getFilter();
        componentTypes = complexTypeModel.getAllComplexTypes();
        ArrayList<String> componentTypeNames = new ArrayList<String>();
        componentTypeNames.add("Не задано");
        int complexTypePos = 0;
        for(int i=0;i<componentTypes.size();i++)
        {
            componentTypeNames.add(componentTypes.get(i).name);
            if(filter.complexTypeId == componentTypes.get(i).id)
            {
                complexTypePos = i+1;
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, componentTypeNames);


        searchSpinnerAddressesList = (Spinner)findViewById(R.id.searchSpinnerAddressesList);
        searchSpinnerRoomsList = (Spinner)findViewById(R.id.searchSpinnerRoomsList);


        searchSpinnerComponentTypesList = (Spinner)findViewById(R.id.searchSpinnerComponentTypesList);
        searchSpinnerComponentTypesList.setAdapter(dataAdapter);
        searchSpinnerComponentTypesList.setSelection(complexTypePos);

        ArrayList<String> withEmptyCodesState = new ArrayList<String>();
        withEmptyCodesState.add("Не задано");
        withEmptyCodesState.add("С пустыми компонентами");
        withEmptyCodesState.add("Без пустых компонент");
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, withEmptyCodesState);

        searchWithEmptyCodes = (Spinner)findViewById(R.id.searchWithEmptyCodes);
        searchWithEmptyCodes.setAdapter(dataAdapter);
        if(filter.withUnset == -1)
        {
            searchWithEmptyCodes.setSelection(0);
        }
        else if(filter.withUnset == 1)
        {
            searchWithEmptyCodes.setSelection(1);
        }
        else
        {
            searchWithEmptyCodes.setSelection(2);
        }

        objects = objectModel.getAllObjects();
        ArrayList<String> objectNames = new ArrayList<String>();
        objectNames.add("Не задано");
        int objectPos = 0;
        for(int i=0;i<objects.size();i++)
        {
            objectNames.add(objects.get(i).name);
            if(filter.objectId == objects.get(i).id)
            {
                objectPos = i+1;
            }
        }
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, objectNames);
        searchSpinnerObjectsList = (Spinner)findViewById(R.id.searchSpinnerObjectsList);
        searchSpinnerObjectsList.setAdapter(dataAdapter);
        searchSpinnerObjectsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                SearchComplexesActivity.this.updateAddressList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        searchComplexes = (Button)findViewById(R.id.searchComplexes);
        searchComplexes.setOnClickListener(this);

        Button scanComponentCode = (Button)findViewById(R.id.scanComponentCode);
        scanComponentCode.setOnClickListener(this);

        searchByComponentCodeBtn = (Button)findViewById(R.id.searchByComponentCodeBtn);
        searchByComponentCodeBtn.setOnClickListener(this);


        inputComponentCode = (EditText)findViewById(R.id.inputComponentCode);
        inputComponentCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String input = inputComponentCode.getText().toString();
                    System.out.println("input: "+input);
                    input = input.replaceAll("\n","");
                    System.out.println("input post: "+input);
                    inputComponentCode.setText(input);
                    KeyboardHider.hideKeyboardFrom(SearchComplexesActivity.this, inputComponentCode);
                }
                return false;
            }
        });

        searchInputAppendixET = findViewById(R.id.searchInputAppendixET);
        searchInputAppendixET.setText(filter.additionalInfo);
        searchInputIPET = findViewById(R.id.searchInputIPET);
        searchInputIPET.setText(filter.ipAddress);
        searchInputAddressIdentityET = findViewById(R.id.searchInputAddressIdentityET);
        searchInputAddressIdentityET.setText(filter.idByAddress);
        searchSpinnerObjectsList.setSelection(objectPos);
    }

    private void setPortrait()
    {
        LinearLayout rootShowComplexes = (LinearLayout)findViewById(R.id.rootShowComplexes);
        rootShowComplexes.setWeightSum(100f);
    }

    private void setLandscape()
    {
        LinearLayout rootShowComplexes = (LinearLayout)findViewById(R.id.rootShowComplexes);
        rootShowComplexes.setWeightSum(50f);
    }

    private void updateAddressList()
    {
        if(searchSpinnerObjectsList.getSelectedItemPosition() == 0)
        {
            if(addresses != null)
                addresses.clear();
            else
                return;
        }
        else
        {
            addresses = addressModel.getAddressesByObjectId(objects.get(searchSpinnerObjectsList.getSelectedItemPosition() - 1).id);
        }
        ArrayList<String> strAddr = new ArrayList<String>();
        strAddr.add("Не задано");
        int addressPos = 0;
        for(int i=0;i < addresses.size();i++)
        {
            strAddr.add(addresses.get(i).getStringFullAddress());
            if(addresses.get(i).id == filter.addressId)
            {
                addressPos = i+1;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, strAddr);
        searchSpinnerAddressesList.setAdapter(adapter);
        searchSpinnerAddressesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                SearchComplexesActivity.this.updateRooms();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        searchSpinnerAddressesList.setSelection(addressPos);
    }

    private void updateRooms()
    {
        if(searchSpinnerAddressesList.getSelectedItemPosition() == 0)
        {
            if(rooms != null)
                rooms.clear();
            else
                return;
        }
        else
        {
            int address_id = addresses.get(searchSpinnerAddressesList.getSelectedItemPosition() - 1).id;
            rooms = roomModel.getCabinetsByAddressId(address_id);
        }
        ArrayList<String> roomNames = new ArrayList<String>();
        roomNames.add("Не задано");
        int roomPos = 0;
        for(int i=0;i < rooms.size();i++)
        {
            roomNames.add(rooms.get(i).name);
            if(filter.roomId == rooms.get(i).id)
            {
                roomPos = i+1;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, roomNames);
        searchSpinnerRoomsList.setAdapter(adapter);
        searchSpinnerRoomsList.setSelection(roomPos);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.searchComplexes){
//            int complexTypeId = -1,
//                    objectId = -1,
//                    addressId = -1,
//                    roomId = -1,
//                    withUnset = -1;
            int complexTypeId = 0,
                    objectId = 0,
                    addressId = 0,
                    roomId = 0,
                    withUnset = -1;
            Intent intent = new Intent(getApplicationContext(),ShowComplexesActivity.class);
            if(searchSpinnerComponentTypesList.getSelectedItemPosition() > 0)
            {
                complexTypeId = componentTypes.get(searchSpinnerComponentTypesList.getSelectedItemPosition() - 1).id;
            }
            if(searchSpinnerObjectsList.getSelectedItemPosition() > 0)
            {
                objectId = objects.get(searchSpinnerObjectsList.getSelectedItemPosition() - 1).id;
            }
            if(searchSpinnerAddressesList.getSelectedItemPosition() > 0)
            {
                addressId = addresses.get(searchSpinnerAddressesList.getSelectedItemPosition() - 1).id;
            }
            if(searchSpinnerRoomsList.getSelectedItemPosition() > 0)
            {
                roomId = rooms.get(searchSpinnerRoomsList.getSelectedItemPosition() - 1).id;
            }
            if(searchWithEmptyCodes.getSelectedItemPosition() > 0)
            {
                if(searchWithEmptyCodes.getSelectedItemPosition() == 1)
                {
                    withUnset = 1;
                }
                else {
                    withUnset = 0;
                }
            }
//            intent.putExtra("complexTypeId", complexTypeId);
//            intent.putExtra("objectId", objectId);
//            intent.putExtra("addressId", addressId);
//            intent.putExtra("roomId", roomId);
//            intent.putExtra("additionalInfo", searchInputAppendixET.getText().toString());
//            intent.putExtra("ipAddress", searchInputIPET.getText().toString());
//            intent.putExtra("idByAddress", searchInputAddressIdentityET.getText().toString());
//            intent.putExtra("withUnset", withUnset);
            filter.complexTypeId = complexTypeId;
            filter.objectId = objectId;
            filter.addressId = addressId;
            filter.roomId = roomId;
            filter.additionalInfo = searchInputAppendixET.getText().toString();
            filter.ipAddress = searchInputIPET.getText().toString();
            filter.idByAddress = searchInputAddressIdentityET.getText().toString();
            filter.withUnset = withUnset;
            filterModel.updateFilter(filter);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if(view.getId()==R.id.scanComponentCode){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
        if(view.getId()==R.id.searchByComponentCodeBtn){
            Intent intent = new Intent(getApplicationContext(),ShowComplexesActivity.class);
            intent.putExtra("componentCode", inputComponentCode.getText().toString());
            startActivity(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            inputComponentCode.setText(scanContent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            setLandscape();
        }else{

            setPortrait();
        }
    }
}
