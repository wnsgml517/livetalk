package com.liveshare.livetalk;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liveshare.livetalk.databinding.ActivitySignupBinding;

public class activity_signup extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증 관련
    private DatabaseReference mDatabaseReference; // 데이터베이스 관련
    private EditText et_email, et_pwd, et_nickname;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("FirebaseEmailAccount");

        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        et_nickname =findViewById(R.id.et_nickname);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(v -> {
            String strEmail = et_email.getText().toString();
            String strPwd = et_pwd.getText().toString();
            String strName = et_nickname.getText().toString();
            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(activity_signup.this, task -> { //유저가 다 만들어졌을 때
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser =mFirebaseAuth.getCurrentUser(); //로그인을 성공해서 가능한 것
                    UserAccount account = new UserAccount();
                    account.setUserName(strName);
                    account.setEmail(firebaseUser.getEmail());
                    account.setPassword(strPwd);
                    account.setIdToken(firebaseUser.getUid());
                    //database에 저장
                    mDatabaseReference.child("userAccount").child(firebaseUser.getUid()).setValue(account);

                    Toast.makeText(activity_signup.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity_signup.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}