package com.example.vitaliy.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitaliy.myapplication.Entity.Address;
import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.ComplexComponent;
import com.example.vitaliy.myapplication.Entity.ComplexInfo;
import com.example.vitaliy.myapplication.Entity.LastSelection;
import com.example.vitaliy.myapplication.Model.AddressModel;
import com.example.vitaliy.myapplication.Model.ComplexHasComponentModel;
import com.example.vitaliy.myapplication.Model.ComplexModel;
import com.example.vitaliy.myapplication.Model.ComplexTypeModel;
import com.example.vitaliy.myapplication.Model.ComponentModel;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.Model.LastSelectionModel;
import com.example.vitaliy.myapplication.Model.ObjectModel;
import com.example.vitaliy.myapplication.Model.RoomModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BindComplexActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    //models
    private ComplexModel complexModel;
    private ComplexTypeModel complexTypeModel;
    private LastSelectionModel lastSelectionModel;
    private ObjectModel objectModel;
    private AddressModel addressModel;
    private RoomModel roomModel;
    private ComplexHasComponentModel complexHasComponentModel;
    private ComponentModel componentModel;
    private String mCurrentPhotoPath;
    private Uri photoURI;
    static final int REQUEST_TAKE_PHOTO = 1;



    private int lastObjectPos;

    private String parent;
    private int complex_id;
    private ArrayList<CommonInfo> complexTypes, objects, rooms;
    private Spinner spinnerComplexTypesList,
            spinnerObjectForComplexList,
            spinnerAddressForComplexList,
            spinnerRoomsForComplexList,
            spinnerComponentForComplexList;

    private ArrayList<Address> addresses;
    private boolean isNew;
    private Button deleteComponentFromComplexBtn,
            addComponentToComplexBtn,
            takePhotoBtn;

    private boolean first;
    //таблица компонент комплекса
    private TableLayout componentsOfComplexTable;
    //компоненты комплекса
    private ArrayList<ComplexComponent> componentsOfComplex;
    //коды компонент
    private ArrayList<EditText> codes;
    //кнопки сканирования
    private ArrayList<Button> scanBtns;

    private int codeIndex;

    private ComplexInfo currentComplex;

    private static int scanBtnIndex=-1;

    private int lastRow;
    private ArrayList<CommonInfo> components;

    private EditText inputAppendixET, inputIPET, inputAddressIdentityET;
    private ImageView photo, frame;
    private RelativeLayout photoFrame;
    private LinearLayout photoPlace;


    private LastSelection lastSelection;

    private boolean orientationChangedForAddress;
    private boolean orientationChangedForRoom;
    private boolean orientationChangedForObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orientationChangedForAddress = false;
        orientationChangedForRoom = false;
        orientationChangedForObject = false;
        setContentView(R.layout.activity_bind_complex);
        parent = getIntent().getStringExtra("parent");
        complex_id = getIntent().getIntExtra("complex_id", -1);
        lastRow = -1;
        photo = (ImageView)findViewById(R.id.photo);
        frame = (ImageView)findViewById(R.id.frame);
        photoFrame = (RelativeLayout)findViewById(R.id.photoFrame);
        photoPlace = (LinearLayout)findViewById(R.id.photoPlace);



        initializeModels();

        currentComplex = null;
        if(complex_id > 0)
        {
            currentComplex = complexModel.getComplex(complex_id);
        }
        initiateEditTexts();
        initializeButtons();
        initializeComplexTypesSpinner();

        spinnerAddressForComplexList = (Spinner)findViewById(R.id.spinnerAddressForComplexList) ;
        spinnerRoomsForComplexList = (Spinner)findViewById(R.id.spinnerRoomsForComplexList) ;

        initializeObjectsSpinner();
        //инициализация таблицы компонент
        componentsOfComplexTable = (TableLayout)findViewById(R.id.componentsOfComplexTable);

        setSelectionOnComplexType();
        initializeComponentsSpinner();

        final View content = findViewById(android.R.id.content);

        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                System.out.println("Orientation changed");
                orientationChangedForAddress = true;
                orientationChangedForRoom = true;
                orientationChangedForObject = true;
                content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                setPhoto();
            }

        });
    }


    private void initiateEditTexts()
    {
        inputAppendixET = (EditText)findViewById(R.id.inputAppendixET);
        inputIPET = (EditText)findViewById(R.id.inputIPET);
        inputAddressIdentityET = (EditText)findViewById(R.id.inputAddressIdentityET);
        if(complex_id > 0 && currentComplex != null)
        {
            inputAppendixET.setText(currentComplex.appendix);
            inputIPET.setText(currentComplex.ipAddress);
            inputAddressIdentityET.setText(currentComplex.id_by_address);
        }
    }

    private void setPhoto()
    {
        if(complex_id < 0)
            return;
        //int targetW = photoFrame.getWidth();
        int targetW = photoPlace.getWidth();
        int targetH = photoFrame.getHeight();
        System.out.println("targetH: "+targetH+" targetW: "+targetW);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if(currentComplex.photoPath == null)
            return;
        BitmapFactory.decodeFile(currentComplex.photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        if(photoH == 0 || photoW==0)
            return;
        System.out.println("photoH: "+photoH+" photoW: "+photoW);

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)photoFrame.getLayoutParams();
        layoutParams.width = photoW/scaleFactor;
        layoutParams.height = photoH/scaleFactor;
        photoFrame.setLayoutParams(layoutParams);

        Bitmap bitmap = BitmapFactory.decodeFile(currentComplex.photoPath, bmOptions);
        photo.setImageBitmap(bitmap);

        RelativeLayout.LayoutParams layoutParamsRel = (RelativeLayout.LayoutParams)frame.getLayoutParams();
        if(photoH>photoW)
        {
            layoutParamsRel.setMargins(0,8,0,8);
        }
        else
        {
            layoutParamsRel.setMargins(8,0,8,0);
        }
        frame.setLayoutParams(layoutParamsRel);

        layoutParamsRel = (RelativeLayout.LayoutParams)photo.getLayoutParams();
        if(photoH>photoW)
        {
            layoutParamsRel.setMargins(7,0,7,0);
        }
        else
        {
            layoutParamsRel.setMargins(0,7,0,7);
        }
        photo.setLayoutParams(layoutParamsRel);
    }


    private void initializeModels()
    {
        complexModel = new ComplexModel(this);
        complexTypeModel = new ComplexTypeModel(this);
        lastSelectionModel = new LastSelectionModel(this);
        objectModel = new ObjectModel(this);
        addressModel = new AddressModel(this);
        roomModel = new RoomModel(this);
        complexHasComponentModel = new ComplexHasComponentModel(this);
        componentModel = new ComponentModel(this);
    }

    private void initializeButtons()
    {
        Button saveComplexTypeBtn = (Button)findViewById(R.id.saveComplexTypeBtn);
        saveComplexTypeBtn.setOnClickListener(this);

        Button saveComplexBtn = (Button)findViewById(R.id.saveComplexBtn);
        saveComplexBtn.setOnClickListener(this);

        Button saveAndNewBtn = (Button)findViewById(R.id.saveAndNewBtn);
        saveAndNewBtn.setOnClickListener(this);


        Button backBindComplexBtn = (Button)findViewById(R.id.backBindComplexBtn);
        backBindComplexBtn.setOnClickListener(this);

        deleteComponentFromComplexBtn = (Button)findViewById(R.id.deleteComponentFromComplexBtn);
        deleteComponentFromComplexBtn.setOnClickListener(this);

        addComponentToComplexBtn = (Button)findViewById(R.id.addComponentToComplexBtn);
        addComponentToComplexBtn.setOnClickListener(this);

        takePhotoBtn = (Button)findViewById(R.id.takePhotoBtn);
        takePhotoBtn.setOnClickListener(this);


        setEnabledBindedButtons(false);
    }

    private void setEnabledBindedButtons(boolean enabled)
    {
        if(enabled)
        {
            deleteComponentFromComplexBtn.setBackgroundResource(R.drawable.add_btn);
            addComponentToComplexBtn.setBackgroundResource(R.drawable.add_btn);
            takePhotoBtn.setBackgroundResource(R.drawable.add_btn);
        }
        else
        {
            deleteComponentFromComplexBtn.setBackgroundResource(R.drawable.disable_btn);
            addComponentToComplexBtn.setBackgroundResource(R.drawable.disable_btn);
            takePhotoBtn.setBackgroundResource(R.drawable.disable_btn);
        }
        deleteComponentFromComplexBtn.setEnabled(enabled);
        addComponentToComplexBtn.setEnabled(enabled);
        takePhotoBtn.setEnabled(enabled);
    }

    private void initializeComplexTypesSpinner()
    {
        complexTypes = complexTypeModel.getAllComplexTypes();
        ArrayList<String> complexTypeNames = new ArrayList<String>();
        complexTypeNames.add("Не задано");
        lastSelection = lastSelectionModel.getLastSelection();
        int lastComplexTypePos = -1;
        for(int i=0;i<complexTypes.size();i++)
        {
            complexTypeNames.add(complexTypes.get(i).name);
            if(complexTypes.get(i).id == lastSelection.complexTypeId)
                lastComplexTypePos = i;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, complexTypeNames);
        spinnerComplexTypesList = (Spinner)findViewById(R.id.spinnerComplexTypesList) ;
        spinnerComplexTypesList.setAdapter(dataAdapter);
        spinnerComplexTypesList.setSelection(lastComplexTypePos + 1);
    }

    private void initializeObjectsSpinner()
    {
        objects = objectModel.getAllObjects();
        ArrayList<String> objectNames = new ArrayList<String>();
        objectNames.add("Не задано");
        lastObjectPos = -1;
        for(int i=0;i<objects.size();i++)
        {
            objectNames.add(objects.get(i).name);
            if(objects.get(i).id == lastSelection.objectId)
                lastObjectPos = i;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, objectNames);
        spinnerObjectForComplexList = (Spinner)findViewById(R.id.spinnerObjectForComplexList) ;
        spinnerObjectForComplexList.setAdapter(dataAdapter);


        spinnerObjectForComplexList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(!orientationChangedForObject)
                {
                    System.out.println("object orientation changed");
                    BindComplexActivity.this.updateAddressList();
                }
                else
                {
                    orientationChangedForObject = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void initializeComponentsSpinner()
    {
        components = componentModel.getAllComponents();
        ArrayList<String> componentNames = new ArrayList<String>();
        for(int i=0;i<components.size();i++)
        {
            componentNames.add(components.get(i).name);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, componentNames);
        spinnerComponentForComplexList = (Spinner)findViewById(R.id.spinnerComponentForComplexList);
        spinnerComponentForComplexList.setAdapter(dataAdapter);
    }


    private void setSelectionOnComplexType()
    {
        first = false;

        if(complex_id > -1)
        {
            updateComponentsTable();
            int complexTypeId = complexModel.getComplexTypeIdOfComplex(complex_id);
            if(complexTypeId > -1)
            {
                int complex_type_pos = -1;
                for(int i=0;i<complexTypes.size();i++)
                {
                    if(complexTypes.get(i).id == complexTypeId)
                    {
                        complex_type_pos = i;
                        break;
                    }
                }
                if(complex_type_pos > -1)
                {
                    spinnerComplexTypesList.setSelection(complex_type_pos + 1);
                }
                else
                {
                    spinnerComplexTypesList.setSelection(0);
                }
            }
            setEnabledBindedButtons(true);
            int objectId = complexModel.getObjectIdOfComplex(complex_id);
            if(objectId > -1)
            {
                int object_pos = -1;
                for(int i=0;i<objects.size();i++)
                {
                    if(objects.get(i).id == objectId)
                    {
                        object_pos = i;
                        break;
                    }
                }
                if(object_pos > -1)
                {
                    first = true;
                    spinnerObjectForComplexList.setSelection(object_pos + 1);
                }
                else
                {
                    spinnerObjectForComplexList.setSelection(0);
                }
            }
            isNew = false;
        }
        else
        {
            first = true;
            spinnerObjectForComplexList.setSelection(lastObjectPos + 1);
            isNew = true;
        }
    }

    private void updateComponentsTable() {
        if (complex_id < 0)
            return;
        componentsOfComplex = complexModel.getComponentOfComplex(complex_id);
        componentsOfComplexTable.removeViews(0, componentsOfComplexTable.getChildCount());
        codes = new ArrayList<EditText>();
        scanBtns = new ArrayList<Button>();
        int divcount = 0;
        for(int i=0;i<componentsOfComplex.size();i++)
        {
            TableRow tableRow = new TableRow(this);
            TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            rowLayoutParams.setMargins(5,5,5,5);
            tableRow.setLayoutParams(rowLayoutParams);
            tableRow.setWeightSum(5f);
            //tableRow.setBackgroundResource(R.drawable.row_border);


            TextView textView = new TextView(this);
            textView.setText(componentsOfComplex.get(i).name);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAppearance(this, R.style.TextAppearance_AppCompat_Medium);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0,
                    TableLayout.LayoutParams.MATCH_PARENT, 1);
            layoutParams.setMargins(0,0,5,0);
            textView.setLayoutParams(layoutParams);
            tableRow.addView(textView, 0);

            EditText editText = new EditText(this);
            editText.setText(componentsOfComplex.get(i).code);
            editText.setGravity(Gravity.CENTER);
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            layoutParams = new TableRow.LayoutParams(0,
                    TableLayout.LayoutParams.MATCH_PARENT, 3);
            layoutParams.setMargins(0,0,5,0);
            editText.setLayoutParams(layoutParams);
            tableRow.addView(editText, 1);
            editText.setOnFocusChangeListener(this);
            editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        String text = v.getText().toString();
                        text = text.replaceAll("(//.*)+", "");
                        v.setText(text);
                        codeIndex = codes.indexOf(v);
                        if(codeIndex > -1)
                        {
                            EditText et = (EditText)codes.get(codeIndex);
                            et.setSelection(et.getText().length());
                            if(codeIndex < codes.size() - 1)
                            {
                                codeIndex++;
                                et = (EditText)codes.get(codeIndex);
                                et.requestFocus();
                                et.setSelection(et.getText().length());
                            }
                        }
                        BindComplexActivity.this.saveCodes();
                    }
                    return true;
                }
            });
            codes.add(editText);

            Button scanBtn = new Button(this);
            scanBtn.setText("Скан");
            scanBtn.setBackgroundResource(R.drawable.add_btn);
            layoutParams = new TableRow.LayoutParams(0,
                    TableLayout.LayoutParams.MATCH_PARENT, 1);
            //layoutParams.setMargins(0,5,0,5);
            scanBtn.setLayoutParams(layoutParams);
            scanBtn.setTextColor(Color.WHITE);
            scanBtn.setOnClickListener(this);
            tableRow.addView(scanBtn, 2);
            scanBtns.add(scanBtn);


            tableRow.setOnClickListener(this);

            View view = new View(this);
            layoutParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    2);
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


            componentsOfComplexTable.addView(tableRow, i+divcount);
            if(i < componentsOfComplex.size() - 1)
                componentsOfComplexTable.addView(view, i+ ++divcount);
        }
    }


    private void saveCodes()
    {
        if(codes != null)
        {
            for(int i=0;i<codes.size();i++)
            {
                if(codes.get(i).isFocused())
                {
                    componentsOfComplex.get(i).code = ((EditText)codes.get(i)).getText().toString();
                    break;
                }
            }
        }
        complexModel.addComponentsToComplex(complex_id, componentsOfComplex);
        Toast.makeText(getApplicationContext(),
                "Информация о комплексе сохранена",
                Toast.LENGTH_SHORT).show();
    }

    private void updateAddressList()
    {
        if(spinnerObjectForComplexList.getSelectedItemPosition() == 0)
        {
            if(addresses != null)
                addresses.clear();
            else
                return;
        }
        else
        {
            int object_id = objects.get(spinnerObjectForComplexList.getSelectedItemPosition() - 1).id;
            addresses = addressModel.getAddressesByObjectId(object_id);
        }
        ArrayList<String> strAddr = new ArrayList<String>();
        strAddr.add("Не задано");
        int lastAddressPos = -1;
        for(int i=0;i < addresses.size();i++)
        {
            Address current = addresses.get(i);
            strAddr.add(current.city + ", " + current.street + ", " + current.building);
            if(addresses.get(i).id == lastSelection.addressId)
                lastAddressPos = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, strAddr);
        spinnerAddressForComplexList.setAdapter(adapter);
        spinnerAddressForComplexList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(!orientationChangedForAddress)
                {
                    System.out.println("address orientation changed");
                    saveComplexInfo();
                    BindComplexActivity.this.updateRoomsList();
                }
                else{
                    orientationChangedForAddress = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        if(first)
        {
            first = false;
            if(complex_id <= 0)
            {
                first = true;
                System.out.println("1 update select");
                spinnerAddressForComplexList.setSelection(lastAddressPos + 1);
                return;
            }
            int address_pos = -1;
            int addressId = complexModel.getAddressIdOfComplex(complex_id);
            for(int i=0;i<addresses.size();i++)
            {
                if(addresses.get(i).id == addressId)
                {
                    address_pos = i;
                    break;
                }
            }
            System.out.println("address_pos: "+address_pos);
            if(address_pos > -1)
            {
                first = true;
                System.out.println("2 update select");
                spinnerAddressForComplexList.setSelection(address_pos + 1);
            }
            else
            {
                System.out.println("3 update select");
                spinnerAddressForComplexList.setSelection(0);
            }
        }
    }

    private void updateRoomsList()
    {
        if(spinnerAddressForComplexList.getSelectedItemPosition() == 0)
        {
            if(rooms != null)
                rooms.clear();
            else
                return;
        }
        else
        {
            int address_id = addresses.get(spinnerAddressForComplexList.getSelectedItemPosition() - 1).id;
            rooms = roomModel.getCabinetsByAddressId(address_id);
        }
        ArrayList<String> roomNames = new ArrayList<String>();
        roomNames.add("Не задано");
        int lastRoomPos = -1;
        for(int i=0;i < rooms.size();i++)
        {
            roomNames.add(rooms.get(i).name);
            if(rooms.get(i).id == lastSelection.roomId)
                lastRoomPos = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, roomNames);
        spinnerRoomsForComplexList.setAdapter(adapter);
        spinnerRoomsForComplexList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(!orientationChangedForRoom)
                {
                    System.out.println("room orientation changed");
                    saveComplexInfo();
                }
                else{
                    orientationChangedForRoom = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        if(first)
        {
            first = false;
            if(complex_id <= 0)
            {
                spinnerRoomsForComplexList.setSelection(lastRoomPos + 1);
                return;
            }
            int room_pos = -1;
            int roomId = complexModel.getRoomIdOfComplex(complex_id);
            for(int i=0;i<rooms.size();i++)
            {
                if(rooms.get(i).id == roomId)
                {
                    room_pos = i;
                    break;
                }
            }
            if(room_pos > -1)
            {
                spinnerRoomsForComplexList.setSelection(room_pos + 1);
            }
            else
            {
                spinnerRoomsForComplexList.setSelection(0);
            }
        }
    }

    private int saveComplexType()
    {
        int complex_type_id;
        //находим идентификатор типа комплекса по выбранной позиции спиннера
        if(spinnerComplexTypesList.getSelectedItemPosition() > 0)
        {
            complex_type_id = complexTypes.get(spinnerComplexTypesList.getSelectedItemPosition() - 1).id;
        }
        //если вырано "Не задано", то выходим
        else
        {
            return -1;
        }
        //сохраняем комплекс, и привязываем к нему тип
        complex_id = complexModel.bindComplexTypeToComplex(complex_type_id, complex_id);
        //если комплекс новый, то подгружаем компоненты, согласно типу
        if(isNew)
        {
            complexModel.updateComponentsFromComplexType(complex_id);
            isNew = false;
        }
        //добавить компоненты в активити
        updateComponentsTable();
        currentComplex = complexModel.getComplex(complex_id);
        return complex_type_id;
    }


    private void setDeleteButtonEnabled(boolean enabled)
    {
        if(enabled)
            deleteComponentFromComplexBtn.setBackgroundResource(R.drawable.add_btn);
        else
            deleteComponentFromComplexBtn.setBackgroundResource(R.drawable.disable_btn);
        deleteComponentFromComplexBtn.setEnabled(enabled);
    }

    private void backToParent()
    {
        Intent intent = null;
        if(parent.contentEquals("ShowComplexesActivity"))
        {
            intent = new Intent(getApplicationContext(),ShowComplexesActivity.class);
        }
        if(parent.contentEquals("MainActivity"))
        {
            intent = new Intent(getApplicationContext(),MainActivity.class);
        }
        if(intent != null)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
    }

    private void saveComplexInfo()
    {
        int complex_type_id = saveComplexType();
        int object_id;
        if(spinnerObjectForComplexList.getSelectedItemPosition() > 0)
        {
            object_id = objects.get(spinnerObjectForComplexList.getSelectedItemPosition() - 1).id;
        }
        else
        {
            object_id = 0;
        }

        int address_id;
        if(spinnerAddressForComplexList.getSelectedItemPosition() > 0)
        {
            address_id = addresses.get(spinnerAddressForComplexList.getSelectedItemPosition() - 1).id;
        }
        else
        {
            address_id = 0;
        }
        int room_id;
        if(spinnerRoomsForComplexList.getSelectedItemPosition() > 0)
        {
            room_id = rooms.get(spinnerRoomsForComplexList.getSelectedItemPosition() - 1).id;
        }
        else
        {
            room_id = 0;
        }
        System.out.println("complex_id: "+complex_id);
        lastSelection.complexTypeId = complex_type_id;
        lastSelection.objectId = object_id;
        lastSelection.addressId = address_id;
        lastSelection.roomId = room_id;
        lastSelectionModel.updateLastSelection(lastSelection);
        String appendix = inputAppendixET.getText().toString();
        String ipAddress = inputIPET.getText().toString();
        String idByAddress = inputAddressIdentityET.getText().toString();
        complexModel.saveComplexInfo(complex_id,
                address_id,
                room_id,
                appendix,
                ipAddress,
                idByAddress);
        System.out.println("complex_id: "+complex_id
                + " address_id: "+address_id +
                " room_id: "+room_id);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.backBindComplexBtn)
        {
            backToParent();
            return;
        }
        if(view.getId() == R.id.saveComplexTypeBtn)
        {
            int complex_type_id = saveComplexType();

            //сохраняем выбранную позицию в последнее выбанное
            lastSelection.complexTypeId = complex_type_id;
            lastSelectionModel.updateLastSelection(lastSelection);
            setEnabledBindedButtons(true);
            return;
        }
        if(view.getId() == R.id.saveComplexBtn)
        {
            saveComplexInfo();
            backToParent();
            return;
        }
        if(view.getId() == R.id.deleteComponentFromComplexBtn)
        {
            int index = 0;
            if(lastRow > 0)
                index = lastRow/2;
            complexHasComponentModel.deleteComponentFromComplex(componentsOfComplex.get(index).id);
            updateComponentsTable();
            lastRow = -1;
            return;
        }
        if(view.getId() == R.id.addComponentToComplexBtn)
        {
            int component_id = components.get(spinnerComponentForComplexList.getSelectedItemPosition()).id;
            complexHasComponentModel.addComponentToComplex(complex_id, component_id);
            updateComponentsTable();
            return;
        }
        if(view.getId() == R.id.takePhotoBtn)
        {
            dispatchTakePictureIntent();
        }
        if(view.getId() == R.id.saveAndNewBtn)
        {
            saveComplexInfo();
            Intent intent = new Intent(getApplicationContext(),BindComplexActivity.class);
            intent.putExtra("parent", "ShowComplexesActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        int currentRow = componentsOfComplexTable.indexOfChild(view);
        if(currentRow > -1)
        {
            if(lastRow == currentRow)
            {
                Drawable background = componentsOfComplexTable.getChildAt(lastRow).getBackground();
                if(background != null)
                {
                    if(((ColorDrawable)background).getColor() == Color.TRANSPARENT)
                    {
                        componentsOfComplexTable.getChildAt(lastRow).setBackgroundColor(Color.GRAY);
                        setDeleteButtonEnabled(true);
                    }
                    else
                    {
                        componentsOfComplexTable.getChildAt(lastRow).setBackgroundColor(Color.TRANSPARENT);
                        setDeleteButtonEnabled(false);
                    }
                }
                return;
            }
            else if(lastRow > -1)
            {
                componentsOfComplexTable.getChildAt(lastRow).setBackgroundColor(Color.TRANSPARENT);
                setDeleteButtonEnabled(false);
            }
            lastRow = currentRow;
            componentsOfComplexTable.getChildAt(lastRow).setBackgroundColor(Color.GRAY);
            setDeleteButtonEnabled(true);
            return;
        }
        scanBtnIndex = scanBtns.indexOf(view);
        System.out.println("scanBtnIndex: "+scanBtnIndex);
        if(scanBtnIndex > -1)
        {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b)
        {
            codeIndex = codes.indexOf(view);
            if(codeIndex > -1)
            {
                componentsOfComplex.get(codeIndex).code = ((EditText)codes.get(codeIndex)).getText().toString();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            complexModel.saveComplexPhoto(mCurrentPhotoPath, complex_id);
            currentComplex.setPhotoPath(mCurrentPhotoPath);
            setPhoto();
            return;
        }
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        int childIndex = scanBtnIndex*2;
        System.out.println("childIndex: "+scanBtnIndex);
        System.out.println("childCount: "+componentsOfComplexTable.getChildCount());
        TableRow tableRow = (TableRow)componentsOfComplexTable.getChildAt(childIndex);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            ((EditText)tableRow.getChildAt(1)).setText(scanContent);
            componentsOfComplex.get(scanBtnIndex).code = scanContent;
            saveCodes();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "ARM_"+complex_id;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
