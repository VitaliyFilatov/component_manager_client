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

import com.example.vitaliy.myapplication.Entity.Address;
import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Model.AddressModel;
import com.example.vitaliy.myapplication.Model.DBHelper;
import com.example.vitaliy.myapplication.ViewManager.TableOwner;

import java.util.ArrayList;

public class AddressManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private CommonInfo object;
    private TableOwner tableOwner;

    private AddressModel addressModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        addressModel = new AddressModel(this);
        Bundle bundle = getIntent().getBundleExtra("object");
        object = new CommonInfo(bundle.getInt("id"),
                bundle.getString("name"));
        TextView addressManagerObjectLbl = (TextView)findViewById(R.id.addressManagerObjectLbl);
        addressManagerObjectLbl.setText(object.name);
        tableOwner = new TableOwner((TableLayout) findViewById(R.id.allAddressesOfObjTable));
        Button deleteAddress = (Button)findViewById(R.id.deleteAddress);
        deleteAddress.setOnClickListener(this);
        tableOwner.bindButton(deleteAddress);
        Button managerAddAddress = (Button)findViewById(R.id.managerAddAddress);
        managerAddAddress.setOnClickListener(this);
        Button backInAddrManager = (Button)findViewById(R.id.backInAddrManager);
        backInAddrManager.setOnClickListener(this);
        updateAddressTable();
    }

    private void updateAddressTable()
    {
        tableOwner.setTableModel(addressModel.getAddressesByObjectId(object.id));
        tableOwner.updateTable();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.deleteAddress)
        {
            if(tableOwner.getLastRow() > -1)
            {
                Bundle address = tableOwner.getCurrentModel();
                if(address != null)
                {
                    if(address.containsKey("id"))
                    {
                        int id = Integer.parseInt(address.getString("id"));
                        addressModel.deleteAddress(id);
                        updateAddressTable();
                    }
                }
            }
            return;
        }
        if(view.getId() == R.id.managerAddAddress)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("id", object.id);
            bundle.putString("name", object.name);
            Intent intent = new Intent(getApplicationContext(),AddAddressActivity.class);
            intent.putExtra("object", bundle);
            startActivity(intent);
        }
        if(view.getId() == R.id.backInAddrManager)
        {
            Intent intent = new Intent(getApplicationContext(),PlacementActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
