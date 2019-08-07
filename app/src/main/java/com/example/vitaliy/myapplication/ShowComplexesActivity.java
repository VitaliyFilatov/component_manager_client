package com.example.vitaliy.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitaliy.myapplication.CustomElement.InteractiveScrollView;
import com.example.vitaliy.myapplication.Entity.ComplexInfo;
import com.example.vitaliy.myapplication.Entity.Filter;
import com.example.vitaliy.myapplication.Entity.Settings;
import com.example.vitaliy.myapplication.Model.ComplexModel;
import com.example.vitaliy.myapplication.Model.FilterModel;
import com.example.vitaliy.myapplication.Service.SingletonSettings;

import java.util.ArrayList;

public class ShowComplexesActivity extends AppCompatActivity implements View.OnClickListener, InteractiveScrollView.OnBottomReachedListener {

    private ComplexModel complexModel;
    private FilterModel filterModel;
    private TableLayout complexesTable;
    private ArrayList<ComplexInfo> complexes;
    private int lastRow;
    private Button changeComplexBtn, deleteComplexBtn;
    private int complexTypeId, objectId, addressId, roomId, withUnset;
    private String additionalInfo,
            ipAddress,
            idByAddress;
    private boolean currentRowState;
    private Settings settings;
    private String componentCode;

    private int lightResource;
    private InteractiveScrollView complexesScroll;

    private enum ComplexModelState {
        ALLCOMPLEXES,
        COMPLEXBYCONDITION,
        COMPLEXBYCOMPONENTCODE
    }

    ;

    private ComplexModelState state;
    private boolean complexesLoaded;

    @Override
    public void onBottomReached() {
        if(complexesLoaded)
            return;
        ArrayList<ComplexInfo> addComplexes = new ArrayList<ComplexInfo>();
        switch (state) {
            case ALLCOMPLEXES: {
                addComplexes = complexModel.getAllComplexes();
                break;
            }
            case COMPLEXBYCOMPONENTCODE: {
                addComplexes = complexModel.getAllComplexesByComponentCode(componentCode);
                break;
            }
            case COMPLEXBYCONDITION: {
                addComplexes = complexModel.getAllComplexesByCondition(complexTypeId,
                        objectId,
                        addressId,
                        roomId,
                        withUnset,
                        additionalInfo,
                        ipAddress,
                        idByAddress);
                break;
            }
        }
        if (complexes == null) {
            complexes = addComplexes;
        } else {
            complexes.addAll(addComplexes);
        }
        for (int i = 0; i < addComplexes.size(); i++) {
            System.out.println("id: " + addComplexes.get(i).id);
        }
        updateComplexesTable(addComplexes);
        if(addComplexes.isEmpty())
        {
            Toast.makeText(getApplicationContext(),
                    "Все комплексы загружены",
                    Toast.LENGTH_SHORT).show();
            complexesLoaded = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_complexes);
        settings = SingletonSettings.getInstance(this).getSettings();
        complexModel = new ComplexModel(this, settings.pageSize);
        filterModel = new FilterModel(this);
        Filter filter = filterModel.getFilter();
        changeComplexBtn = (Button) findViewById(R.id.changeComplexBtn);
        changeComplexBtn.setOnClickListener(this);
        deleteComplexBtn = (Button) findViewById(R.id.deleteComplexBtn);
        deleteComplexBtn.setOnClickListener(this);
        state = ComplexModelState.ALLCOMPLEXES;
        complexesLoaded = false;

        if (settings.lightUnset) {
            lightResource = R.drawable.row_border_red;
        } else {
            lightResource = R.drawable.row_border;
        }

        Button newComplexBtn = (Button) findViewById(R.id.newComplexBtn);
        newComplexBtn.setOnClickListener(this);

//        complexTypeId = getIntent().getIntExtra("complexTypeId", -1);
//        objectId = getIntent().getIntExtra("objectId", -1);
//        addressId = getIntent().getIntExtra("addressId", -1);
//        roomId = getIntent().getIntExtra("roomId", -1);
//        additionalInfo = getIntent().getStringExtra("additionalInfo");
//        ipAddress = getIntent().getStringExtra("ipAddress");
//        idByAddress = getIntent().getStringExtra("idByAddress");
//        withUnset = getIntent().getIntExtra("withUnset", -1);
        complexesTable = (TableLayout) findViewById(R.id.complexesTable);
        complexTypeId = filter.complexTypeId;
        objectId = filter.objectId;
        addressId = filter.addressId;
        roomId = filter.roomId;
        additionalInfo = filter.additionalInfo;
        ipAddress = filter.ipAddress;
        idByAddress = filter.idByAddress;
        withUnset = filter.withUnset;

        if (getIntent().hasExtra("componentCode")) {
            componentCode = getIntent().getStringExtra("componentCode");
            complexes = complexModel.getAllComplexesByComponentCode(componentCode);
            state = ComplexModelState.COMPLEXBYCOMPONENTCODE;
        } else {
            complexes = complexModel.getAllComplexesByCondition(complexTypeId,
                    objectId,
                    addressId,
                    roomId,
                    withUnset,
                    additionalInfo,
                    ipAddress,
                    idByAddress);
            state = ComplexModelState.COMPLEXBYCONDITION;

        }

        refreshComplexesTable(complexes);

        Button clearFilterBtn = (Button) findViewById(R.id.clearFilterBtn);
        clearFilterBtn.setOnClickListener(this);

        Button findComplexesBtn = (Button) findViewById(R.id.findComplexesBtn);
        findComplexesBtn.setOnClickListener(this);

        complexesScroll = (InteractiveScrollView) findViewById(R.id.complexesScroll);
        complexesScroll.setOnBottomReachedListener(this);
    }

    private TextView getEmptyTextViewForTable() {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        //textView.setTextAppearance(this, R.style.TextAppearance_AppCompat_Medium);
        textView.setBackgroundResource(R.drawable.border);
        return textView;
    }

    private void setBindedBtnEnabled(boolean enabled) {
        if (enabled) {
            changeComplexBtn.setBackgroundResource(R.drawable.add_btn);
            deleteComplexBtn.setBackgroundResource(R.drawable.add_btn);
        } else {
            changeComplexBtn.setBackgroundResource(R.drawable.disable_btn);
            deleteComplexBtn.setBackgroundResource(R.drawable.disable_btn);
        }
        changeComplexBtn.setEnabled(enabled);
        deleteComplexBtn.setEnabled(enabled);
    }

    private LinearLayout prepareField(String name, String value) {
        LinearLayout innerLinearLayout;
        innerLinearLayout = new LinearLayout(this);
        innerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLinearLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        innerLinearLayout.setWeightSum(4f);
        TextView textView = getEmptyTextViewForTable();
        textView.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        textView.setText(name);
        innerLinearLayout.addView(textView);

        textView = getEmptyTextViewForTable();
        textView.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 3f));
        textView.setText(value);
        innerLinearLayout.addView(textView);
        return innerLinearLayout;
    }

    private void deleteComplex(int id) {
        for (int i = 0; i < complexes.size(); i++) {
            if (complexes.get(i).id == id) {
                complexes.remove(i);
                complexesTable.removeViewAt(i);
                setBindedBtnEnabled(false);
                break;
            }
        }
    }

    private void refreshComplexesTable(ArrayList<ComplexInfo> complexes) {
        setBindedBtnEnabled(false);
        currentRowState = true;
        lastRow = -1;
        complexesTable.removeViews(0, complexesTable.getChildCount());
        updateComplexesTable(complexes);
    }

    private void updateComplexesTable(ArrayList<ComplexInfo> complexes) {
        int startIndex = complexesTable.getChildCount();
        for (int i = 0; i < complexes.size(); i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            if (complexes.get(i).haveUnsetComponents) {
                tableRow.setBackgroundResource(lightResource);
            } else if (complexes.get(i).photoPath == null) {
                tableRow.setBackgroundResource(lightResource);
            } else if (complexes.get(i).photoPath.equals("")) {
                tableRow.setBackgroundResource(lightResource);
            } else {
                tableRow.setBackgroundResource(R.drawable.row_border);
            }
            TextView textView = getEmptyTextViewForTable();
            textView.setText("" + complexes.get(i).id);
            textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tableRow.addView(textView, 0);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


            if (settings.type) {
                linearLayout.addView(prepareField("Тип",
                        complexes.get(i).complex_type.name));
            }

            if (settings.object) {
                linearLayout.addView(prepareField("Объект",
                        complexes.get(i).object.name));
            }

            if (settings.address) {
                linearLayout.addView(prepareField("Адрес",
                        complexes.get(i).address.getStringFullAddress()));
            }

            if (settings.room) {
                linearLayout.addView(prepareField("Кабинет",
                        complexes.get(i).room.name));
            }

            if (settings.additionalInfo) {
                linearLayout.addView(prepareField("Примечание",
                        complexes.get(i).appendix));
            }

            if (settings.ipAddress) {
                linearLayout.addView(prepareField("IP-адрес",
                        complexes.get(i).ipAddress));
            }

            if (settings.idByAddress) {
                linearLayout.addView(prepareField("Идент-ор",
                        complexes.get(i).id_by_address));
            }

            tableRow.addView(linearLayout, 1);

            tableRow.setOnClickListener(this);
            complexesTable.addView(tableRow, startIndex + i);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.changeComplexBtn) {
            Intent intent = new Intent(getApplicationContext(), BindComplexActivity.class);
            intent.putExtra("parent", "ShowComplexesActivity");
            ComplexInfo complexInfo = complexes.get(lastRow);
            intent.putExtra("complex_id", complexInfo.id);
            startActivity(intent);
            return;
        }
        if (view.getId() == R.id.deleteComplexBtn) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Удалить комплекс?");
            dlgAlert.setTitle("Подтверждение");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    complexModel.deleteComplex(complexes.get(lastRow).id);
                    deleteComplex(complexes.get(lastRow).id);

                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            return;
        }
        if (view.getId() == R.id.newComplexBtn) {
            Intent intent = new Intent(getApplicationContext(), BindComplexActivity.class);
            intent.putExtra("parent", "ShowComplexesActivity");
            startActivity(intent);
            return;
        }
        if (view.getId() == R.id.clearFilterBtn) {
            complexTypeId = -1;
            objectId = -1;
            addressId = -1;
            roomId = -1;
            additionalInfo = null;
            ipAddress = null;
            idByAddress = null;
            filterModel.updateFilter(new Filter(-1, -1, -1, -1, -1, "", "", ""));
            complexModel.resetModel();
            complexesLoaded = false;
            complexes = complexModel.getAllComplexes();
            state = ComplexModelState.ALLCOMPLEXES;
            refreshComplexesTable(complexes);
            return;
        }
        if (view.getId() == R.id.findComplexesBtn) {
            Intent intent = new Intent(getApplicationContext(), SearchComplexesActivity.class);
            startActivity(intent);
        }
        int currentRow = complexesTable.indexOfChild(view);
        if (currentRow > -1) {
            if (lastRow == currentRow) {
                Drawable background = complexesTable.getChildAt(lastRow).getBackground();
                if (background != null) {
                    if (currentRowState) {
                        complexesTable.getChildAt(lastRow).setBackgroundColor(Color.GRAY);
                        setBindedBtnEnabled(true);
                        currentRowState = false;
                    } else {
                        if (complexes.get(lastRow).haveUnsetComponents) {
                            complexesTable.getChildAt(lastRow).setBackgroundResource(lightResource);
                        } else if (complexes.get(lastRow).photoPath == null) {
                            complexesTable.getChildAt(lastRow).setBackgroundResource(lightResource);
                        } else if (complexes.get(lastRow).photoPath.equals("")) {
                            complexesTable.getChildAt(lastRow).setBackgroundResource(lightResource);
                        } else {
                            complexesTable.getChildAt(lastRow).setBackgroundResource(R.drawable.row_border);
                        }
                        setBindedBtnEnabled(false);
                        currentRowState = true;
                    }
                }
                return;
            } else if (lastRow > -1) {
                if (complexes.get(lastRow).haveUnsetComponents) {
                    complexesTable.getChildAt(lastRow).setBackgroundResource(lightResource);
                } else if (complexes.get(lastRow).photoPath == null) {
                    complexesTable.getChildAt(lastRow).setBackgroundResource(lightResource);
                } else if (complexes.get(lastRow).photoPath.equals("")) {
                    complexesTable.getChildAt(lastRow).setBackgroundResource(lightResource);
                } else {
                    complexesTable.getChildAt(lastRow).setBackgroundResource(R.drawable.row_border);
                }
                setBindedBtnEnabled(false);
                currentRowState = true;
            }
            lastRow = currentRow;
            complexesTable.getChildAt(lastRow).setBackgroundColor(Color.GRAY);
            setBindedBtnEnabled(true);
            currentRowState = false;
        }
    }
}
