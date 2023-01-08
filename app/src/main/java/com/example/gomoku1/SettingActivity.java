package com.example.carofinal;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    Switch darkMode;
    Switch soundSwitch;
    private ImageView backBtn;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title

        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW); //enable full screen
        setContentView(R.layout.activity_settings);

        darkMode = (Switch)  findViewById(R.id.darkmode_switch);
        soundSwitch = (Switch)  findViewById(R.id.sound_switch);

        backBtn = (ImageView) findViewById(R.id.settings_back_btn);

        if(Services.DARKMODE_CHECK)
        {
            darkMode.setChecked(true);
        }
        else if(!Services.DARKMODE_CHECK)
        {
            darkMode.setChecked(false);
        }

        //Change darkmode handling here
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Services.DARKMODE_CHECK=true;
                }else {
                    Services.DARKMODE_CHECK=false;
                }
            }
        });

        //Sound check - handling here
        if(Services.SOUND_CHECK)
        {
            soundSwitch.setChecked(true);
        }
        else if(!Services.SOUND_CHECK)
        {
            soundSwitch.setChecked(false);
        }


        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Services.SOUND_CHECK =true;
                }
                else {
                    Services.SOUND_CHECK= false;
                }
            }

        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
