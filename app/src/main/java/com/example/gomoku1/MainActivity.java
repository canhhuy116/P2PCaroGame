package com.example.gomoku1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
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
    private Pair<Integer, Boolean> ans1;
    private Pair<Integer, Boolean> ans2;
    private int Count  = 0 ;
    private boolean rightRule;
    private boolean playwithBot = false;
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


    private void machineTurn() {
        tvTurn.setText("Turn of: Machine");
    }

    private void makeMove(){
        cell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);

        if(turnPlay==1){
            turnPlay=3-turnPlay;
            valueCell[xMove][yMove] = 1;
            player2Turn();
        }else{
            turnPlay=3-turnPlay;
            valueCell[xMove][yMove] = 2;
            player1Turn();
        }
    }

    boolean inBoard(int x, int y) {
        if (0 <= x && x < maxN && 0 <= y && y < maxN) return true;    //tọa độ con trỏ trong phạm vi bàn cờ
        return false;
    }

    private  Pair<Integer,Boolean> checkLine(int x, int y, int n, int m) {
        int count = 0;
        int newX = x, newY = y;
        while (1 > 0) {
            newX += n;
            newY += m;
            if (valueCell[newX][newY] == valueCell[x][y]) {
                count++;
            } else {
                break;
            }
        }

        return new Pair<Integer, Boolean>(count, valueCell[newX][newY] != 0);
    }


    private  Pair<Integer,Integer> checkLineBot(int x, int y, int n, int m) {
        int count = 0;
        int newX = x, newY = y;
        while (1 > 0) {
            newX += n;
            newY += m;
            if (valueCell[newX][newY] == valueCell[x][y]) {
                count++;
            } else {
                break;
            }
        }

        return new Pair<Integer, Integer>(newX, newY);
    }

    private void checkRow(int a, int b, int c, int d) {
        ans1 = checkLine(xMove, yMove, a,b);
        ans2 = checkLine(xMove, yMove, c,d);
        Count = ans1.first + ans2.first + 1;
        rightRule = (ans1.second && ans2.second);
    }


    boolean priority() {
        if (Count >= 3 && !ans1.second && !ans1.second)  {
            return true;
        }

        if (Count >= 4 && ((!ans1.second && ans2.second) || (ans1.second && !ans2.second))) return true;   //thì máy sẽ ưu tiện chặn lại
        return false;
    }

    void addPace(int a, int b, int c, int d) {    //máy sẽ thêm nước đi cho nó
        Pair<Integer, Integer> res1;
        Pair<Integer, Integer> res2;
        res1 = checkLineBot(xMove, yMove, a, b);
        res2 = checkLineBot(xMove, yMove, c, d);
        if (priority()) {
            if (!ans1.second) {
                if (inBoard(res1.first, res1.second)) {
                    roadOfBot1.push_back(res1);
                }
            }
            if (!ans2.second) {
                if (inBoard(res2.first, res2.second)) {
                    roadOfBot1.push_back(res2);
                }
            }
        }
        else {
            if (!ans1.second) {
                if (inBoard(res1.first, res1.second)) {
                    roadOfBot2.push_back(res1);
                }
            }
            if (!ans2.second) {
                if (inBoard(res2.first, res2.second)) {
                    roadOfBot2.push_back(res2);
                }
            }
        }
    }


    private int attackPoint() {
        int res = 0;
        if (Count >= 5 && !(ans1.second && ans2.second)) res += 100;
        if (Count >= 4 && !ans1.second && !ans2.second) res += 20;
        if (Count >= 3 && !ans1.second && !ans2.second) res += 5;
        if (Count >= 4 && !(ans1.second && ans2.second)) res += 5;
        if (Count >= 3 && !(ans1.second && ans2.second)) res += 1;
        if (Count >= 2 && !ans1.second && !ans2.second) res += 1;

        return res;
    }

    private boolean attack() {
        int preX = -1, preY = -1, ans = 0;
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                if (valueCell[i][j] != 0) continue;
                valueCell[i][j].c = 1;




                int res = 0;
                checkRow(0, 1, 0, -1);
                res += attackPoint();

                checkRow(1, 0, -1, 0);
                res += attackPoint();

                checkRow(1, 1, -1, -1);
                res += attackPoint();

                checkRow(1, -1, -1, 1);
                res += attackPoint();

                if (ans < res) {
                    ans = res;
                    preX = i;
                    preY = j;
                }

                valueCell[i][j] = 0;
            }
        }
        if (ans == 0) return false;

        // danh
        cell[preX][preY].setImageDrawable(drawCell[turnPlay]);

        return true;
    }





    private int hasWinner() {
        checkRow(0,1,0,-1);
        if (Count == 5 && !rightRule) {
            return 1;
        }

        checkRow(1,0,-1,0);

        if (Count == 5 && !rightRule) {
            return 2;
        }

        checkRow(1,1,-1,-1);

        if (Count == 5 && !rightRule) {
            return 3;
        }

        checkRow(1,-1,-1,1);


        if (Count == 5 && !rightRule) {
            return 4;
        }

        return 0;
    }


    void effectWin() {
        System.out.print("hi ne");

        if (hasWinner() == 1) {
            System.out.print("thang");
        }

        if (hasWinner() == 2) {
            System.out.print("thang");
        }

        if (hasWinner() == 3) {
            System.out.print("thang");
        }

        if (hasWinner() == 4) {
            System.out.print("thang");
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

                        System.out.print(valueCell[x][y]);
                        Log.d("hahaha", getString(valueCell[x][y]));



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