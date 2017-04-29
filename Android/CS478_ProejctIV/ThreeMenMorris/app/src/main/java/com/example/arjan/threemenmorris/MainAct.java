package com.example.arjan.threemenmorris;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static com.example.arjan.threemenmorris.R.id.heading;
import static com.example.arjan.threemenmorris.R.id.startButton;
import static com.example.arjan.threemenmorris.R.id.t00;
import static com.example.arjan.threemenmorris.R.id.t01;
import static com.example.arjan.threemenmorris.R.id.t02;
import static com.example.arjan.threemenmorris.R.id.t10;
import static com.example.arjan.threemenmorris.R.id.t11;
import static com.example.arjan.threemenmorris.R.id.t12;
import static com.example.arjan.threemenmorris.R.id.t20;
import static com.example.arjan.threemenmorris.R.id.t21;
import static com.example.arjan.threemenmorris.R.id.t22;

public class MainAct extends Activity {
    //set value 1 for player1 and 2 for player 2.
    public int [][]board=new int[3][3];
    private Button mButton;
    public TextView[][] tv=new TextView[3][3];
    public static final int NEXT_MOVE=0;
    public static final int PLAYER2_MOVE=2;
    public static final int PLAYER1_MOVE=1;
    public static final int TIE_BREAKER=3;
    static int lastRow=0;
    static int lastCol=0;
    int moveCounter=0;
    public Player1 p1;
    public Player2 p2;
    public Handler uiHandler = new Handler(){
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {

                case PLAYER2_MOVE:
                    dispBoard();
                    System.out.println("Moves elapsed!"+moveCounter);
                    Log.i("TMM", "In UIHandler handlemessage NEXTMOVE");

                    if (checkGameStatus()) {
                        Toast.makeText(MainAct.this, "Game Over! Blue Wins", Toast.LENGTH_SHORT).show();
                    }
                    else if(moveCounter==6 || moveCounter >6) {
                        Toast.makeText(MainAct.this, "Tie Breaker!!!", Toast.LENGTH_SHORT).show();
                        Message msg1 = p2.getHandler().obtainMessage(TIE_BREAKER);
                        p2.getHandler().sendMessage(msg1);
                    }
                    else{
                            //p2.start();
                            Log.i("TMM", "Before obtaining message for P2");
                            Message msg1 = p2.getHandler().obtainMessage(NEXT_MOVE);
                            p2.getHandler().sendMessage(msg1);
                    }
                    break;
                case PLAYER1_MOVE:
                    dispBoard();
                    System.out.println("Moves elapsed!"+moveCounter);
                    Log.i("TMM", "In UIHandler handlemessage PLAYER1_MOVE");
                    if (checkGameStatus()) {
                        Toast.makeText(MainAct.this, "Game Over! Red Wins", Toast.LENGTH_SHORT).show();
                    }
                    else if(moveCounter==6 ||moveCounter >6) {
                        Toast.makeText(MainAct.this, "Tie Breaker!!", Toast.LENGTH_SHORT).show();
                        Message msg1 = p1.getPlayer1Handler().obtainMessage(TIE_BREAKER);
                        p1.getPlayer1Handler().sendMessage(msg1);}
                    else {
                        //p2.start();
                        Log.i("TMM", "Before obtaining message for P1");
                        Message msg1 = p1.getPlayer1Handler().obtainMessage(NEXT_MOVE);
                        p1.getPlayer1Handler().sendMessage(msg1);
                        break;
                    }
            }
        }
    };

    void dispBoard(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
                System.out.print(board[i][j]+" ");
            System.out.println("");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         p1= new Player1();
         p2 = new Player2();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton=(Button)findViewById(startButton);
        //Initializing textViews
        tv[0][0]=(TextView)findViewById(t00);
        tv[0][1]=(TextView)findViewById(t01);
        tv[0][2]=(TextView)findViewById(t02);
        tv[1][0]=(TextView)findViewById(t10);
        tv[1][1]=(TextView)findViewById(t11);
        tv[1][2]=(TextView)findViewById(t12);
        tv[2][0]=(TextView)findViewById(t20);
        tv[2][1]=(TextView)findViewById(t21);
        tv[2][2]=(TextView)findViewById(t22);


     mButton.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v){
            reset();

            p1.start();
            p2.start();
            Message msg1 = p1.player1Handler.obtainMessage(NEXT_MOVE);
            p1.getPlayer1Handler().sendMessage(msg1);

        }
    });
    } //onCreate ends
//function that checks whether the game has ended or not.
    public boolean checkGameStatus(){
        int horCount=0;
        int verCount=0;
        //horizontal check
        for (int i=0;i<3;i++) {
            int valCom = board[i][0];
            horCount=0;
            for (int j = 0; j < 3; j++) {
                if (valCom == board[i][j] && valCom != 0)
                    horCount++;
                if(horCount==3)
                    return true;
            }
        }


            //vertical check
         for (int i=0;i<3;i++) {
             int valCom = board[0][i];
             verCount=0;
             for (int j = 0; j < 3; j++) {
                 if (valCom == board[j][i] && valCom != 0) {
                     verCount++;
                 }
                 if(verCount==3)
                     return true;

             }
         }
        dispBoard();
        Log.i("TMM","Inside checkGame horCount "+horCount);
        if (horCount==3)
            return true;
             Log.i("TMM","Inside checkGame verCount "+verCount);
            if (verCount==3)
                return true;
        return false;
    }
    //function that resets all values
    public void reset(){
        moveCounter=0;
        Log.i("TMM","inside reset");
        p1.interrupt();
        p2.interrupt();
        p1=null;
        p2=null;
        p1=new Player1();
        p2=new Player2();
        for(int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                board[i][j]=0;
                tv[i][j].setBackgroundResource(R.color.gray1);
            }
        }


    }//reset ends
    //worker thread


    class Player1 extends Thread{
        //creating handler for the player1 thread
       public Handler player1Handler = new Handler(){
            public void handleMessage(Message msg){
                int what=msg.what;
                switch(what){
                    case NEXT_MOVE:
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("TMM","Player1 message Handled!!");
                        //nextMove();
                        smartMove();
                        break;
                    case TIE_BREAKER:
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("TMM","Player1 message Handled!!");
                        //nextMove();
                        tieBreaker();
                        break;
                }
            }
        };
        public Handler getPlayer1Handler(){
            return player1Handler;
        }

        public void run(){

            Looper.prepare();
            Log.i("TMM","Player 1 run");
            //board[0][0]=1;
            //tv[0][0].setBackgroundResource(R.color.blue1);// why isnt it throwing an error....
           /* uiHandler.post(new Runnable(){
                public void run(){
                    //tv[0][0].setBackgroundResource(R.color.blue1);
                    try{Log.i("TMM","Sleeping after one move...");
                        Thread.sleep(1000);}
                    catch(InterruptedException e){System.out.println("Thread interrupted!!");}

             }});*/
            Looper.loop();

        }
        void smartMove(){//makes next move on the basis of the previous move
            System.out.println("Inside smartMove lastRow:"+lastRow+" lastCol:"+lastCol);
            if(board[lastRow][lastCol]==1) {
                if ((lastCol + 1) < 3 && board[lastRow][lastCol + 1] == 0) {
                    board[lastRow][lastCol + 1] = 1;
                    lastCol = lastCol + 1;
                } else if ((lastRow + 1 < 3) && board[lastRow + 1][lastCol] == 0) {
                    board[lastRow + 1][lastCol] = 1;
                    lastRow = lastRow + 1;
                } else if ((lastRow - 1 > -1) && board[lastRow - 1][lastCol] == 0) {
                    board[lastRow - 1][lastCol] = 1;
                    lastRow = lastRow - 1;
                } else if ((lastCol - 1 > -1) && board[lastRow][lastCol - 1] == 0) {
                    board[lastRow][lastCol - 1] = 1;
                    lastCol = lastCol - 1;
                }
                else{
                    int indexes[]=indexGenerator();
                    board[indexes[0]][indexes[1]]=1;
                    lastRow=indexes[0];
                    lastCol=indexes[1];
                }
            }
            else{
                    System.out.println("Have to generate indexes now");
                    int indexes[]=indexGenerator();
                    board[indexes[0]][indexes[1]]=1;
                    lastRow=indexes[0];
                    lastCol=indexes[1];
                System.out.println("lastRow:"+lastRow+" lastCol:"+lastCol);
                }
                moveCounter++;
                uiHandler.post(new Runnable() {
                    public void run() {
                        tv[lastRow][lastCol].setBackgroundResource(R.color.blue1);
                    }
                });
                Message msg = uiHandler.obtainMessage(PLAYER2_MOVE);
                uiHandler.sendMessage(msg);
            }

        void nextMove(){
            Log.i("TMM","Player 1 next move");
            int indexes[] = new int[2];
            indexes=indexGenerator();
            board[indexes[0]][indexes[1]]=1;
            final int row=indexes[0];
            final int col=indexes[1];
            Log.i("TMM","In Player1 row:"+row+" col:"+col);
            // board[2][2] = 2;
            //tv[2][2].setBackgroundResource(R.color.red1);
            moveCounter++;
            uiHandler.post(new Runnable() {
                public void run() {
                    tv[row][col].setBackgroundResource(R.color.blue1);
                }
            });

            Message msg = uiHandler.obtainMessage(PLAYER2_MOVE);
            uiHandler.sendMessage(msg);
        }
        public void tieBreaker(){
            int index[]=new int[2];
            index=returnIndex(1);
            int swapWith[]= new int[2];
            swapWith=indexGenerator();
            int temp=board[index[0]][index[1]];
            board[swapWith[0]][swapWith[1]]=temp;
            board[index[0]][index[1]]=0;
            final int row=index[0];
            final int col=index[1];
            final int srow=swapWith[0];
            final int scol=swapWith[1];
            uiHandler.post(new Runnable() {
                public void run() {
                    tv[srow][scol].setBackgroundResource(R.color.blue1);
                    tv[row][col].setBackgroundResource(R.color.gray1);
                }
            });
            Message msg = uiHandler.obtainMessage(PLAYER2_MOVE);
            uiHandler.sendMessage(msg);
        }

    }

    class Player2 extends Thread {
        //creating handler for the player2 thread.
       public Handler player2Handler= new Handler(){
            public void handleMessage(Message msg){
                int what=msg.what;
                switch(what){

                    case MainAct.NEXT_MOVE:
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("TMM","Player 2 Message Handled!!!");
                        nextMove();
                        break;
                    case MainAct.TIE_BREAKER:
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tieBreaker();
                        break;
                }
            }
        };
        //function to continue after the game has been tied.
        public void tieBreaker(){
            int index[]=new int[2];
            index=returnIndex(2);
            int swapWith[]= new int[2];
            swapWith=indexGenerator();
            int temp=board[index[0]][index[1]];
            board[swapWith[0]][swapWith[1]]=temp;
            board[index[0]][index[1]]=0;
            final int row=index[0];
            final int col=index[1];
            final int srow=swapWith[0];
            final int scol=swapWith[1];
            uiHandler.post(new Runnable() {
                public void run() {
                    tv[srow][scol].setBackgroundResource(R.color.red1);
                    tv[row][col].setBackgroundResource(R.color.gray1);
                }
            });
            Message msg = uiHandler.obtainMessage(PLAYER1_MOVE);
            uiHandler.sendMessage(msg);
        }
        public Handler getHandler(){
            return player2Handler;
        }
        public void run() {
            Looper.prepare();
            Log.i("TMM","Player 2 run");
             Looper.loop();

        }
        //function that generates the next move.
        void nextMove(){
            Log.i("TMM","Player 2 next move");
            int indexes[] = new int[2];
            indexes=indexGenerator();
            board[indexes[0]][indexes[1]]=2;
            final int row=indexes[0];
            final int col=indexes[1];
            Log.i("TMM","In Player2 row:"+row+" col:"+col);
            // board[2][2] = 2;
            //tv[2][2].setBackgroundResource(R.color.red1);
            moveCounter++;
            uiHandler.post(new Runnable() {
                public void run() {
                    tv[row][col].setBackgroundResource(R.color.red1);
                }
            });

            Message msg = uiHandler.obtainMessage(PLAYER1_MOVE);
            uiHandler.sendMessage(msg);
        }
    }


//generates random index that is vacant.
    public int[] indexGenerator(){
        System.out.println("In indexGenerator");
      int indexes[] = new int[2];
        Random r = new Random();
        while(true) {
            indexes[0] = r.nextInt(3);
            indexes[1] = r.nextInt(3);
            if (board[indexes[0]][indexes[1]] == 0) {
                break;
            }
        }
        return indexes;
    }
//returns index of particular player
    int [] returnIndex(int value){
        int index[]=new int[2];
        Random r = new Random();
        outerloop:
        while(true){
            int x=r.nextInt(3);
            int y=r.nextInt(3);
            if(board[x][y]==value){
                index[0]=x;
                index[1]=y;
                break outerloop;
            }
        }


     return index;
    }
   /* int [] bestMove(int val){
        int [][] index=new int[3][2];
        int []i1=new int[0];
        int val1=0;
        int indr=0;
        int indc=0;
                for (int i=0;i<3;i++){
                    for(int j=0;j<3;j++){
                        if(board[i][j]==val){
                            index[indr][indc]=i;
                            index[indr][indc+1]=j;
                        }
                    }
                    indr++;
                    indc=0;
                }
                //if(index[0][0]==index[1][0])//same row
        return i1;
    }*/
}
