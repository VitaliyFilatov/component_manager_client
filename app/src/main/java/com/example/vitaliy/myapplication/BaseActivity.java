package com.example.vitaliy.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

public class BaseActivity extends AppCompatActivity {
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(startHeight < 0)
            {
                startHeight = rootLayout.getHeight();
            }
            else
            {
                if(rootLayout.getHeight() >= startHeight){
                    onHideKeyboard();
                } else {
                    //int keyboardHeight = heightDiff - contentViewTop;
                    onShowKeyboard(0);
                }
            }
        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;
    private int startHeight;

    protected void onShowKeyboard(int keyboardHeight) {}
    protected void onHideKeyboard() {}

    protected void attachKeyboardListeners(ViewGroup viewGroup) {
        if (keyboardListenersAttached) {
            return;
        }

        startHeight = -1;
        rootLayout = viewGroup;
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}