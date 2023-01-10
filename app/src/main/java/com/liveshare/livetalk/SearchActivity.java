package com.liveshare.livetalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class SearchActivity extends AppCompatActivity {
    //로그캣 사용 설정
    private static final String TAG = "AddActivity";

    //지도 프래그먼트 설정
    //객체 선언
    SupportMapFragment mapFragment;
    GoogleMap map;
    Button btnLocation, btnKor2Loc;
    EditText editText;
    Geocoder geocoder;
    MarkerOptions myMarker=null, mOptions=null;
    List<Address> adder=null;
    String search=null;
    View marker_root_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //권한 설정
        checkDangerousPermissions();

        //객체 초기화
        btnLocation = findViewById(R.id.button1);
        editText = findViewById(R.id.editText);
        btnKor2Loc = findViewById(R.id.button2);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.memolist_type2, null);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady: ");
                map = googleMap;
                map.setMyLocationEnabled(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.537523, 126.96558), 14));
                map.setOnMapClickListener(point -> {
                    mOptions = new MarkerOptions();
                    // 마커 타이틀

                    mOptions.title("마커 좌표");

                    Double latitude = point.latitude; // 위도
                    Double longitude = point.longitude; // 경도
                    LatLng curPoint = new LatLng(latitude, longitude);
                    // 마커의 스니펫(간단한 텍스트) 설정
                    mOptions.title(getCurrentAddress(latitude,longitude));
                    mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                    // LatLng: 위도 경도 쌍을 나타냄
                    mOptions.position(new LatLng(latitude, longitude));

                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoContents(Marker marker) {
                            LinearLayout info = new LinearLayout(getApplicationContext());
                            info.setOrientation(LinearLayout.VERTICAL);
                            info.setGravity(Gravity.CENTER);
                            info.setPadding(3, 3, 3, 3);

                            info.setLayoutParams(new LinearLayout.LayoutParams(600, 400));
                            TextView title = new TextView(getApplicationContext());
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setText(mOptions.getTitle());
                            title.setTextSize(15);
                            Typeface typeFace = ResourcesCompat.getFont(getApplicationContext(), R.font.nanumd);
                            title.setTypeface(typeFace);
                            TextView snippet = new TextView(getApplicationContext());
                            snippet.setTextColor(Color.GRAY);
                            snippet.setGravity(Gravity.LEFT);
                            snippet.setTypeface(typeFace);
                            snippet.setText(mOptions.getSnippet());
                            LinearLayout.LayoutParams btnparams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            Button addbtn = new Button(getApplicationContext());
                            addbtn.setTextColor(Color.WHITE);
                            addbtn.setGravity(Gravity.CENTER);
                            addbtn.setBackgroundColor(Color.GRAY);
                            addbtn.setPadding(10,4,10,4);
                            addbtn.setTextSize(16);
                            addbtn.setText("장소 추가 요청");
                            addbtn.setOnClickListener(new Button.OnClickListener() { // 버튼 클릭 시 채팅방 화면 ChatActivity로 화면 이동
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(SearchActivity.this, "장소 추가 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            info.addView(title);
                            info.addView(snippet);
                            info.addView(addbtn,btnparams1);
                            return info;
                        }
                        @Nullable
                        @Override
                        public View getInfoWindow(@NonNull Marker marker) {
                            return null;
                        }
                    });
                    // 마커(핀) 추가
                    googleMap.addMarker(mOptions);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
                });

            }
        });
        MapsInitializer.initialize(this);

        //위치 확인 버튼 기능 추가
        btnLocation.setOnClickListener(view -> requestMyLocation());

        btnKor2Loc.setOnClickListener(view -> {
            if(editText.getText().toString().length() > 0) {
                Location location = getLocationFromAddress(getApplicationContext(), editText.getText().toString());

                showCurrentLocation(location);
            }
        });
    }
    private void requestMyLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            long minTime = 1000;    //갱신 시간
            float minDistance = 0;  //갱신에 필요한 최소 거리

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    showCurrentLocation(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }
    private Location getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        Location resLocation = new Location("");
        try {
            addresses = geocoder.getFromLocationName(address, 5);
            if((addresses == null) || (addresses.size() == 0)) {
                return null;
            }
            Address addressLoc = addresses.get(0);

            resLocation.setLatitude(addressLoc.getLatitude());
            resLocation.setLongitude(addressLoc.getLongitude());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resLocation;
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
        if(curPoint==null){
            Toast.makeText(this,"존재하지 않습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        String msg = "Latitutde : " + curPoint.latitude
                + "\nLongitude : " + curPoint.longitude;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        //화면 확대, 숫자가 클수록 확대


        //마커 찍기
        Location targetLocation = new Location("");
        targetLocation.setLatitude(curPoint.latitude);
        targetLocation.setLongitude(curPoint.longitude);
        showMyMarker(targetLocation);
        map.setOnInfoWindowClickListener(infoWindowClickListener);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
    }
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = marker -> {

        if(mOptions!=null){
            Toast.makeText(SearchActivity.this, "장소 추가 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            mOptions=null;
        }
        else
        {
            String markerId = marker.getId();
            Toast.makeText(SearchActivity.this, "정보창 클릭 Marker ID : " + markerId, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
            intent.putExtra("chat_room_name", editText.getText().toString());
            intent.putExtra("chat_user_name", "익명");
            startActivity(intent);
        }
    };
    //------------------권한 설정 시작------------------------
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    //------------------권한 설정 끝------------------------
    public static Bitmap resizeMarker(Context context){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("pin", "drawable", context.getPackageName()));
        return Bitmap.createScaledBitmap(bitmap, 200, 200, false);
    }
    private void showMyMarker(Location location) { // 마커 클릭 시 표시될 내용

        geocoder = new Geocoder(this);
        try {

            adder = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(adder.get(0).getAddressLine(0));
        if(myMarker == null) {
            myMarker = new MarkerOptions();
            myMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            myMarker.icon(BitmapDescriptorFactory.defaultMarker(200f));
            myMarker.title("◎ "+editText.getText().toString()+" 채팅방 \n");
            myMarker.snippet(adder.get(0).getAddressLine(0));
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoContents(Marker marker) {
                    LinearLayout info = new LinearLayout(getApplicationContext());
                    info.setLayoutParams(new LinearLayout.LayoutParams(600, 400));
                    info.setOrientation(LinearLayout.VERTICAL);
                    info.setPadding(3,3,3,3);
                    info.setGravity(Gravity.CENTER);
                    TextView title = new TextView(getApplicationContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setText(marker.getTitle());
                    title.setTextSize(15);

                    Typeface typeFace = ResourcesCompat.getFont(getApplicationContext(), R.font.nanumd);
                    title.setTypeface(typeFace);
                    TextView snippet = new TextView(getApplicationContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setGravity(Gravity.CENTER);
                    snippet.setTypeface(typeFace);
                    snippet.setText(marker.getSnippet());

                    Button addbtn = new Button(getApplicationContext());
                    addbtn.setTextColor(Color.WHITE);
                    addbtn.setGravity(Gravity.CENTER);
                    addbtn.setBackgroundColor(Color.GRAY);
                    addbtn.setPadding(10,4,10,4);
                    addbtn.setTextSize(16);
                    LinearLayout.LayoutParams btnparams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    addbtn.setText("채팅방 입장");
                    addbtn.setOnClickListener(new Button.OnClickListener() { // 버튼 클릭 시 채팅방 화면 ChatActivity로 화면 이동
                        @Override
                        public void onClick(View view) {
                            String markerId = myMarker.getTitle();
                            Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                            intent.putExtra("chat_room_name", editText.getText().toString());
                            intent.putExtra("chat_user_name", "익명");
                            startActivity(intent);
                        }
                    }) ;
                    info.addView(title);
                    info.addView(snippet);
                    info.addView(addbtn,btnparams1);

                    return info;
                }

                @Nullable
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    return null;
                }
            }); // 마커 커스텀 윈도우 레이아웃 작성

            map.addMarker(myMarker);
        }
    }
}