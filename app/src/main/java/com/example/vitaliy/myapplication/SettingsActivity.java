package com.example.vitaliy.myapplication;

import android.app.ExpandableListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.vitaliy.myapplication.Entity.CheckBoxItem;
import com.example.vitaliy.myapplication.Entity.Settings;
import com.example.vitaliy.myapplication.Model.SettingsModel;
import com.example.vitaliy.myapplication.Service.SingletonSettings;
import com.example.vitaliy.myapplication.ViewManager.ExpandableListViewAdapter;

import java.util.ArrayList;

public class SettingsActivity extends ExpandableListActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private ArrayList<String> groupNames;
    ArrayList<ArrayList<CheckBoxItem>> items;
    Settings settings;
    private ExpandableListViewAdapter adapter;
    private SettingsModel settingsModel;
    private CheckBox lightUnset;
    private EditText inputIpServer, inputPageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        groupNames = new ArrayList<String>();
        groupNames.add("Отображаемые поля");
        settings = SingletonSettings.getInstance(this).getSettings();
        settingsModel = new SettingsModel(this);
        lightUnset = (CheckBox)findViewById(R.id.lightUnset);
        lightUnset.setChecked(settings.lightUnset);
        lightUnset.setOnCheckedChangeListener(this);

        Button saveSettings = (Button)findViewById(R.id.saveSettings);
        saveSettings.setOnClickListener(this);

        inputIpServer = (EditText)findViewById(R.id.inputIpServer);
        inputIpServer.setText(settings.serverIp);

        inputPageSize = (EditText)findViewById(R.id.inputPageSize);
        inputPageSize.setText(String.valueOf(settings.pageSize));



        items = new ArrayList<ArrayList<CheckBoxItem>>();
        ArrayList<CheckBoxItem> item = new ArrayList<CheckBoxItem>();
        item.add(new CheckBoxItem("Тип", settings.type));
        item.add(new CheckBoxItem("Объект", settings.object));
        item.add(new CheckBoxItem("Адрес", settings.address));
        item.add(new CheckBoxItem("Кабинет", settings.room));
        item.add(new CheckBoxItem("Примечания", settings.additionalInfo));
        item.add(new CheckBoxItem("IP-адрес", settings.ipAddress));
        item.add(new CheckBoxItem("Идентификатр по адресу", settings.idByAddress));

        items.add(item);

        adapter = new ExpandableListViewAdapter(this, groupNames, items);
        setListAdapter(adapter);
    }

    public boolean onChildClick(
            ExpandableListView parent,
            View v,
            int groupPosition,
            int childPosition,
            long id) {
        System.out.println("onChildClick: "+childPosition );
        CheckBox cb = (CheckBox)v.findViewById( R.id.check1 );
        if( cb != null ){
            cb.toggle();
            boolean checked = cb.isChecked();
            items.get(groupPosition).get(childPosition).state = checked;
            switch (childPosition)
            {
                case 0:
                {
                    settingsModel.setType(checked);
                    settings.type = checked;
                    break;
                }
                case 1:
                {
                    settingsModel.setObject(checked);
                    settings.object = checked;
                    break;
                }
                case 2:
                {
                    settingsModel.setAddress(checked);
                    settings.address = checked;
                    break;
                }
                case 3:
                {
                    settingsModel.setRoom(checked);
                    settings.room = checked;
                    break;
                }
                case 4:
                {
                    settingsModel.setAdditionalInfo(checked);
                    settings.additionalInfo = checked;
                    break;
                }
                case 5:
                {
                    settingsModel.setIpAddress(checked);
                    settings.ipAddress = checked;
                    break;
                }
                case 6:
                {
                    settingsModel.setIdByAddress(checked);
                    settings.idByAddress = checked;
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        settingsModel.setLightUnset(b);
        settings.lightUnset = b;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.saveSettings)
        {
            settingsModel.setServerIp(inputIpServer.getText().toString());
            settings.setServerIp(inputIpServer.getText().toString());
            settingsModel.setPageSize(Integer.parseInt(inputPageSize.getText().toString()));
            settings.setPageSize(Integer.parseInt(inputPageSize.getText().toString()));
            Toast.makeText(getApplicationContext(),
                    "Настройки сохранены",
                    Toast.LENGTH_LONG).show();
        }
    }
}
