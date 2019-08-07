package com.example.vitaliy.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Entity.TableModel;
import com.example.vitaliy.myapplication.Model.ComplexTypeModel;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.ViewManager.OnRowSelectedListener;
import com.example.vitaliy.myapplication.ViewManager.TableOwner;

import java.util.ArrayList;

public class ComplexTypesActivity extends AppCompatActivity implements View.OnClickListener, OnRowSelectedListener {

    private ComplexTypeModel complexTypeModel;
    private TableOwner complexTypeTableOwner, componentTableOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex_types);
        complexTypeModel = new ComplexTypeModel(this);
        complexTypeTableOwner = new TableOwner((TableLayout) findViewById(R.id.allComplexTypesTable));
        componentTableOwner = new TableOwner((TableLayout) findViewById(R.id.componentsOfComplexTypeTable));
        complexTypeTableOwner.setOnRowSelectedListener(this);
        Button deleteComplexTypeBtn = (Button) findViewById(R.id.deleteComplexTypeBtn);
        deleteComplexTypeBtn.setOnClickListener(this);
        Button changeComplexTypeBtn = (Button) findViewById(R.id.changeComplexTypeBtn);
        changeComplexTypeBtn.setOnClickListener(this);
        complexTypeTableOwner.bindButton(deleteComplexTypeBtn);
        complexTypeTableOwner.setOnRowSelectedListener(this);
        complexTypeTableOwner.bindButton(changeComplexTypeBtn);
        Button newComplexType = (Button) findViewById(R.id.newComplexType);
        newComplexType.setOnClickListener(this);
        Button backToMain = (Button) findViewById(R.id.backToMain);
        backToMain.setOnClickListener(this);
        updateComplexTypes();
    }

    private void updateComplexTypes()
    {
        complexTypeTableOwner.setTableModel(complexTypeModel.getAllComplexTypes());
        complexTypeTableOwner.updateTable();
    }



    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.deleteComplexTypeBtn)
        {
            if(complexTypeTableOwner.getLastRow() > -1)
            {
                Bundle complexType = complexTypeTableOwner.getCurrentModel();
                if(complexType != null)
                {
                    if(complexType.containsKey("id"))
                    {
                        int id = Integer.parseInt(complexType.getString("id"));
                        complexTypeModel.deleteComplexType(id);
                        updateComplexTypes();
                        componentTableOwner.clear();
                    }
                }
            }
            return;
        }
        if(view.getId() == R.id.changeComplexTypeBtn)
        {
            Bundle complexType = complexTypeTableOwner.getCurrentModel();
            if(complexType == null)
                return;
            Bundle bundle = new Bundle();
            bundle.putInt("id", Integer.parseInt(complexType.getString("id")));
            bundle.putString("name", complexType.getString("name"));
            Intent intent = new Intent(getApplicationContext(),ComponentsOfComplexTypeActivity.class);
            intent.putExtra("complexType", bundle);
            startActivity(intent);
        }
        if(view.getId() == R.id.newComplexType)
        {
            Intent intent = new Intent(getApplicationContext(),ComponentsOfComplexTypeActivity.class);
            startActivity(intent);
            return;
        }
        if(view.getId() == R.id.backToMain)
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    @Override
    public void onRowSelected() {
        if(complexTypeTableOwner.getLastRow() > -1)
        {
            Bundle complexType = complexTypeTableOwner.getCurrentModel();
            if(complexType != null)
            {
                if(complexType.containsKey("id"))
                {
                    int id = Integer.parseInt(complexType.getString("id"));
                    componentTableOwner.setTableModel(complexTypeModel.getComponentOfComplexType(id));
                    componentTableOwner.updateTable();
                }
            }
        }
    }

    @Override
    public void onRowDeselected() {
        componentTableOwner.setTableModel(new ArrayList<CommonInfo>());
        componentTableOwner.updateTable();
    }

}
