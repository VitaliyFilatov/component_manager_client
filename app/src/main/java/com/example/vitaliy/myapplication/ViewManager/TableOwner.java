package com.example.vitaliy.myapplication.ViewManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.vitaliy.myapplication.Entity.TableModel;
import com.example.vitaliy.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class TableOwner implements View.OnClickListener {
    private ArrayList<Button> bindedButtons;
    private TableLayout table;
    private int lastRow;
    private ArrayList<TableModel> tableModel;
    private int commonColor, selectedColor;
    private OnRowSelectedListener selectedListener;

    public TableOwner(TableLayout table)
    {
        this.table = table;
        lastRow = -1;
        bindedButtons = new ArrayList<Button>();
        commonColor = Color.TRANSPARENT;
        selectedColor = Color.GRAY;
    }
    public void setOnRowSelectedListener(OnRowSelectedListener selectedListener)
    {
        this.selectedListener = selectedListener;
    }

    public void bindButton(Button btn)
    {
        bindedButtons.add(btn);
        btn.setEnabled(false);
    }

    public void unbindButton(Button btn)
    {
        bindedButtons.remove(btn);
    }

    public int getLastRow()
    {
        return lastRow;
    }

    public void setTableModel(ArrayList<? extends TableModel> tableModel)
    {
        this.tableModel = (ArrayList<TableModel>)tableModel;
        lastRow = -1;
    }

    public void setEnabledBindedBtns(boolean enabled)
    {
        for(int i=0;i<bindedButtons.size();i++)
        {
            if(enabled)
                bindedButtons.get(i).setBackgroundResource(R.drawable.add_btn);
            else
                bindedButtons.get(i).setBackgroundResource(R.drawable.disable_btn);
            bindedButtons.get(i).setEnabled(enabled);
        }
    }

    public void updateTable()
    {
        table.removeViews(0, table.getChildCount());
        for(int i=0;i<tableModel.size();i++)
        {
            TableRow tableRow = new TableRow(table.getContext());

            for(int j=0;j<tableModel.get(i).visibleColumnCount();j++)
            {
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                TextView textView = new TextView(table.getContext());
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                textView.setText(tableModel.get(i).getStringValueOfVisibleColumn(j));
                textView.setGravity(Gravity.CENTER);
                textView.setTextAppearance(table.getContext(), R.style.TextAppearance_AppCompat_Medium);
                textView.setBackgroundResource(R.drawable.border);
                tableRow.addView(textView, j);
            }


            tableRow.setOnClickListener(this);
            table.addView(tableRow, i);
        }
    }

    public void clear()
    {
        tableModel = null;
        table.removeViews(0, table.getChildCount());
    }

    public Bundle getCurrentModel()
    {
        if(lastRow > -1)
        {
            TableModel current = tableModel.get(lastRow);
            Bundle bundle = new Bundle();
            for(int i=0;i<current.columnCount();i++)
            {
                bundle.putString(current.getNameOfColumn(i),
                        current.getStringValueOfColumn(i));
            }
            return bundle;
        }
        return null;
    }

    private void onSelectedRow()
    {
        if(selectedListener != null)
        {
            selectedListener.onRowSelected();
        }
    }

    private void onDeselectedRow()
    {
        if(selectedListener != null)
        {
            selectedListener.onRowDeselected();
        }
    }


    @Override
    public void onClick(View view) {
        int currentRow = table.indexOfChild(view);
        if(currentRow > -1)
        {
            if(lastRow == currentRow)
            {
                Drawable background = table.getChildAt(lastRow).getBackground();
                if(background != null)
                {
                    if(((ColorDrawable)background).getColor() == commonColor)
                    {
                        table.getChildAt(lastRow).setBackgroundColor(selectedColor);
                        setEnabledBindedBtns(true);
                        onSelectedRow();
                    }
                    else
                    {
                        table.getChildAt(lastRow).setBackgroundColor(commonColor);
                        setEnabledBindedBtns(false);
                        onDeselectedRow();
                    }
                }
                return;
            }
            else if(lastRow > -1)
            {
                table.getChildAt(lastRow).setBackgroundColor(commonColor);
                setEnabledBindedBtns(false);
                onDeselectedRow();
            }
            lastRow = currentRow;
            table.getChildAt(lastRow).setBackgroundColor(selectedColor);
            setEnabledBindedBtns(true);
            onSelectedRow();
        }
    }
}
