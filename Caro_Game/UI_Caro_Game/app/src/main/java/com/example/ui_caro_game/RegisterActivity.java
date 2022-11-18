package com.example.ui_caro_game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class RegisterActivity extends Activity {
    Button Back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

    }
    @Override
    protected void onStart() {
        super.onStart();
        Back=findViewById(R.id.back_to_login_page);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_page= new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(login_page);
            }
        });
    }
}
