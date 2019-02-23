package com.example.mq661.govproject.HomePage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.deleteroom;
import com.example.mq661.govproject.BookRoom.bookroomServer;
import com.example.mq661.govproject.Login_Register.Login;
import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.roomAdapterInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;
import com.example.mq661.govproject.tools.dateToString;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainInterfaceToday_handler extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private String ssBuildingNumber,ssRoomNumber,ssTime,ssSize,ssFunction,ssIsMeeting,IsMeeting2,ssDays,ssssTime;
    private List<roomAdapterInfo> data;
    Button commit;
    Intent ssdata=new Intent();
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private String Token1;
    private LinearLayout linear;
    private ListView searchroomlv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchroom_lv_layout);
        new Thread(runnable).start();  //启动子线程
        helper=new tokenDBHelper(this);
        initView();
    }
    private void initView() {
        ssTime= dateToString.nowdateToString2();//获取当前时间
        searchroomlv=findViewById(R.id.searchroomlv);
        searchroomlv.setOnItemClickListener(this);       //设置短按事件
          searchroomlv.setOnItemLongClickListener(this);   //设置长按事件
    }
    //handler 处理返回的请求结果
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            //
            // TODO: 更新界面
            //
            Log.i("mylog","请求结果-->" + val);
        }
    };
    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    //新线程进行网络请求
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //
            // TODO: http request.
            //
            data=new ArrayList<roomAdapterInfo>();
            Token1=select();
            sendRequest(Token1,ssTime);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }


    };
    private void sendRequest(String Token1,String Time1) {
        //
        Map map = new HashMap();
        map.put("Token", Token1);
        map.put("Time", Time1);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                //  .url("http://192.168.43.174:8080/SmartRoom4/SelectServlet") //马琦IP
                .url("http://39.96.68.13:8080/SmartRoom/MainInterface1Servlet")
                // .url("http://192.168.2.176:8080/SmartRoom/login")
                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainInterfaceToday_handler.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i=0; i < jsonArray.length(); i++)    {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String BuildingNumber1 =   jsonObj.getString("buildingNumber");
                        String RoomNumber1 = jsonObj.getString("roomNumber");
                        String Time1 =    jsonObj.getString("time");
                        String  Size1 = jsonObj.getString("size");
                        String  Function1 =  jsonObj.getString("functions");
                        String  IsMeeting = jsonObj.getString("isMeeting");
                        if(IsMeeting.equals("0"))
                        {
                            IsMeeting2="空闲";
                        }
                        else if(IsMeeting.equals("1"))
                        {
                            IsMeeting2="占用";
                        }
                        else if(IsMeeting.equals("2"))
                        {
                            IsMeeting2="维修中";
                        }
                        else{
                            IsMeeting2="未知";
                        }
                        String  Days =   jsonObj.getString("days");
                        String mapx="map"+i;

                        if(BuildingNumber1.equals("-1")&&RoomNumber1.equals("-1")&&Time1.equals("-1")) {

                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2,Days, mapx);

                            break; }
                        else if(BuildingNumber1.equals("-3")&&RoomNumber1.equals("-3")&&Time1.equals("-3")) {

                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2,Days, mapx);

                            break; }

                        else  showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2,Days, mapx);

                    }





                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void showRequestResult(final String BuildNumber1,final String RoomNumber1,final String Time1,final String Size1,final String Function1,final String IsMeeting1,final String Days1,final String mapx) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if(BuildNumber1.equals("-1")&&RoomNumber1.equals("-1")&&Time1.equals("-1")) {
                    Toast.makeText(MainInterfaceToday_handler.this, "查询不成功！", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(),"0");
                    relog();
                }
                else if(BuildNumber1.equals("-3")&&RoomNumber1.equals("-3")&&Time1.equals("-3")) {
                    Toast.makeText(MainInterfaceToday_handler.this, "token失效！请重新登录", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(),"0");
                    relog();
                }
                else {
                    roomAdapterInfo mapx = new roomAdapterInfo();
                    mapx.setBuildingNumber(BuildNumber1);
                    mapx.setRoomNumber(RoomNumber1);
                    mapx.setFunction(Function1);
                    mapx.setSize(Size1);
                    mapx.setTime(Time1);
                    mapx.setIsMeeting(IsMeeting1);
                    mapx.setDays(Days1);
                    data.add(mapx);

                    searchroomlv.setAdapter(new MainInterfaceToday_handler.MyAdapter());
                }
            }
        });
    }
    public void onClick(View v) {

    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ssBuildingNumber=data.get(position).getBuildingNumber();
        ssSize=data.get(position).getSize();
        ssRoomNumber=data.get(position).getRoomNumber();
        ssTime=data.get(position).getTime();
        ssFunction=data.get(position).getFunction();
        ssIsMeeting=data.get(position).getIsMeeting();
        ssDays=data.get(position).getDays();

        showMultiBtnDialog(ssBuildingNumber,ssSize,ssRoomNumber,ssTime,ssFunction,ssIsMeeting,ssDays);

    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ssBuildingNumber=data.get(position).getBuildingNumber();
        ssSize=data.get(position).getSize();
        ssRoomNumber=data.get(position).getRoomNumber();
        ssTime=data.get(position).getTime();
        ssFunction=data.get(position).getFunction();
        ssIsMeeting=data.get(position).getIsMeeting();
        ssDays=data.get(position).getDays();
        showMultiBtnDialog(ssBuildingNumber,ssSize,ssRoomNumber,ssTime,ssFunction,ssIsMeeting,ssDays);
        return true;      //返回true时可以解除长按与短按的冲突。


    }

    private class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return data.size();

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view =View.inflate(MainInterfaceToday_handler.this,R.layout.wty_searchroom_adp_layout,null);


            TextView BuildingNumber = view.findViewById(R.id.BuildNumber);
            TextView  RoomNumber = view.findViewById(R.id.RoomNumber);
            TextView Time = view.findViewById(R.id.Time);
            TextView Size = view.findViewById(R.id.Size);
            TextView Function=view.findViewById(R.id.Function);
            TextView IsMeeting=view.findViewById(R.id.IsMeeting);
            TextView Days=view.findViewById(R.id.Days3);
            BuildingNumber.setText(data.get(position).getBuildingNumber());
            Size.setText(data.get(position).getSize());
            RoomNumber.setText(data.get(position).getRoomNumber());
            Time.setText(data.get(position).getTime());
            Function.setText(data.get(position).getFunction());
            IsMeeting.setText(data.get(position).getIsMeeting());
            Days.setText(data.get(position).getDays());
            return view;
        }
    }

    /* @setNeutralButton 设置中间的按钮
     * 若只需一个按钮，仅设置 setPositiveButton 即可
     */
    public void showMultiBtnDialog(final String BuildingNumber, String Size, final String RoomNumber,
                                   final String Time, String Function, String IsMeeting, final String Days) {


         AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this.getParent());
        normalDialog.setIcon(R.drawable.app);
        normalDialog.setTitle("GoV").setMessage("房间信息：\n" + "楼号：" + BuildingNumber + " 房间号：" + RoomNumber + " 容量：" + Size + "\n时间段：" + Time + "    功能：" + Function + "\n是否开会：" + IsMeeting
                + "       日期： " + Days
        );

        normalDialog.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        normalDialog.setNegativeButton("预约", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO
                //单独的HTTP请求
                bookroomServer book = new bookroomServer();
                book.setContent(MainInterfaceToday_handler.this);
                try {
                    book.startbookroom(BuildingNumber, RoomNumber, Time, Token1, Days);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 创建实例并显示
//        normalDialog.show();
//        runnable2 = new Runnable() {
//            @Override
//            public void run() {
//                //
//                // TODO: http request.
//                //
//
//                Message msg = new Message();
//                Bundle data = new Bundle();
//                data.putString("value", "请求结果");
//                msg.setData(data);
//                handler2.sendMessage(msg);
//                normalDialog.show();
//            }
//        };
        normalDialog.show();
    }
    public void deleteroom()
    {
        Intent intent;
        intent = new Intent(this, deleteroom.class);
        startActivityForResult(intent, 0);

        // finish();
    }


    public void insert(String token){


        //自定义增加数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        //String token =mytoken.getMytoken();

        values.put("token", token);
        long l = db.insert("token", null, values);

        if(l==-1){
            Toast.makeText(this, "插入不成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "插入成功"+l,Toast.LENGTH_SHORT).show();}
        db.close();
    }

    public void update(String token){


        //自定义更新
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        //     String oldtoken=mytoken.getMytoken();
        values.put("token", token);
//        int i = db.update("token", values, "token=?",new String[]{oldtoken});
        int i = db.update("token", values, null,null);
        if(i==0){
            Toast.makeText(this, "更新不成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "更新成功"+i,Toast.LENGTH_SHORT).show();}
        db.close();
    }

    public void delete(String token){

        SQLiteDatabase db = helper.getWritableDatabase();



        int i = db.delete("token", "token=?",new String[]{token});
        if(i==0){
            Toast.makeText(this, "删除不成功",Toast.LENGTH_SHORT).show();
        }else{  Toast.makeText(this, "删除成功"+i,Toast.LENGTH_SHORT).show();}
        db.close();

    }

    //查找
    public String select(){

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from token", null);
        String token1=null;
        while(cursor.moveToNext()){
//            mytoken token= new mytoken();
//            token.setMytoken(cursor.getString(0));
            token1=cursor.getString(0);
        }
        db.close();
        return token1;
    }
    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1){
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
}