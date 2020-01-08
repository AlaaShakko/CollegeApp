package com.example.ekene.blogzone;

import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoActivity extends AppCompatActivity {
    TextView num;
    TextView name;
    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        num = (TextView) findViewById(R.id.num);
        name = (TextView) findViewById(R.id.name);
        date = (TextView) findViewById(R.id.date);

        FirebaseAuth Maut = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = Maut.getCurrentUser();

        DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String a = dataSnapshot.child("EVENT").getValue().toString();
                String ab = dataSnapshot.child("Username").getValue().toString();
                String abb = dataSnapshot.child("date").getValue().toString();

                num.setText(a);
                name.setText(ab);
                date.setText(abb);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.info) {
            startActivity(new Intent(InfoActivity.this, InfoActivity.class));
        } else if (id == R.id.action_add) {
            startActivity(new Intent(InfoActivity.this, PostActivity.class));
        } else if (id == R.id.logout) {

            Intent logouIntent = new Intent(InfoActivity.this, RegisterActivity.class);
            logouIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logouIntent);
        }


        return super.onOptionsItemSelected(item);
    }
}
