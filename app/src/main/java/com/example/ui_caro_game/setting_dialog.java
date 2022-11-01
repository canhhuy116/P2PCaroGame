package com.example.ui_caro_game;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class setting_dialog extends Dialog {

    public setting_dialog( Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
