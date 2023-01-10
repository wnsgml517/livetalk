package com.liveshare.livetalk;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


@SuppressWarnings("deprecation") //이거 있어야 합니다.

public class LiveActivity extends TabActivity {

    String email="";
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        try{
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser(); //현재 로그인한 email 읽어오기

            if(user.getEmail()!=null) {
                email=user.getEmail();
                Toast.makeText(LiveActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LiveActivity.this, "로그인 안되어있음", Toast.LENGTH_SHORT).show();
                return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        TabHost tabHost = getTabHost(); //탭 호스트 객체 생성

// 탭스팩 선언하고, 탭의 내부 명칭, 탭에 출력될 글 작성
        TabHost.TabSpec spec;
        Intent intent; //객체

//탭에서 액티비티를 사용할 수 있도록 인텐트 생성
        intent = new Intent().setClass(this, SearchActivity.class);
        spec = tabHost.newTabSpec("search"); // 객체를 생성
        spec.setIndicator("검색하기"); //탭의 이름 설정
        spec.setContent(intent);
        tabHost.addTab(spec);


        intent = new Intent().setClass(this, AddActivity.class);
        spec = tabHost.newTabSpec("add"); // 객체를 생성
        spec.setIndicator("제보하기"); //탭의 이름 설정
        spec.setContent(intent);
        tabHost.addTab(spec);


//탭에서 액티비티를 사용할 수 있도록 인텐트 생성

        intent = new Intent().setClass(this, MainActivity.class);
        spec = tabHost.newTabSpec("chat"); // 객체를 생성
        spec.setIndicator("채팅방 입장"); //탭의 이름 설정
        spec.setContent(intent);
        tabHost.addTab(spec);

        if(email.equals("")) // 로그인 안되어있을 때
            intent = new Intent().setClass(this, LoginActivity.class); // 로그인 화면으로 이동
        else // 로그인 되어있을 때
            intent = new Intent().setClass(this, MypageActivity.class); // 마이페이지로 이동
        spec = tabHost.newTabSpec("mypage"); // 객체를 생성
        spec.setIndicator("마이페이지"); //탭의 이름 설정
        spec.setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0); //먼저 열릴 탭을 선택! (2)로 해두면 그룹이 시작 화면!


    }

}