package com.liveshare.livetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class activity_login extends AppCompatActivity {

    private Button btn_register_1;

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증 관련
    private EditText et_email, et_pwd;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_register_1 = findViewById(R.id.btn_register_1);
        btn_register_1.setOnClickListener(v -> {
            Intent intent = new Intent(activity_login.this, activity_signup.class);
            startActivity(intent);
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> {
            String strEmail = et_email.getText().toString();
            String strPwd = et_pwd.getText().toString();

            mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(activity_login.this, task -> {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(activity_login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(activity_login.this, strPwd+strEmail+"로그인 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}