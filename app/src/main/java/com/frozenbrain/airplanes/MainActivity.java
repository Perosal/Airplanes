package com.frozenbrain.airplanes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /* getResources().getColor(R.color.yellow) is deprecated*/

    private int AIRPLANES_NUMBER = 3;
    public boolean GAME_START = false;
    public boolean USER_FINISHED = false;
    private int [][] GAME_TABLE = new int[11][11];
    private int [][] GAME_TABLE_OPPONENT = new int[11][11];
    private int DOTS_NUMBER = 0;
    private List<Integer> colorList = new ArrayList<>();

    public String resolution;
    public int width , height;
    public String userId , username , opponentUsername , opponentId ;
    /*Bye java language, welcome kotlin 10.1.2019*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // region Predefined Things
        /*Spinner*/
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        /*PUT THE COLORS INTO LIST*/
        colorList.add(R.color.blue);
        colorList.add(R.color.yellow);
        colorList.add(R.color.orange);

        /*Get Display Resolution*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        resolution = width + "x" + height;

        // endregion

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                reset(null);
                /*Implement For Both Users connected*/
                if(text.equals("Default")&&AIRPLANES_NUMBER!=3){
                    AIRPLANES_NUMBER = 3;
                    Toast.makeText(MainActivity.this, "You Choose 3 Airplanes", Toast.LENGTH_SHORT).show();
                }else if(text.equals("One")){
                    AIRPLANES_NUMBER = 1;
                    Toast.makeText(MainActivity.this, "You Choose 1 Airplane", Toast.LENGTH_SHORT).show();
                }else if(text.equals("Two")){
                    AIRPLANES_NUMBER = 2;
                    Toast.makeText(MainActivity.this, "You Choose 2 Airplanes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        userId = FirebaseAuth.getInstance().getUid().toString();
        /*TAKES THE ID OF CONNECTED USER*/
        FirebaseDatabase.getInstance().getReference("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                opponentId = dataSnapshot.child("connectedUser").getValue().toString();

                /*TAKES THE DATA SEND BY INTENT*/
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                opponentUsername = bundle.getString("opponentUsername");
                ((TextView)findViewById(R.id.players)).setText(username + " VS " + opponentUsername);


                /*LISTEN FOR OPPONENT MOVE, I PUT THIS HERE BECAUSE BELOW NEEDS TO EXECUTE FIRST*/
                FirebaseDatabase.getInstance().getReference("users").child(opponentId).child("move").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String move = dataSnapshot.getValue().toString();
                        /*RESET THE GAME IF OPPONENT PRESS RESET AND VICE VERSA*/
                        if (move.equals("restart")){
                            reset(null);
                        }
                        else if(move.length()>0&&GAME_START) {
                            int i = Integer.parseInt(move);

                            GridLayout gridLayout = findViewById(R.id.game_table);
                            CardView cardView = (CardView) gridLayout.getChildAt(i);
                            Glide.with(MainActivity.this).load(R.drawable.x).into(((ImageView)cardView.getChildAt(0)));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reset(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.how_to_play) {
            startActivityForResult(new Intent(this, HowToPlay.class) , 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void userTable(View view){
        CardView cardView = (CardView)view;
        /*Get the index of child/view*/
        int i = ((ViewGroup) view.getParent()).indexOfChild(view);
        if(GAME_START&&!USER_FINISHED&&GAME_TABLE[i/11][i%11]!=1&&DOTS_NUMBER<AIRPLANES_NUMBER*8) {
            cardView.setCardBackgroundColor(getResources().getColor(colorList.get(DOTS_NUMBER/8)));
            GAME_TABLE[i/11][i%11] = 1;
            ++DOTS_NUMBER;
        } else if (!GAME_START)
            Toast.makeText(this, "Start the game first", Toast.LENGTH_SHORT).show();
        else if(DOTS_NUMBER==AIRPLANES_NUMBER*8)
            USER_FINISHED = true;

    }

    public void MakeAMove(View view) {
        // Convert the 'view' into a cardView
        CardView cardView = (CardView)view;
        if(GAME_START&&USER_FINISHED) {

            // Get the first child view aka ImageView
            ImageView imageView = (ImageView) cardView.getChildAt(0);
            /*Get the index of child/view*/
            int i = ((ViewGroup) view.getParent()).indexOfChild(view);
            if(GAME_TABLE_OPPONENT[i/11][i%11]!=1) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                Glide.with(this).load(R.drawable.plus).into(imageView);
                FirebaseDatabase.getInstance().getReference("users").child(userId).child("move").setValue("" + i);
                GAME_TABLE_OPPONENT[i/11][i%11]=1;
            } else {
                imageView.setImageResource(android.R.color.transparent);
                cardView.setCardBackgroundColor(getResources().getColor(R.color.indigo));
                GAME_TABLE_OPPONENT[i/11][i%11]=0;
            }



        } else if (!GAME_START)
            Toast.makeText(this, "Start the game first", Toast.LENGTH_SHORT).show();
        else if (!USER_FINISHED)
            Toast.makeText(this, "First put the airplanes", Toast.LENGTH_SHORT).show();

    }

    public void start(View view){
        GAME_START = true;
        reset(null);
    }
    public void reset(View view){
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("move").setValue("restart");
        GridLayout userTable = findViewById(R.id.game_table);
        GridLayout opponentTable = findViewById(R.id.enemy_table);
        for(int i = 12; i < 121 ;++i){
            if(i%11!=0) {
                ((CardView) userTable.getChildAt(i)).setCardBackgroundColor(getResources().getColor(R.color.white));
                ((CardView) opponentTable.getChildAt(i)).setCardBackgroundColor(getResources().getColor(R.color.white));
                ((ImageView)((CardView) userTable.getChildAt(i)).getChildAt(0)).setImageResource(android.R.color.transparent);
                ((ImageView)((CardView) opponentTable.getChildAt(i)).getChildAt(0)).setImageResource(android.R.color.transparent);
                GAME_TABLE[i/11][i%11] = 0;
                GAME_TABLE_OPPONENT[i/11][i%11] = 0;

            }
        }
        USER_FINISHED = false;
        DOTS_NUMBER = 0;
    }

}
