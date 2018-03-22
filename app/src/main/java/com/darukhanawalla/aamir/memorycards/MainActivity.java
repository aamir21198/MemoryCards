package com.darukhanawalla.aamir.memorycards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView card1, card2;
    TextView user_score, comp_score;
    //HashMap<String, Integer> images;
    HashSet<String> available;
    HashMap<String, Drawable> board;
    int cardsPicked;
    int userScore, compScore;
    private static final int MAX_ENTRIES = 4;
    LinkedHashMap<Drawable, ArrayList<String>> brain;
    ArrayList<Drawable> tempBrain;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        comp_score = findViewById(R.id.comp_score);
        user_score = findViewById(R.id.user_score);

        linearLayout = findViewById(R.id.linear_layout);

         available = new HashSet<>();
         for(int i=0; i<16; i++)
             available.add(Integer.toString(i));

         board = new HashMap<>();
         initializeBoard(board);

         cardsPicked = 0;

         userScore = 0;
         compScore = 0;

         brain = new LinkedHashMap<Drawable, ArrayList<String>>(){
             protected boolean removeEldestEntry(Map.Entry eldest) {
                 return size() > MAX_ENTRIES;
             }
         };
         tempBrain = new ArrayList<>();

        Toast.makeText(this, "Game Starts", Toast.LENGTH_SHORT).show();
    }

    private void initializeBoard(HashMap<String, Drawable> board) {
        int[] photos = {R.drawable.angry,R.drawable.arsenal,R.drawable.bat,R.drawable.cat,R.drawable.dog,R.drawable.flower,R.drawable.lol,R.drawable.orange};
        //List<Integer>
        ArrayList<String> temp = new ArrayList<>();
        for(int i=0; i<16; i++)
            temp.add(Integer.toString(i));
        Collections.shuffle(temp);
        for(int i=0; i<8; i++) {
            Drawable x = getDrawable(photos[i]);
            board.put(temp.get(i), x);
            board.put(temp.get(i+8), x);
        }
    }

    public void turn(View view) {

        if (cardsPicked == 0) {
            card1 = (ImageView) view;
            card1.setImageDrawable(board.get(card1.getTag().toString()));
            cardsPicked++;
            card1.setEnabled(false);

        } else if (cardsPicked == 1) {
            card2 = (ImageView) view;
            enableAll(false);
            if (board.get(card1.getTag().toString()) == board.get(card2.getTag().toString())) {
                card2.setImageDrawable(board.get(card2.getTag().toString()));
                userScore++;
                user_score.setText("Your Score: " + userScore);
                card1.setAlpha(0.5f);
                card2.setAlpha(0.5f);
                removeCard(card1);
                removeCard(card2);
                brain.remove(board.get(card1.getTag().toString()));
                tempBrain.remove(board.get(card1.getTag().toString()));
                Toast.makeText(this, "+1", Toast.LENGTH_SHORT).show();
            } else {
                addCardToBrain(card1);
                addCardToBrain(card2);
                flip(card1);
                flip(card2);
            }
            cardsPicked = 0;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();
                }

            }, 2011);
        }
    }

    private void computerTurn() {
        if(!tempBrain.isEmpty())
        {
            checkTemp();
        }
        else
        {
            Random random = new Random();
            if (available.size() == 0) {
                gameOver();
                return;
            }
            int r = random.nextInt(available.size());
            String chosen = "", chosen2 = "";
            for (String x: available)
            {
                if (r == 0) {
                    chosen = x;
                    break;
                }
                r--;
            }
            card1 = (ImageView) linearLayout.findViewWithTag(chosen);
            addCardToBrain(card1);
            if(!tempBrain.isEmpty())
            {
                checkTemp();
            }
            else {
                available.remove(chosen);
                int y = random.nextInt(available.size());
                for (String x: available)
                {
                    if (y == 0) {
                        chosen2 = x;
                        break;
                    }
                    y--;
                }
                available.add(chosen);
                card2 = (ImageView) linearLayout.findViewWithTag(chosen2);
                addCardToBrain(card2);
                if(!tempBrain.isEmpty())
                    checkTemp();
                else {
                    flip(card1);
                    flip(card2);
                }
            }
        }
        if (available.size() == 0) {
            gameOver();
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enableAll(true);
            }
        }, 2011);
    }

    private void checkTemp() {
        Drawable d = tempBrain.remove(0);
        String pos1 = brain.get(d).get(0);
        String pos2 = brain.get(d).get(1);
        brain.remove(d);
        available.remove(pos1);
        available.remove(pos2);
        ImageView temp1 = (ImageView) linearLayout.findViewWithTag(pos1);
        ImageView temp2 = (ImageView) linearLayout.findViewWithTag(pos2);
        temp1.setImageDrawable(d);
        temp2.setImageDrawable(d);
        temp1.setAlpha(0.5f);
        temp2.setAlpha(0.5f);
        incrementCompScore();
    }

    private void gameOver() {
        if(userScore > compScore)
            Toast.makeText(this, "You Win!!", Toast.LENGTH_SHORT).show();
        else if(userScore < compScore)
            Toast.makeText(this, "Computer Wins", Toast.LENGTH_SHORT).show();
        else if(userScore == compScore)
            Toast.makeText(this, "Draw", Toast.LENGTH_SHORT).show();
        return;
    }

    private void incrementCompScore() {
        Toast.makeText(this, "Computer: +1", Toast.LENGTH_SHORT).show();
        compScore++;
        comp_score.setText("Computer Score: " + compScore);
    }

    private void enableAll(boolean enabled) {
        int j= 0;
        for(String i : available)
        {
            ImageView temp = (ImageView) linearLayout.findViewWithTag(i);
            temp.setEnabled(enabled);
        }
    }

    public void flip(final ImageView img) {
        //int resId = getResources().getIdentifier("ic_launcher_background", "drawable", getPackageName());
        img.setImageDrawable(board.get(img.getTag().toString()));
        Handler handler = new Handler();
        img.setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int r = getResources().getIdentifier("back", "drawable", getPackageName());
                img.setImageResource(r);
                //img.setEnabled(true);
            }
        }, 2000);
    }

    private void removeCard(ImageView card)
    {
        available.remove(card.getTag().toString());
        card.setEnabled(false);

    }

    private void addCardToBrain(ImageView card)
    {
        if(brain.containsKey(board.get(card.getTag().toString())))
        {
            ArrayList<String> temp = brain.get(board.get(card.getTag().toString()));
            if (temp.contains(card.getTag().toString()))
                return;
            temp.add(card.getTag().toString());
            tempBrain.add(board.get(card.getTag().toString()));
        }
        else
        {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(card.getTag().toString());
            brain.put(board.get(card.getTag().toString()), temp);
        }
    }

    public void reset(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
