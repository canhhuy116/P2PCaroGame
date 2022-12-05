package com.example.ui_caro_game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class RegisterActivity extends Activity {
    Button Back,register_button;;
    DBHelper DB;
    EditText username,password,confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        username=findViewById(R.id.register_username);
        password=findViewById(R.id.register_password);
        confirm=findViewById(R.id.register_confirm);
        register_button=findViewById(R.id.register_button_2);
        Back=findViewById(R.id.back_to_login_page);
        DB=new DBHelper(this);

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
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=username.getText().toString();
                String pass= password.getText().toString();
                String confirmPassword=confirm.getText().toString();

                if(user.equals("")||pass.equals("")||confirmPassword.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please enter all fields ", Toast.LENGTH_SHORT).show();
                }
                else if (pass.equals(confirmPassword)==false)
                {
                    Toast.makeText(getApplicationContext(),"Password not matching",Toast.LENGTH_SHORT).show();;
                }
                else
                {
                    Boolean checkUser=DB.checkUsername(user);
                    if(checkUser==true)
                    {
                        Toast.makeText(getApplicationContext(),"User already exists",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Boolean insert = DB.insertData(user,pass);
                        if(insert==true)
                        {
                            Toast.makeText(getApplicationContext(),"Registered successfully",Toast.LENGTH_SHORT).show();
                            Intent login_page= new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(login_page);

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Registered failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


    }
}
