package com.example.carofinal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import pl.droidsonroids.gif.GifImageView;

public class ChoiceModeActivity extends AppCompatActivity implements View.OnTouchListener {


    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int ITEM_DELAY = 300;

    public int SCREEN_SIZE;
    public int SET_TRANSLATE;
    private boolean animationStarted = false;
    DBHelper DB;
    private GifImageView settingsGifView;
    private Button WithAFriendBtn , WithAi,MatchHistory,Shopping;
    private ImageView logout;
    Dialog quitdialog;
    TextView name,gold;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choicemode);


        settingsGifView  = (GifImageView) findViewById(R.id.seting_gifview_offline_menu);
        WithAFriendBtn = (Button) findViewById(R.id.playerbtn);
        WithAi = (Button) findViewById(R.id.aibtn);
        MatchHistory=(Button) findViewById(R.id.matchbtn);
        Shopping=(Button) findViewById(R.id.shopbtn);
        logout=(ImageView) findViewById(R.id.logoutbtn) ;

        quitdialog = new Dialog(this);
        DB=new DBHelper(this);


        name=(TextView) findViewById(R.id.user_name_txt);
        name.setText(LoginActivity.user_ID);
        gold=(TextView) findViewById(R.id.player_two_won_txt);


        //DB.updateGold(LoginActivity.user_ID,4);
        LoginActivity.user_Gold=DB.getGold(LoginActivity.user_ID);
        gold.setText(String.valueOf(LoginActivity.user_Gold));



        // settingsGifView.getBackground().Stop();

        // settingsGifView.getAnimation().hasEnded();


        Drawable drawable = settingsGifView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }

        SCREEN_SIZE =getScreenResolution(this);

        if(SCREEN_SIZE >1500)
        {
            SET_TRANSLATE = -560;
        }
        else if(SCREEN_SIZE <=1500)
        {
            SET_TRANSLATE = -300;
        }
        //Play with player
        WithAFriendBtn.setOnTouchListener(this);
        WithAFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceModeActivity.this,ChooseConnectionActivity.class);
                startActivity(intent);
            }
        });
        //Play with ai
        WithAi.setOnTouchListener(this);
        WithAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(    ChoiceModeActivity.this,ChooseSymbolActivity.class);
                startActivity(intent);
            }
        });
        //Match history
        MatchHistory.setOnTouchListener(this);
        MatchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //Shopping
        Shopping.setOnTouchListener(this);
        Shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        logout.setOnTouchListener(this);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitDialogfun();
            }
        });

        //settingsGifView.setOnTouchListener(this);
        settingsGifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler =  new Handler(Looper.getMainLooper());
                Drawable drawable = settingsGifView.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();

                }


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable = settingsGifView.getDrawable();
                        if (drawable instanceof Animatable) {
                            ((Animatable) drawable).stop();

                        }
                        Intent intent = new Intent(ChoiceModeActivity.this,SettingActivity.class);
                        startActivity(intent);

                    }
                }, 500);

            }
        });


    }



    private int getScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        //  Toast.makeText(SplashActivity.this , "Screen height is : "+ height , Toast.LENGTH_SHORT).show();

        return height ;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus || animationStarted) {
            return;
        }


        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == WithAFriendBtn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        else  if (v == WithAi) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        else  if (v == MatchHistory) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        else  if (v == Shopping) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        else  if (v ==logout ) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        return false;
    }

    private void  quitDialogfun() {


        quitdialog.setContentView(R.layout.quit_dialog);
        quitdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        quitdialog.setCanceledOnTouchOutside(false);

        Button continueBtn = quitdialog.findViewById(R.id.continue_btn);
        Button logout2=quitdialog.findViewById(R.id.logoutbtn2);

        logout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitdialog.dismiss();
                Intent intent = new Intent(ChoiceModeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitdialog.dismiss();
            }
        });
        quitdialog.show();
    }
}