package com.example.ui_caro_game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    Button login_button,register_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

    }
    @Override
    protected void onStart() {
        super.onStart();
        login_button= findViewById(R.id.login_button);
        register_button=findViewById(R.id.register_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home_page= new Intent(MainActivity.this,HomeActivity.class);
                startActivity(home_page);
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register_page=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(register_page);

            }
        });
    };
}