package com.example.vitaliy.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.Model.ObjectModel;
import com.example.vitaliy.myapplication.ViewManager.KeyboardHider;
import com.example.vitaliy.myapplication.ViewManager.TableOwner;

import java.util.ArrayList;

public class ObjectManagerActivity extends BaseActivity implements View.OnClickListener {

    private ObjectModel objectModel;

    private TableLayout allObjectsTable;
    private int lastObjRow;
    private Button deleteObject, addObjBtn;
    private EditText inputObjName;
    private TableOwner tableOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_manager);
        attachKeyboardListeners((ViewGroup)findViewById(R.id.rootObjectManager));
        tableOwner = new TableOwner((TableLayout) findViewById(R.id.allObjectsTable));
        deleteObject = (Button)findViewById(R.id.deleteObject);
        deleteObject.setOnClickListener(this);
        tableOwner.bindButton(deleteObject);
        addObjBtn = (Button)findViewById(R.id.addObjBtn);
        addObjBtn.setOnClickListener(this);
        Button backInObjManager = (Button)findViewById(R.id.backInObjManager);
        backInObjManager.setOnClickListener(this);
        inputObjName = (EditText)findViewById(R.id.inputObjName);
        objectModel = new ObjectModel(this);
        lastObjRow = -1;
        updateObjectsTable();
    }

    private void updateObjectsTable()
    {
        ArrayList<CommonInfo> objects = objectModel.getAllObjects();
        tableOwner.setTableModel(objects);
        tableOwner.updateTable();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.deleteObject){
            if(tableOwner.getLastRow() > -1)
            {
                Bundle object = tableOwner.getCurrentModel();
                if(object != null)
                {
                    if(object.containsKey("id"))
                    {
                        int id = Integer.parseInt(object.getString("id"));
                        objectModel.deleteObject(id);
                        updateObjectsTable();
                    }
                }
            }
            return;
        }
        if(view.getId()==R.id.addObjBtn){
            objectModel.addNewObject(inputObjName.getText().toString());
            updateObjectsTable();
            KeyboardHider.hideKeyboardFrom(this, inputObjName);
            inputObjName.setText("");
        }
        if(view.getId()==R.id.backInObjManager){
            Intent intent = new Intent(getApplicationContext(),PlacementActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        ScrollView allObjectsScroll = (ScrollView)findViewById(R.id.allObjectsScroll);

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)allObjectsScroll.getLayoutParams();
        params.weight = 30;
        allObjectsScroll.setLayoutParams(params);

        LinearLayout rootObjectManager = (LinearLayout)findViewById(R.id.rootObjectManager);
        rootObjectManager.setWeightSum(60f);
    }

    @Override
    protected void onHideKeyboard() {
        ScrollView allObjectsScroll = (ScrollView)findViewById(R.id.allObjectsScroll);

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)allObjectsScroll.getLayoutParams();
        params.weight = 80;
        allObjectsScroll.setLayoutParams(params);

        LinearLayout rootObjectManager = (LinearLayout)findViewById(R.id.rootObjectManager);
        rootObjectManager.setWeightSum(110f);
    }
}
