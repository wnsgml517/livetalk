package com.liveshare.livetalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class MainPage extends AppCompatActivity {
    private Button btn_move;
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증 관련
    private DatabaseReference mDatabaseReference; // 데이터베이스 관련
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
            mFirebaseAuth =FirebaseAuth.getInstance();


        btn_move=findViewById(R.id.btn_move);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }
}