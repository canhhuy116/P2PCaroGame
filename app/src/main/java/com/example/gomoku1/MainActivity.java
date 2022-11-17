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
    private void player2Turn(){
        tvTurn.setText("Turn of: Player 2");
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
                    Toast.makeText(context, "Winner is Player 1"  , Toast.LENGTH_SHORT).show();

                }else if (winnerPlay==2){
                    Toast.makeText(context, "Winner is Player 2" , Toast.LENGTH_SHORT).show();

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
                cell[i][j].setBackground(drawCell[3]);
                final int x=i;
                final int y=j;
                cell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {

                        if(valueCell[x][y]==0) {
                            xMove = x;
                            yMove = y;

                            makeMove();
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
}