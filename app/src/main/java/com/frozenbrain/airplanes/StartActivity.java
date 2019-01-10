package com.frozenbrain.airplanes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frozenbrain.airplanes.Model.GenerateUsername;
import com.frozenbrain.airplanes.Model.User;
import com.frozenbrain.airplanes.Model.UserId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    TextView connectedUser;
    TextView opponentUser;
    String opponent , user;
    private boolean IS_CONNECTED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViewById(R.id.buttonEnter).setOnClickListener(this);
        findViewById(R.id.buttonConnect).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);
        ((EditText)findViewById(R.id.username)).setText(new GenerateUsername().getUsername());
        connectedUser = findViewById(R.id.connectedUser);
        opponentUser = findViewById(R.id.opponentUser);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseUser!=null) {
            FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    connectedUser.setText("Connected as : " + dataSnapshot.getValue().toString());
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            /*TAKE THE USERNAME OF THE CURRENT USER*/
                            user = dataSnapshot.getValue().toString();
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

        }


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonEnter){
            FirebaseAuth.getInstance().signOut();
            firebaseAuth = FirebaseAuth.getInstance();
            final String username = ((EditText)findViewById(R.id.username)).getText().toString();
            /*TAKES THE USERNAME OF THE NEW USER*/
            user = username;

            if (username.length()>=4){

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userId");
                /*VERIFY IF USER EXISTS*/
                reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){

                            firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        /*ADD DATA INTO DATABASE AFTER USER IS CREATED*/
                                        firebaseUser = firebaseAuth.getCurrentUser();
                                        FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).setValue(new User(username , "" , ""));
                                        FirebaseDatabase.getInstance().getReference("userId").child(username).setValue(new UserId(firebaseUser.getUid()));
                                        connectedUser.setText("Connected as : " + username);
                                        Toast.makeText(StartActivity.this, "User : " + username + " created", Toast.LENGTH_SHORT).show();

                                    } else
                                        Toast.makeText(StartActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else
                            Toast.makeText(StartActivity.this, "Username Already Taken", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            } else
                Toast.makeText(this, "Enter At Least 4 Letters", Toast.LENGTH_SHORT).show();


        } else if (i == R.id.buttonConnect){
            final String opponentUsername = ((EditText)findViewById(R.id.opponentUsername)).getText().toString();
            opponent = opponentUsername;
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userId");
            if (opponentUsername.length()>=4) {
                reference.child(opponentUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String oppId = dataSnapshot.child("id").getValue().toString();
                            Toast.makeText(StartActivity.this, "Connected with : " + opponentUsername , Toast.LENGTH_SHORT).show();
                            opponentUser.setText(opponentUsername);
                            IS_CONNECTED = true;
                            FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("connectedUser").setValue(oppId);
                        } else
                            Toast.makeText(StartActivity.this, "User Does't Exists", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "Enter At Least 4 Digits", Toast.LENGTH_SHORT).show();
            }


        } else if (i == R.id.start){
            if(firebaseUser!=null&&IS_CONNECTED){
                startActivity(new Intent(StartActivity.this , MainActivity.class).putExtra("username", user).putExtra("opponentUsername", opponent));
            }else
                Toast.makeText(this, "Complete Both Fields", Toast.LENGTH_SHORT).show();
        }


    }
}
