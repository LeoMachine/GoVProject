package com.example.mq661.govproject.BookRoom;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
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
import com.example.mq661.govproject.Login_Register.Login;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.roomAdapterInfo;
import com.example.mq661.govproject.SearchRoom.searchroom;
import com.example.mq661.govproject.mytoken.tokenDBHelper;
import com.example.mq661.govproject.tools.dateToString;
import com.example.mq661.govproject.tools.tounicode;

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

    public class Participants extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
        private String Token1,EmployeeNumber1,Name1,Ministry1;
        private  int num=0;
        private List<people> data;
        Button button1,button2,button3,button4;
        ListView listView;
        private OkHttpClient okhttpClient;
        private tokenDBHelper helper;
        private LinearLayout linear;
        private ListView participantslv;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.participants_lv_layout);
            helper=new tokenDBHelper(this);
            initView();
        }

        private void initView() {
           // participantslv=findViewById(R.id.participantslv);
            button1 = (Button) findViewById(R.id.button1);// 全选
            button2 = (Button) findViewById(R.id.button2);// 反选
            button3 = (Button) findViewById(R.id.button3);// 全不选
            button4 = (Button) findViewById(R.id.button4);// 删除
            button1.setOnClickListener(this);
            listView = (ListView) findViewById(R.id.listveiw);//
            listView.setOnItemClickListener(this);       //设置短按事件


        }

        @Override
        public void onClick(View v) {
            data=new ArrayList<people>();

            Token1=select();
            Toast.makeText(this, "读本地"+Token1, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    sendRequest(Token1);
                }
            }).start();
        }

        private void sendRequest(String Token1) {
            //
            Map map = new HashMap();
            map.put("Token", Token1);

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
                    .url("http://39.96.68.13:8080/SmartRoom/MainInterfaceServlet")
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
                            Toast.makeText(Participants.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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


                            String Name = tounicode.decodeUnicode(jsonObj.getString("Name"));
                            String EmployeeNumber = jsonObj.getString("EmployeeNumber");
                            String Ministry = tounicode.decodeUnicode( jsonObj.getString("Ministry"));
                            String mapx="map"+i;

                            showRequestResult(Name, EmployeeNumber, Ministry, mapx);
                        }





                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        private void showRequestResult(final String Name,final String EmployeeNumber,final String Ministry,final String mapx) {
            runOnUiThread(new Runnable() {
                @Override
                /**
                 * 实时更新，数据库信息改变时，客户端内容发生改变
                 */
                public void run() {


                        linear.removeAllViews();
                        people mapx = new people();
                        mapx.setName(Name);
                        mapx.setEmployeeNumber(EmployeeNumber);
                        mapx.setMinistry(Ministry);
                        data.add(mapx);
                        listView.setAdapter(new Participants.MyAdapter());

                    }

            });
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
            intent = new Intent(this, Login.class);
            startActivityForResult(intent, 0);
            finish();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

                View view =View.inflate(Participants.this,R.layout.participants_adp_layout,null);


                TextView EmployeeNumber = view.findViewById(R.id.EmployeeNumber);
                TextView  Ministry = view.findViewById(R.id.Ministry);
                TextView Name = view.findViewById(R.id.Name);

                EmployeeNumber.setText(data.get(position).getEmployeeNumber());
                Ministry.setText(data.get(position).getMinistry());
                Name.setText(data.get(position).getName());
                return view;
            }
        }
    }

