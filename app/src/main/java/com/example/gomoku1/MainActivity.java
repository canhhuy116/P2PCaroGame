package com.example.gomoku1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    final static  int maxN=15;
    private ImageView[][] cell=new ImageView[maxN][maxN];
    private Context context;
    private Drawable[] drawCell=new Drawable[4];
    private Button btnPlay;
    private TextView tvTurn;
    private  int winnerPlay;
    private boolean firstMove;
    private int xMove,yMove;
    private int[][] valueCell=new int[maxN][maxN];
    private int turnPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        setListen();
        loadResources();
        designBoardGame();
    }
    private void setListen(){
        btnPlay=(Button) findViewById(R.id.playBtn);
        tvTurn=(TextView) findViewById(R.id.tvTurn);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_game();
                play_game();
            }
        });
    }
    private void init_game(){
        firstMove=true;
        winnerPlay=0;
        for(int i=0;i<maxN;i++){
            for (int j=0;j<maxN;j++){
                cell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j]=0;
            }
        }
    }
    private void play_game(){
        Random r=new Random();
        turnPlay=r.nextInt(2)+1;
        if(turnPlay==1){
            Toast.makeText(context,"Player 1 first",Toast.LENGTH_SHORT).show();
            player1Turn();
        }else{
            Toast.makeText(context,"Player 2 first",Toast.LENGTH_SHORT).show();
            player2Turn();
        }
    }
    private void player1Turn(){
        tvTurn.setText("Turn of: Player 1");
        if(firstMove){
            firstMove=false;
            makeMove();
        }else{

        }
    }
    private void player2Turn(){
        tvTurn.setText("Turn of: Player 2");
    }
    private void makeMove(){
        cell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);

        if(turnPlay==1){
            turnPlay=3-turnPlay;
            player2Turn();
        }else{
            turnPlay=3-turnPlay;
            player1Turn();
        }
    }
    private void loadResources(){
        drawCell[0]=null;
        drawCell[1]=context.getResources().getDrawable(R.drawable.x);
        drawCell[2]=context.getResources().getDrawable(R.drawable.o);
        drawCell[3]=context.getResources().getDrawable(R.drawable.cell);
    }
    private boolean isClicked;
    private void designBoardGame(){
        int sizeOfCell=Math.round(ScreenWidth()/maxN);
        LinearLayout.LayoutParams lpRow= new LinearLayout.LayoutParams(sizeOfCell*maxN,sizeOfCell);
        LinearLayout.LayoutParams lpcell=new LinearLayout.LayoutParams(sizeOfCell,sizeOfCell);
        LinearLayout boardGame=(LinearLayout) findViewById(R.id.BoardGame);
        for (int i=0;i<maxN;i++){
            LinearLayout lnRow=new LinearLayout(context);
            for (int j=0;j<maxN;j++){
                cell[i][j]=new ImageView(context);
                cell[i][j].setBackground(drawCell[3]);
                final int x=i;
                final int y=j;
                cell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        xMove=x;
                        yMove=y;
                        makeMove();
                    }
                });
                lnRow.addView(cell[i][j],lpcell);
            }
            boardGame.addView(lnRow,lpRow);
        }
    }
    private float ScreenWidth(){
        Resources resources=context.getResources();
        DisplayMetrics dm=resources.getDisplayMetrics();
        return dm.widthPixels;
    }
}