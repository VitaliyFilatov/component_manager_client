package com.example.vitaliy.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vitaliy.myapplication.Entity.CommonInfo;
import com.example.vitaliy.myapplication.Model.AddressModel;
import com.example.vitaliy.myapplication.Model.DBHelper;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private CommonInfo object;
    private EditText inputCity, inputStreet, inputBuilding;
    private AddressModel addressModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        addressModel = new AddressModel(this);
        Bundle bundle = getIntent().getBundleExtra("object");
        object = new CommonInfo(bundle.getInt("id"),
                bundle.getString("name"));
        TextView objectLbl = (TextView)findViewById(R.id.objectLbl);
        objectLbl.setText(object.name);
        inputCity = (EditText)findViewById(R.id.inputCity);
        inputStreet = (EditText)findViewById(R.id.inputStreet);
        inputBuilding = (EditText)findViewById(R.id.inputBuilding);
        Button okAddAddr = (Button)findViewById(R.id.okAddAddr);
        Button cancelAddAddr = (Button)findViewById(R.id.cancelAddAddr);
        okAddAddr.setOnClickListener(this);
        cancelAddAddr.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.cancelAddAddr)
        {
            toParentActivity();
        }
        if(view.getId() == R.id.okAddAddr)
        {
            addressModel.addNewAddressToObject(object.id,
                    inputCity.getText().toString(),
                    inputStreet.getText().toString(),
                    inputBuilding.getText().toString());
            toParentActivity();
        }
    }

    private void toParentActivity()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("id", object.id);
        bundle.putString("name", object.name);
        Intent intent = new Intent(getApplicationContext(),AddressManagerActivity.class);
        intent.putExtra("object", bundle);
        startActivity(intent);
    }
}
