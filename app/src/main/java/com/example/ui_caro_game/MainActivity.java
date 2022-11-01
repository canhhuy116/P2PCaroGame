package com.example.ui_caro_game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    Button play_with_a_friend,play_vs_robot;
    ImageButton setting,continue_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }
    @Override
    protected void onStart() {
        super.onStart();
        setting=findViewById(R.id.setting_button);
        continue_btn=findViewById(R.id.continue_btn);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            setting_dialog_show();
            }
        });
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MainActivity.this,ContinueActivity.class);
                startActivity(intent);

            }
        });


    }

    private void initUI()
    {
        play_with_a_friend= findViewById(R.id.play_with_friend);
        play_vs_robot=findViewById(R.id.play_now);

    }
    void setting_dialog_show()
    {
        setting_dialog dialog=new setting_dialog(this);
        dialog.show();
    }
}