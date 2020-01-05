package com.example.campusnavigation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.campusnavigation.bean.Spot;
import com.example.campusnavigation.utils.RequestWithOkhttp;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mapView;
    private AMap aMap;
    private Context context = this;
    private String data;
    private ArrayList<Spot> spotsInfo = new ArrayList<>();          //请求来的景点信息
    private ArrayList<ArrayList<LatLng>> allPathList = new ArrayList<>();
    private ArrayList<LatLng> minPathList = new ArrayList<>();
    private ArrayList<MarkerOptions> spots = new ArrayList<>();     //其中储存的marker信息
    private DrawerLayout drawerLayout;
    private ArrayList<Marker> markerKeeper = new ArrayList<>();
    private ArrayList<Polyline> linesKeeper = new ArrayList<>();
    private ArrayList<Integer> colorlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0x123);

        //初始化所有控件
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        drawerLayout = findViewById(R.id.dl_functions);
        //主页面功能按钮
        Button fbotton = findViewById(R.id.btn_funs);
        fbotton.setOnClickListener(this);
        LinearLayout searchAllP = findViewById(R.id.ll_searchAllPath);
        searchAllP.setOnClickListener(this);
        LinearLayout searchMinP = findViewById(R.id.ll_searchMinPath);
        searchMinP.setOnClickListener(this);
        LinearLayout addPath = findViewById(R.id.ll_addPath);
        addPath.setOnClickListener(this);
        LinearLayout query = findViewById(R.id.ll_query);
        query.setOnClickListener(this);
        ToggleButton toggleButton = findViewById(R.id.tb);
        init();

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            } else {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        });

        /**
         * 下面两个方法监听地图的缩放事件，并对marker进行显示或隐藏
         */
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.zoom <= 16.0) {
                    for (int i = 0; i < spots.size(); i++) {
                        markerKeeper.get(i).setVisible(false);
                    }
                    for (Polyline line : linesKeeper) {
                        line.setVisible(false);
                    }
                } else {
                    for (int i = 0; i < spots.size(); i++) {
                        markerKeeper.get(i).setVisible(true);
                    }
                    for (Polyline line : linesKeeper) {
                        line.setVisible(true);
                    }
                }
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (cameraPosition.zoom <= 16.0) {
                    for (int i = 0; i < spots.size(); i++) {
                        markerKeeper.get(i).setVisible(false);
                    }
                    for (Polyline line : linesKeeper) {
                        line.setVisible(false);
                    }
                } else {
                    for (int i = 0; i < spots.size(); i++) {
                        markerKeeper.get(i).setVisible(true);
                    }
                    for (Polyline line : linesKeeper) {
                        line.setVisible(true);
                    }
                }
//                Log.d("ttw", "zoom1: " + cameraPosition.zoom);
            }
        });

        aMap.setOnMapLoadedListener(() -> {
            if (aMap.getCameraPosition().zoom <= 16.0) {
                for (int i = 0; i < spots.size(); i++) {
                    markerKeeper.get(i).setVisible(false);
                }
            } else {
                for (int i = 0; i < spots.size(); i++) {
                    markerKeeper.get(i).setVisible(true);
                }
            }
//            Log.d("ttw", "zoom2: " + aMap.getCameraPosition().zoom);
        });
    }

    private void parseSpotData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray array = jsonObject.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Spot spot = new Spot();
                spot.setSpotName(object.getString("spotName"));
                spot.setCoordX(object.getDouble("coordX"));
                spot.setCoordY(object.getDouble("coordY"));
                spot.setSpotInfo(object.getString("spotInfo"));
                spotsInfo.add(spot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //初始化地图控件
    private void init() {
        aMap = mapView.getMap();
        CameraUpdate cu = CameraUpdateFactory.zoomTo(16f);
        aMap.moveCamera(cu);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.showMyLocation(true);
        myLocationStyle.interval(2000);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);     //定位蓝点精度圆圈颜色设置为透明
        myLocationStyle.strokeWidth(0);                         //去掉定位蓝点精度圆圈外的线

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);

        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(34.157519, 108.898312));
        points.add(new LatLng(34.157191, 108.907946));
        points.add(new LatLng(34.150412, 108.906487));
        points.add(new LatLng(34.150452, 108.903381));
        points.add(new LatLng(34.14885, 108.903027));
        points.add(new LatLng(34.148792, 108.897867));
        points.add(new LatLng(34.157519, 108.898312));
        Polyline line = aMap.addPolyline(new PolylineOptions().addAll(points).geodesic(true)
                .width(10).color(Color.BLUE));
        line.setVisible(true);

        //打开应用时请求并初始化景点信息
        spots = RequestSpotInfo();
//        Log.d("ttw", "firstrequestspots: " + spots);

        colorlist.add(Color.rgb(0, 0, 255));
        colorlist.add(Color.rgb(0, 0, 255));
        colorlist.add(Color.rgb(0, 0, 0));
        colorlist.add(Color.rgb(0, 0, 0));
        colorlist.add(Color.rgb(255, 0, 0));
        colorlist.add(Color.rgb(255, 0, 0));
        colorlist.add(Color.rgb(255, 0, 255));
        colorlist.add(Color.rgb(255, 0, 255));
        colorlist.add(Color.rgb(0, 255, 0));
        colorlist.add(Color.rgb(0, 255, 0));
        colorlist.add(Color.rgb(0, 255, 255));
        colorlist.add(Color.rgb(0, 255, 255));
        colorlist.add(Color.rgb(138, 43, 226));
        colorlist.add(Color.rgb(138, 43, 226));
        colorlist.add(Color.rgb(244, 164, 95));
        colorlist.add(Color.rgb(244, 164, 95));
        colorlist.add(Color.rgb(255, 255, 0));
        colorlist.add(Color.rgb(255, 255, 0));
        colorlist.add(Color.rgb(34, 139, 34));
        colorlist.add(Color.rgb(34, 139, 34));
        colorlist.add(Color.rgb(0, 199, 140));
        colorlist.add(Color.rgb(0, 199, 140));
        colorlist.add(Color.rgb(255, 215, 0));
        colorlist.add(Color.rgb(255, 215, 0));
        colorlist.add(Color.rgb(75,0,130));
        colorlist.add(Color.rgb(75,0,130));
        colorlist.add(Color.rgb(230,230,250));
        colorlist.add(Color.rgb(230,230,250));
        colorlist.add(Color.rgb(112,128,144));
        colorlist.add(Color.rgb(112,128,144));
        colorlist.add(Color.rgb(95,158,160));
        colorlist.add(Color.rgb(95,158,160));
        colorlist.add(Color.rgb(0,191,255));
        colorlist.add(Color.rgb(0,191,255));
    }

    //请求地点信息
    private ArrayList<MarkerOptions> RequestSpotInfo() {
        RequestWithOkhttp.sendokHttpRequest("http://192.168.1.26:8080/querySpotInfo", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                data = response.body().string();
                try {
                    parseSpotData(data);
                    for (int i = 0; i < spotsInfo.size(); i++) {
                        Spot spot = spotsInfo.get(i);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(spot.getCoordY(), spot.getCoordX()))
                                .title(spot.getSpotName()).snippet(spot.getSpotInfo());


/*                        MarkerOptions options = new MarkerOptions()
                                .position(new LatLng(spot.getCoordY(), spot.getCoordX()))
                                .title(spot.getSpotName()).snippet(spot.getSpotInfo())
                                .setFlat(true)      //是否平贴到地图上
                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                        .decodeResource(getResources(), R.drawable.location)));*/
                        spots.add(markerOptions);
                        markerKeeper.add(aMap.addMarker(markerOptions));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        Log.d("ttw", "requestspots: " + spots);
        return spots;
    }

    //对相应功能做出响应：请求相应的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            assert data != null;
            FormBody body = null;
            if (requestCode == 1 || requestCode == 2 || requestCode == 4) {
                String start = data.getStringExtra("start");
                String dest = data.getStringExtra("dest");
                assert start != null;
                assert dest != null;
                body = new FormBody.Builder()
                        .add("startSpotName", start)
                        .add("endSpotName", dest)
                        .build();
            }
            switch (requestCode) {
                case 1:
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://192.168.1.26:8080/querySimplePaths")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Looper.prepare();
                            Toast.makeText(context, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String data = response.body().string();
                            if ("".equals(data) || data.length() == 0) {
                                Toast.makeText(context, "请输入正确的起点或终点", Toast.LENGTH_SHORT).show();
                            } else {
                                parseAllPath(data);
                                drawAllPath(allPathList);
                            }
                        }
                    });
                    drawerLayout.closeDrawer(GravityCompat.END);
                    break;
                case 2:
                    client = new OkHttpClient();
                    request = new Request.Builder()
                            .url("http://192.168.1.26:8080/queryMinPath")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Looper.prepare();
                            Toast.makeText(context, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String data = response.body().string();
                            if ("".equals(data) || data.length() == 0) {
                                Toast.makeText(context, "请输入正确的起点或终点", Toast.LENGTH_SHORT).show();
                            } else {
                                parseMinPath(data);
                                drawMinPath(minPathList);
                            }
                        }
                    });
                    drawerLayout.closeDrawer(GravityCompat.END);
                    break;
                case 3:
                    String spotName = data.getStringExtra("spotName");
                    int i = 0;
                    for (; i < spotsInfo.size(); i++) {
                        Spot spot = spotsInfo.get(i);
                        if (spot.getSpotName().equals(spotName)) {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition(new LatLng(spot.getCoordY(),
                                            spot.getCoordX()), 17f, 0, 0));
                            Marker marker = markerKeeper.get(i);
                            marker.showInfoWindow();
                            aMap.moveCamera(cameraUpdate);
                            break;
                        }
                    }
                    if (i == spotsInfo.size()) {
                        Toast.makeText(context, "未查询到该地点，请重新查询", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.END);
                    break;
                case 4:
                    client = new OkHttpClient();
                    request = new Request.Builder()
                            .url("http://192.168.1.26:8080/addPath")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Looper.prepare();
                            Toast.makeText(context, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            try {
                                String data = response.body().string();
                                JSONObject object = new JSONObject(data);
                                if (object.getBoolean("succeed")) {
                                    Looper.prepare();
                                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(context, "输入有误或该路径已存在", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    drawerLayout.closeDrawer(GravityCompat.END);
                default:
                    break;
            }
        }

    }

    //绘制出所有路径
    private void drawAllPath(ArrayList<ArrayList<LatLng>> allPathList) {
        int i = 0;
        for (ArrayList<LatLng> path : allPathList) {
            Polyline line = aMap.addPolyline(new PolylineOptions().addAll(path).geodesic(true)
                    .width(10).color(colorlist.get(i)));
            Log.d("ttw", "i: " + i + "size: " + colorlist.size());
            if(i<colorlist.size()) i++;
            linesKeeper.add(line);
            line.setVisible(true);
        }
    }

    //绘制最短路径
    private void drawMinPath(ArrayList<LatLng> minPathList) {
        Polyline line = aMap.addPolyline(new PolylineOptions().addAll(minPathList).geodesic(true)
                .width(10).color(Color.GREEN));
        linesKeeper.add(line);
        line.setVisible(true);
    }

    //解析所有路径信息
    private void parseAllPath(String string) {
        deleteLines();
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);
                ArrayList<LatLng> latLngs = new ArrayList<>();
                for (int j = 0; j < array.length(); j++) {
                    JSONObject object = array.getJSONObject(j);
                    double x = object.getDouble("coordX");
                    double y = object.getDouble("coordY");
                    LatLng latLng = new LatLng(y, x);
                    latLngs.add(latLng);
                }
                allPathList.add(latLngs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //解析最短路径信息
    private void parseMinPath(String string) {
        deleteLines();
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                double x = object.getDouble("coordX");
                double y = object.getDouble("coordY");
                LatLng latLng = new LatLng(y, x);
                minPathList.add(latLng);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteLines() {
        if (allPathList.size() != 0) {
            for (Polyline line : linesKeeper) {
                line.remove();
            }
        }
        if (minPathList.size() != 0) {
            for (Polyline line : linesKeeper) {
                line.remove();
            }
        }
        allPathList.clear();
        minPathList.clear();
    }

    //监听功能列表的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_funs:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.ll_searchAllPath:
                Intent intent = new Intent(context, SearchPathActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.ll_searchMinPath:
                intent = new Intent(context, SearchPathActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.ll_addPath:
                intent = new Intent(context, AddPathActivity.class);
                startActivityForResult(intent, 4);
                break;
            case R.id.ll_query:
                intent = new Intent(context, QueryActivity.class);
                startActivityForResult(intent, 3);
                break;
            default:
                break;
        }
    }
}
