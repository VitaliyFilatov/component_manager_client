package com.example.vitaliy.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Model.ComplexTypeHasComponentModel;
import com.example.vitaliy.myapplication.Model.ComplexTypeModel;
import com.example.vitaliy.myapplication.Model.ComponentModel;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.ViewManager.KeyboardHider;
import com.example.vitaliy.myapplication.ViewManager.TableOwner;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ComponentsOfComplexTypeActivity extends BaseActivity implements View.OnClickListener {

    private ComponentModel componentModel;
    private ComplexTypeModel complexTypeModel;
    private ComplexTypeHasComponentModel complexTypeHasComponentModel;

    private boolean isNew;
    private CommonInfo complexType;
    private TableOwner tableOwner;
    private Spinner spinnerComponentList;
    private EditText complexTypeET;
    private Button addComponentToComplexType, toComponentManagerBtn, deleteComponentFromCT;
    private int lastRow;
    private ArrayList<CommonInfo> components;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_components_of_complex_type);
        attachKeyboardListeners((ViewGroup)findViewById(R.id.rootComponentsOfCT));

        componentModel = new ComponentModel(this);
        complexTypeModel = new ComplexTypeModel(this);
        complexTypeHasComponentModel = new ComplexTypeHasComponentModel(this);

        lastRow = -1;
        addComponentToComplexType = (Button) findViewById(R.id.addComponentToComplexType);
        addComponentToComplexType.setOnClickListener(this);
        Button toComplexTypesBtn = (Button) findViewById(R.id.toComplexTypesBtn);
        toComplexTypesBtn.setOnClickListener(this);
        toComponentManagerBtn = (Button) findViewById(R.id.toComponentManagerBtn);
        toComponentManagerBtn.setOnClickListener(this);
        deleteComponentFromCT = (Button) findViewById(R.id.deleteComponentFromCT);
        deleteComponentFromCT.setOnClickListener(this);
        Button saveComplexTypeName = (Button) findViewById(R.id.saveComplexTypeName);
        saveComplexTypeName.setOnClickListener(this);
        complexTypeET = (EditText) findViewById(R.id.complexType);
        tableOwner = new TableOwner((TableLayout) findViewById(R.id.managerComponentsOfComplexTypeTable));
        tableOwner.bindButton(deleteComponentFromCT);
        if(getIntent().hasExtra("complexType"))
        {
            isNew = false;
            Bundle bundle = getIntent().getBundleExtra("complexType");
            complexType = new CommonInfo(bundle.getInt("id"),
                    bundle.getString("name"));
            complexTypeET.setText(complexType.name);
            updateComponents();
        }
        else
        {
            isNew = true;
            setEnabledManageBtns(false);
        }
        spinnerComponentList = (Spinner)findViewById(R.id.spinnerComponentList);
        components = componentModel.getAllComponents();
        ArrayList<String> componentNames = new ArrayList<String>();
        for(int i=0;i<components.size();i++)
        {
            componentNames.add(components.get(i).name);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, componentNames);
        spinnerComponentList.setAdapter(dataAdapter);
    }

    private void setEnabledManageBtns(boolean enabled)
    {
        if(enabled)
        {
            addComponentToComplexType.setBackgroundResource(R.drawable.add_btn);
            deleteComponentFromCT.setBackgroundResource(R.drawable.add_btn);
        }
        else
        {
            addComponentToComplexType.setBackgroundResource(R.drawable.disable_btn);
            deleteComponentFromCT.setBackgroundResource(R.drawable.disable_btn);
        }
        addComponentToComplexType.setEnabled(enabled);
        deleteComponentFromCT.setEnabled(enabled);
    }

    private void updateComponents()
    {
        if(!isNew)
        {
            tableOwner.setTableModel(complexTypeModel.getComponentOfComplexType(complexType.id));
            tableOwner.updateTable();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.saveComplexTypeName){
            if(isNew)
            {
                int id = complexTypeModel.addNewComplexType(complexTypeET.getText().toString());
                complexType = new CommonInfo(id, complexTypeET.getText().toString());
                isNew = false;
                setEnabledManageBtns(true);
            }
            else
            {
                complexTypeModel.updateComplexTypeById(complexType.id, complexTypeET.getText().toString());
                complexType.name = complexTypeET.getText().toString();
            }
            KeyboardHider.hideKeyboardFrom(this, complexTypeET);
            Toast.makeText(getApplicationContext(),
                    "Имя типа сохранено",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(view.getId()==R.id.deleteComponentFromCT){
            if(tableOwner.getLastRow() > -1)
            {
                Bundle componentOfCT = tableOwner.getCurrentModel();
                if(componentOfCT != null)
                {
                    if(componentOfCT.containsKey("id"))
                    {
                        int id = Integer.parseInt(componentOfCT.getString("id"));
                        complexTypeHasComponentModel.deleteComponentFromComplexType(id);
                        updateComponents();
                    }
                }
            }
            return;
        }
        if(view.getId()==R.id.addComponentToComplexType){
            complexTypeHasComponentModel.addComponentToComplexType(complexType.id,
                    components.get(spinnerComponentList.getSelectedItemPosition()).id);
            updateComponents();
            return;
        }
        if(view.getId()==R.id.toComponentManagerBtn){
            Intent intent = new Intent(getApplicationContext(),ComponentManagerActivity.class);
            if(!isNew)
            {
                Bundle bundle = new Bundle();
                bundle.putInt("id", complexType.id);
                bundle.putString("name", complexType.name);
                intent.putExtra("complexType", bundle);
            }
            startActivity(intent);
        }
        if(view.getId()==R.id.toComplexTypesBtn){
            Intent intent = new Intent(getApplicationContext(),ComplexTypesActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        ScrollView componentOfCTScroll = (ScrollView)findViewById(R.id.componentOfCTScroll);

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)componentOfCTScroll.getLayoutParams();
        params.weight = 20;
        componentOfCTScroll.setLayoutParams(params);

        LinearLayout rootComponentsOfCT = (LinearLayout)findViewById(R.id.rootComponentsOfCT);
        rootComponentsOfCT.setWeightSum(65f);
    }

    @Override
    protected void onHideKeyboard() {
        ScrollView componentOfCTScroll = (ScrollView)findViewById(R.id.componentOfCTScroll);

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)componentOfCTScroll.getLayoutParams();
        params.weight = 60;
        componentOfCTScroll.setLayoutParams(params);

        LinearLayout rootComponentsOfCT = (LinearLayout)findViewById(R.id.rootComponentsOfCT);
        rootComponentsOfCT.setWeightSum(110f);
    }
}
