package com.example.ui_caro_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Caro_with_AI extends AppCompatActivity {
    final static  int maxN=15;
    private ImageView[][] cell=new ImageView[maxN][maxN];
    private Context context;
    private Drawable[] drawCell=new Drawable[4];
    private Button btnPlay,btnBack;
    private TextView tvTurn;
    private  int winnerPlay;
    private boolean firstMove;
    private int xMove,yMove;
    private int[][] valueCell=new int[maxN][maxN];
    private int turnPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caro_with_ai);
        context=this;
        setListen();
        loadResources();
        designBoardGame();
    }
    private void setListen(){
        btnPlay=(Button) findViewById(R.id.playBtn);
        btnBack=(Button) findViewById(R.id.btnBack_ai);
        tvTurn=(TextView) findViewById(R.id.tvTurn);
        btnPlay.setText("Play Game");
        tvTurn.setText("Press button to play game");
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_game();
                play_game();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Caro_with_AI.this,HomeActivity.class);
                startActivity(intent);
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
        //Player is 1, AI is 2
        if(turnPlay==1){
            Toast.makeText(context,"Player first",Toast.LENGTH_SHORT).show();
            player1Turn();
        }else{
            Toast.makeText(context,"Bot first",Toast.LENGTH_SHORT).show();
            player2Turn();
        }
    }
    private void player1Turn(){
        tvTurn.setText("Turn of: Player");
        isClicked=false;
    }
    //
    // Dialog thông báo chiến thắng
    public void openDialog(){
        DialogWinner dialogWinner=new DialogWinner();
        dialogWinner.show(getSupportFragmentManager(), "HIHI");
    }
    //
    private boolean checkWinner(){
        if (winnerPlay!=0) return true;
        VectorEnd(xMove,0,0,1,xMove,yMove);
        if (winnerPlay!=0) return true;

        VectorEnd(0,yMove,1,0,xMove,yMove);
        if (winnerPlay!=0) return true;

        if(xMove+yMove>=maxN-1){
            VectorEnd(maxN-1,xMove+yMove-maxN+1,-1,1,xMove,yMove);
        }
        else if(xMove<=yMove){
            VectorEnd(xMove-yMove+maxN-1,maxN-1,-1,-1,xMove,yMove);
        }else{
            VectorEnd(maxN-1,maxN-1-(xMove-yMove),-1,-1,xMove,yMove);
        }
        if(winnerPlay!=0) return  true;
        return false;
    }
    private void VectorEnd(int xx,int yy,int vx,int vy,int rx,int ry){
        if (winnerPlay!=0){
            return;
        }
        final int range=4;
        int i,j;
        int xbelow=rx-range*vx;
        int ybelow=ry-range*vy;
        int xabove=rx+range*vx;
        int yabove=ry+range*vy;
        String st="";
        i=xx;
        j=yy;
        while (!inside(i,xbelow,xabove)|| !inside(j,ybelow,yabove)){
            i+=vx;
            j+=vy;
        }
        while (true){
            st=st+String.valueOf(valueCell[i][j]);
            if(st.length()==5){
                EvalEnd(st);
                if(winnerPlay!=0){
                    break;
                }
                st=st.substring(1,5);
            }
            i+=vx;
            j+=vy;
            if(!inBoard(i,j)|| !inside(i,xbelow,xabove) || !inside(j,ybelow,yabove)){
                break;
            }
        }
    }
    private boolean inBoard(int i,int j){
        if(i<0||i>=maxN|| j<0 || j>=maxN){
            return false;
        }
        return true;
    }
    private void EvalEnd(String st){
        Toast.makeText(context,st,Toast.LENGTH_SHORT).show();

        switch (st){
            case "11111":
                winnerPlay=1;
                break;
            case "22222":
                winnerPlay=2;
                break;
            default:
                break;
        }
    }
    private boolean inside(int i,int xd, int xt){
        return (i-xd)*(i-xt)<=0;
    }
    private boolean noEmptyCell(){
        for (int i=0;i<maxN;i++){
            for (int j=0;j<maxN;j++){
                if(valueCell[i][j]==0) return false;
            }
        }
        return true;
    }
    private void setValueCell(){
        for (int i=0;i<maxN;i++){
            for (int j=0;j<maxN;j++){
                valueCell[i][j]=0;
            }
        }
    }
    //Bot turn
    private void player2Turn(){
        tvTurn.setText("Turn of: Bot");
        if (firstMove){
            firstMove=false;
            xMove=7;yMove=7;
            makeMove();
        }else{
            //Find best move
            findBotMove();
            makeMove();
        }
    }
    private final int[] iRow={-1,-1,-1,0,1,1,1,0};
    private final int[] iCol={-1,0,1,1,1,0,-1,-1};
    private void findBotMove() {
        List<Integer> listX = new ArrayList<>();
        List<Integer> listY= new ArrayList<Integer>();
        //find empty cell can move, and we we only move two cell in range 2
        final int range=2;
        for(int i=0;i<maxN;i++){
            for(int j=0;j<maxN;j++)
                if(valueCell[i][j]!=0){//not empty
                    for(int t=1;t<=range;t++){
                        for(int k=0;k<8;k++){
                            int x=i+iRow[k]*t;
                            int y=j+iCol[k]*t;
                            if(inBoard(x,y) && valueCell[x][y]==0){
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
        }
        int lx=listX.get(0);
        int ly=listY.get(0);
        //bot always find min board_position_value
        int res= Integer.MAX_VALUE-10;
        for(int i=0;i<listX.size();i++){
            int x=listX.get(i);
            int y=listY.get(i);
            valueCell[x][y]=2;
            int rr=getValue_Position();
            if(rr<res){
                res=rr;lx=x;ly=y;
            }
            valueCell[x][y]=0;
        }
        xMove=lx;yMove=ly;
    }

    private int getValue_Position() {
        //this function will find the board_position_value
        int rr=0;
        int pl=turnPlay;
        //row
        for(int i=0;i<maxN;i++){
            rr+=CheckValue(maxN-1,i,-1,0,pl);
        }
        //column
        for(int i=0;i<maxN;i++){
            rr+=CheckValue(i, maxN - 1, 0, -1, pl);
        }
        //cross right to left
        for(int i=maxN-1;i>=0;i--){
            rr+=CheckValue(i,maxN-1,-1,-1,pl);
        }
        for(int i=maxN-2;i>=0;i--){
            rr+=CheckValue(maxN-1,i,-1,-1,pl);
        }
        //cross left to right
        for(int i=maxN-1;i>=0;i--){
            rr+=CheckValue(i,0,-1,1,pl);
        }
        for(int i=maxN-1;i>=1;i--){
            rr+=CheckValue(maxN-1,i,-1,1,pl);
        }
        return rr;
    }

    private int CheckValue(int xd, int yd, int vx, int vy, int pl) {
        //comback with check value
        int i,j;
        int rr=0;
        i=xd;j=yd;
        String st=String.valueOf(valueCell[i][j]);
        while(true){
            i+=vx;j+=vy;
            if(inBoard(i,j)){
                st=st+String.valueOf(valueCell[i][j]);
                if(st.length()==6){
                    rr+=Eval(st,pl);
                    st=st.substring(1,6);
                }
            } else break;
        }
        return rr;

    }
    private void makeMove(){
        cell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);
        valueCell[xMove][yMove]=turnPlay;
        if(noEmptyCell()){
            Toast.makeText(context,"DRAW!!!",Toast.LENGTH_SHORT).show();
            return;
        }else{
            if(checkWinner()){
                if (winnerPlay==1) {
                    Toast.makeText(context, "Winner is Player"  , Toast.LENGTH_SHORT).show();

                }else if (winnerPlay==2){
                    Toast.makeText(context, "Winner is Bot" , Toast.LENGTH_SHORT).show();

                }
                openDialog();

                return;
            }
        }

        if(turnPlay==1){
            turnPlay=3-turnPlay;
            Toast.makeText(context,"HIHI",Toast.LENGTH_SHORT).show();

            player2Turn();
        }else{
            turnPlay=3-turnPlay;
            Toast.makeText(context,"Haha",Toast.LENGTH_SHORT).show();

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cell[i][j].setBackground(drawCell[3]);
                }
                final int x=i;
                final int y=j;
                cell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {
                        //This is player turn
                        if(turnPlay==1|| !isClicked){
                            if(valueCell[x][y]==0) {
                                isClicked=true;
                                xMove = x;
                                yMove = y;

                                makeMove();
                            }
                        }
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
    // Evaluate Func
    private int Eval(String st, int pl) {
        int b1 = 1, b2 = 1;
        if (pl == 1) {
            b1 = 2;
            b2 = 1;
        } else {
            b1 = 1;
            b2 = 2;
        }
        switch (st) {
            case "111110":return b1* 100000000;
            case "011111":return b1* 100000000;
            case "211111":return b1* 100000000;
            case "111112":return b1* 100000000;
            case "011110":return b1* 10000000;
            case "101110":return b1* 1002;
            case "011101":return b1* 1002;
            case "011112":return b1* 1000;
            case "011100":return b1* 102;
            case "001110":return b1* 102;
            case "210111":return b1* 100;
            case "211110":return b1* 100;
            case "211011":return b1* 100;
            case "211101":return b1* 100;
            case "010100":return b1* 10;
            case "011000":return b1* 10;
            case "001100":return b1* 10;
            case "000110":return b1* 10;
            case "211000":return b1* 1;
            case "201100":return b1* 1;
            case "200110":return b1* 1;
            case "200011":return b1* 1;
            case "222220":return b2* -100000000;
            case "022222":return b2* -100000000;
            case "122222":return b2* -100000000;
            case "222221":return b2* -100000000;
            case "022220":return b2* -10000000;
            case "202220":return b2* -1002;
            case "022202":return b2* -1002;
            case "022221":return b2* -1000;
            case "022200":return b2* -102;
            case "002220":return b2* -102;
            case "120222":return b2* -100;
            case "122220":return b2* -100;
            case "122022":return b2* -100;
            case "122202":return b2* -100;
            case "020200":return b2* -10;
            case "022000":return b2* -10;
            case "002200":return b2* -10;
            case "000220":return b2* -10;
            case "122000":return b2* -1;
            case "102200":return b2* -1;
            case "100220":return b2* -1;
            case "100022":return b2* -1;
            default:
                break;
        }
        return 0;
    }
}