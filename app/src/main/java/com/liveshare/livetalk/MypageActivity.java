package com.liveshare.livetalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ValueEventListener;

public class MypageActivity extends AppCompatActivity {

    private DatabaseReference reference = FirebaseDatabase.getInstance()
            .getReference().getRoot();

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> room = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private String user_name,user_id,user_pw;
    private String email="";
    private TextView tname;
    private TextView temail;
    private Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        logoutBtn=(Button)findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MypageActivity.this, "로그아웃 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MypageActivity.this, LiveActivity.class);
                startActivity(intent);
            }
        });

        try{
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser(); //현재 로그인한 email 읽어오기

            if(user.getEmail()!=null) {
                email=user.getEmail();
                Toast.makeText(MypageActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MypageActivity.this, "로그인 안되어있음", Toast.LENGTH_SHORT).show();
                return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatConversation(dataSnapshot);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatConversation(dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void chatConversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {

            user_id = (String) ((DataSnapshot) i.next()).getValue();
            user_name = (String) ((DataSnapshot) i.next()).getValue();
            user_pw = (String) ((DataSnapshot) i.next()).getValue();
            if(user_id.equals(email)) {
                // id 매핑
                tname=(TextView)findViewById(R.id.et_nickname);

                // id 매핑
                temail= (TextView)findViewById(R.id.et_email);


                // 텍스트 set
                tname.setText(user_name+"님 반갑습니다 ! :)");

                // 텍스트 set
                temail.setText(user_id);
                System.out.println(user_name + " : " + user_id + " - " + user_pw);
                break;
            }
            else
                System.out.println("회원정보없음");
        }
    }
}