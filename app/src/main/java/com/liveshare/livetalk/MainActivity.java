package com.liveshare.livetalk;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private EditText editText;
    private Button button;
    private Button btn_name;
    private FirebaseAuth firebaseAuth;
    private String user_name,user_id,user_pw;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> room = new ArrayList<>();
    private DatabaseReference reference = FirebaseDatabase.getInstance()
            .getReference().getRoot();
    private DatabaseReference referenceN = FirebaseDatabase.getInstance()
            .getReference().getRoot();
    private String name="익명";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        btn_name = (Button) findViewById(R.id.btn_name);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.memolist_type2, room);
        listView.setAdapter(arrayAdapter);

        try{
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser(); //현재 로그인한 email 읽어오기

            if(user.getEmail()!=null) {
                Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                name = user.getEmail();
            }
            else {
                Toast.makeText(MainActivity.this, "로그인 안되어있음", Toast.LENGTH_SHORT).show();
                return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }


        referenceN = FirebaseDatabase.getInstance().getReference().child("Users");
        referenceN.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatName(dataSnapshot);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatName(dataSnapshot);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();

                map.put(editText.getText().toString(), "");
                reference.updateChildren(map);
            }
        });

        btn_name.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                createUserName();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }

                room.clear();
                room.addAll(set);
                room.remove("Users");
                arrayAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("chat_room_name", ((TextView) view).getText().toString());
            intent.putExtra("chat_user_name", name);
            startActivity(intent);
        });
    }


    private void createUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("채팅방에 사용할 이름을 입력하세요");

        final EditText builder_input = new EditText(this);

        builder.setView(builder_input);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                name = builder_input.getText().toString();
                Toast.makeText(MainActivity.this, "닉네임 생성 완료" , Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                // 취소를 누르면 이름을 입력할 때 까지 요청
                createUserName();
            }
        });

        builder.show();
    }
    private void chatName(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {

            user_id = (String) ((DataSnapshot) i.next()).getValue();
            user_name = (String) ((DataSnapshot) i.next()).getValue();
            user_pw = (String) ((DataSnapshot) i.next()).getValue();
            if(user_id.equals(name)) {
                name=user_name; // 회원가입 시 설정했던 닉네임으로 바꾸기.
                break;
            }
            else
                System.out.println("회원정보없음");
        }
    }
}