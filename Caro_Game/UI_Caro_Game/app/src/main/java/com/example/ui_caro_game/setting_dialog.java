package com.example.ui_caro_game;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

public class setting_dialog extends Dialog {
    RadioButton sound_on, sound_off;
    RadioGroup sound_group;
    //ImageButton close_button;
    //setting_dialog dialog;
    //MainActivity mainActivity;

    public setting_dialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);
        sound_on = findViewById(R.id.sound_on);
        sound_off = findViewById(R.id.sound_off);
        sound_group = (RadioGroup) findViewById(R.id.sound_group);
        //close_button = findViewById(R.id.close_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        close_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mainActivity.dialog.dismiss();
//            }
//        });
    }
}
