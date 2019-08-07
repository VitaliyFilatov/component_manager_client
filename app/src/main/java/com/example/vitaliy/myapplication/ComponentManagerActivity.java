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
import com.example.vitaliy.myapplication.Model.ComponentModel;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.ViewManager.KeyboardHider;
import com.example.vitaliy.myapplication.ViewManager.TableOwner;

import java.util.ArrayList;

public class ComponentManagerActivity extends BaseActivity implements View.OnClickListener {

    private ComponentModel componentModel;
    private CommonInfo complexType;
    private EditText inputComponentName;
    private TableOwner tableOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_manager);
        attachKeyboardListeners((ViewGroup)findViewById(R.id.rootComponentManager));
        componentModel = new ComponentModel(this);
        if(getIntent().hasExtra("complexType"))
        {

            Bundle bundle = getIntent().getBundleExtra("complexType");
            complexType = new CommonInfo(bundle.getInt("id"),
                    bundle.getString("name"));
        }
        tableOwner = new TableOwner((TableLayout) findViewById(R.id.allComponentsTable));
        Button deleteComponent = (Button) findViewById(R.id.deleteComponent);
        deleteComponent.setOnClickListener(this);
        tableOwner.bindButton(deleteComponent);
        Button addComponentBtn = (Button) findViewById(R.id.addComponentBtn);
        addComponentBtn.setOnClickListener(this);
        Button toComponentOfCT = (Button) findViewById(R.id.toComponentOfCT);
        toComponentOfCT.setOnClickListener(this);
        inputComponentName = (EditText) findViewById(R.id.inputComponentName);
        updateComponents();
    }


    private void updateComponents()
    {
        tableOwner.setTableModel(componentModel.getAllComponents());
        tableOwner.updateTable();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.deleteComponent){
            if(tableOwner.getLastRow() > -1)
            {
                Bundle component = tableOwner.getCurrentModel();
                if(component != null)
                {
                    if(component.containsKey("id"))
                    {
                        int id = Integer.parseInt(component.getString("id"));
                        componentModel.deleteComponent(id);
                        updateComponents();
                    }
                }
            }
            return;
        }
        if(view.getId()==R.id.addComponentBtn){
            componentModel.addNewComponent(inputComponentName.getText().toString());
            updateComponents();
            KeyboardHider.hideKeyboardFrom(this, inputComponentName);
            inputComponentName.setText("");
            return;
        }
        if(view.getId()==R.id.toComponentOfCT){
            Intent intent = new Intent(getApplicationContext(),ComponentsOfComplexTypeActivity.class);
            if(complexType != null)
            {
                Bundle bundle = new Bundle();
                bundle.putInt("id", complexType.id);
                bundle.putString("name", complexType.name);
                intent.putExtra("complexType", bundle);
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        ScrollView componentManagerScroll = (ScrollView)findViewById(R.id.componentManagerScroll);

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)componentManagerScroll.getLayoutParams();
        params.weight = 20;
        componentManagerScroll.setLayoutParams(params);

        LinearLayout rootComponentManager = (LinearLayout)findViewById(R.id.rootComponentManager);
        rootComponentManager.setWeightSum(55f);
    }

    @Override
    protected void onHideKeyboard() {
        ScrollView componentManagerScroll = (ScrollView)findViewById(R.id.componentManagerScroll);

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)componentManagerScroll.getLayoutParams();
        params.weight = 65;
        componentManagerScroll.setLayoutParams(params);

        LinearLayout rootComponentManager = (LinearLayout)findViewById(R.id.rootComponentManager);
        rootComponentManager.setWeightSum(100f);
    }
}
