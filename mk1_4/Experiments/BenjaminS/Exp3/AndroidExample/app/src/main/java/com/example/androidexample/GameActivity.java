package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    int activeplayer = 0;   //0: yellow, 1: red
    boolean gameactive = true;  //for the winning condition
    int[] gamestate = {2,2,2,2,2,2,2,2,2};   // we have 9 state 0,1,2,3,4,5,6,7,8
    int[][] winningstates = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    private Button backButton;
    public void tapped(View view)
    {

        ImageView counts = (ImageView) view;
        //wierd saftey check
        int tapat = Integer.parseInt(counts.getTag().toString());

        if(gameactive && gamestate[tapat] == 2){

            counts.setTranslationY(-1500); //creates the animation of the y axis
            gamestate[tapat]=activeplayer;
            //initializes the 2 players and their colors
            if(activeplayer==0)
            {
                counts.setImageResource(R.drawable.yellow);
                activeplayer=1;
            }
            else
            {
                counts.setImageResource(R.drawable.red);
                activeplayer=0;
            }
            counts.animate().translationYBy(1500).rotation(3600).setDuration(300);
            for(int[] winningstate : winningstates)
            {
                // trackes the game states of each color and sees if they have matched one of the winning gamestates
                if(gamestate[winningstate[0]]==gamestate[winningstate[1]] && gamestate[winningstate[1]]==gamestate[winningstate[2]] && gamestate[winningstate[1]]!=2)
                {
                    // program sees who is the winner based on who's turn it is
                    String winner="";
                    if(activeplayer == 1)
                    {
                        winner = "yellow ";
                    }
                    else
                    {
                        winner ="red ";
                    }
                    gameactive = false;

                    //when user has won, highlight the text view
                    TextView winnertext = (TextView) findViewById(R.id.winner);
                    winnertext.setText(winner + "has won");
                    winnertext.setVisibility(View.VISIBLE);
                    Button playag = (Button) findViewById(R.id.playagainbutton);
                    playag.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    public  void playagain(View view)
    {
        TextView winnertext = (TextView) findViewById(R.id.winner);
        winnertext.setVisibility(View.INVISIBLE);
        Button playag = (Button) findViewById(R.id.playagainbutton);
        playag.setVisibility(View.INVISIBLE);
        GridLayout grd = (GridLayout) findViewById(R.id.gridLayout);
        for(int i=0;i<grd.getChildCount();i++)  //erase the imageView from grid viewngetChildCount() will return the count of childs
        {
            ImageView counts = (ImageView) grd.getChildAt(i);
            counts.setImageDrawable(null);  // removes all the Homers and Barts
        }
        // update the game state again
        activeplayer = 0;
        gameactive = true;  //for the winning condition
        for(int i=0;i<gamestate.length;i++)
        {
            gamestate[i]=2;
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        backButton = findViewById(R.id.returnToHomeBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


}
