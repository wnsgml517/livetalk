package com.liveshare.livetalk;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    private Button button;
    private TextView textView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> room = new ArrayList<>();

    private String chat_room_name, chat_user_name;

    private DatabaseReference reference;
    private String key;
    private String chat_user;
    private String chat_message;
    private String chat_nickname;
    private long chat_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        textView = (TextView)findViewById(R.id.text);
        listView = (ListView) findViewById(R.id.list);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        chat_user_name = getIntent().getExtras().get("chat_user_name").toString();
        chat_room_name = getIntent().getExtras().get("chat_room_name").toString();
        System.out.println("닉네임: "+chat_nickname);

        textView.setText(chat_room_name + " 채팅방");

        reference = FirebaseDatabase.getInstance().getReference().child(chat_room_name);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.memolist_type, room);
        listView.setAdapter(arrayAdapter);

        button.setOnClickListener(new View.OnClickListener() { // 채팅 입력
            @Override public void onClick(View view) {

                Map<String, Object> map = new HashMap<String, Object>();
                key = reference.push().getKey();

                reference.updateChildren(map);

                long mNow = System.currentTimeMillis();
                Date mReDate = new Date(mNow);
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formatDate = mFormat.format(mReDate); // 현재 시간 읽어오기

                DatabaseReference root = reference.child(key);

                Map<String, Object> objectMap = new HashMap<String, Object>();

                objectMap.put("name", chat_user_name); //이름
                objectMap.put("message", editText.getText().toString()); //메세지
                objectMap.put("time", mNow); // 시간
                root.updateChildren(objectMap);

                editText.setText("");
            }
        });

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
    private static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg; // 댓글 단 시간 확인
    }
    private void chatConversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            chat_message = (String) ((DataSnapshot) i.next()).getValue();
            chat_user = (String) ((DataSnapshot) i.next()).getValue();
            chat_time = (Long) ((DataSnapshot) i.next()).getValue();
            arrayAdapter.add(" " + chat_user + " : " + chat_message+" - " +formatTimeString(chat_time));
        }

        arrayAdapter.notifyDataSetChanged();
    }
}
