package com.example.ekene.blogzone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class CommentActivity extends AppCompatActivity {

    private Button commentButton;
    private RecyclerView comment_list;
    private EditText commentInput;
    private String postID , current_user_id ;

    private DatabaseReference usersRef, databaseRef, commentdatabaseRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        postID = getIntent().getExtras().get("PostID").toString();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Blogzone").child(postID).child("Comments");
        commentdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Blogzone").child(postID).child("Comments").child("comment");
        commentButton = (Button) findViewById(R.id.post_comment_btn);
        commentInput = (EditText) findViewById(R.id.commentInput);

        comment_list = (RecyclerView) findViewById(R.id.comment_recyclerview);
        comment_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comment_list.setLayoutManager(linearLayoutManager);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            String username = dataSnapshot.child("Username").getValue().toString();

                            ValidateComment(username);

                            commentInput.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comments, CommentViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentViewHolder>(
                        Comments.class,
                        R.layout.all_comments_layout,
                        CommentViewHolder.class,
                        commentdatabaseRef) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comments model, int position) {

                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
            }
        };

        comment_list.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CommentViewHolder(View itemView){
            super(itemView);

            mView = itemView;
        }

        public void setUsername(String username) {

            TextView myUsername = (TextView) mView.findViewById(R.id.comment_username);
            myUsername.setText(username);
        }

        public void setComment(String comment){

            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }
    }

    private void ValidateComment(String username) {

        String commentText = commentInput.getText().toString();

        if(TextUtils.isEmpty(commentText)){

            Toast.makeText(this, "please write a comment", Toast.LENGTH_SHORT);
        }else{


            final String RandomKey = current_user_id;
            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", current_user_id);
            commentsMap.put("comment", commentText);
            commentsMap.put("username", username);

            databaseRef.child(RandomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        Toast.makeText(CommentActivity.this, "You sent the comment successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(CommentActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
