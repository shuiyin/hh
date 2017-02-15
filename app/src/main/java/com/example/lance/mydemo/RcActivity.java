package com.example.lance.mydemo;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import static com.example.lance.mydemo.Constant.*;
import static com.example.lance.mydemo.DBUtil.*;



public class RcActivity extends AppCompatActivity {
    public String[] defultType = new String[]{"会议","备忘","待办"};//软件的三个不能删除的默认类型
    public static ArrayList<Schedule> alSch = new ArrayList<>();//存储所有schedule对象的ArrayList
    public static ArrayList<String> alType = new ArrayList<>();//存储所有日程类型的arraylist
    public Schedule schTemp;//临时的schedule
    public String rangeFrom=getNowDateString();//查找日程时设置的起始日期，默认当前日期
    public String rangeTo=rangeFrom;//查找日程时设置的终止日期，默认当前日期
    public ArrayList<Boolean> alSelectedType = new ArrayList<>();//记录查找界面中类型前面checkbox状态的
    Layout curr = null;//记录当前界面的枚举类型
    Constant.WhoCall wcSetTimeOrAlarm;//用来判断调用时间日期对话框的按钮是设置时间还是设置闹钟,以便更改对话框中的一些控件该设置为visible还是gone
    Constant.WhoCall wcNewOrEdit;//用来判断调用日程编辑界面的是新建日程按钮还是在修改日程按钮，以便设置对应的界面标题
    int sel=0;
	/*临时记录新建日程界面里的类型spinner的position，因为设置时间的对话框cancel后
	     回到新建日程界面时会刷新所有控件，spinner中以选中的项目也会回到默认*/

    Handler hd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    gotoMain();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
        goToWelcomeView();



    }
    //欢迎界面
    public void goToWelcomeView(){
        MySurfaceView mview = new MySurfaceView(this);
        /**
         getWindow().setFlags(//全屏
         WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN
         );
         */
        setContentView(mview);
    }
    //==============主界面Start======================
    public void gotoMain(){//初始化主界面
        /**
         getWindow().setFlags(//非全屏
         WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
         );*/
        setContentView(R.layout.main);

        ImageButton bNew = (ImageButton) findViewById(R.id.ibmainNew);//新建日程按钮
        ImageButton bDel = (ImageButton) findViewById(R.id.ibmainDel);//删除当前选中日程的按钮
        ImageButton bCheck = (ImageButton) findViewById(R.id.ibmainCheck);//查看日程详细内容的按钮
        ImageButton bEdit = (ImageButton) findViewById(R.id.ibmainEdit);//修改日程按钮
        ImageButton bDelAll = (ImageButton) findViewById(R.id.ibmainDelAll);//删除所有过期日程按钮
        ImageButton bSearch = (ImageButton) findViewById(R.id.ibmainSearch);//查找日程按钮
        ListView lv = (ListView) findViewById(R.id.lvmainSchedule);//日程列表

        bCheck.setEnabled(false);//这三个按钮分别为主界面的日程查看、日程修改、日程删除,
        bEdit.setEnabled(false);//默认设为不可用状态
        bDel.setEnabled(false);

        alSch.clear();//从数据库读取之前清空存储日程的arraylist
        loadSchedule(this);//从数据库中读取日程
        loadType(this);//从数据库中读取类型

        //bNew设置
        bNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int t1 = c.get(Calendar.YEAR);
                int t2 = c.get(Calendar.MONTH)+1;
                int t3 = c.get(Calendar.DAY_OF_MONTH);
                schTemp = new Schedule(t1,t2,t3);//临时新建一个日程对象，年月日设为当前日期
                wcNewOrEdit = Constant.WhoCall.NEW;//调用日程编辑界面的是新建按钮
                gotoSetting();//去日程编辑界面
            }
        });
        //bEdit设置
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wcNewOrEdit = Constant.WhoCall.EDIT;//调用日程编辑界面的是修改按钮
                gotoSetting();//去日程编辑界面
            }
        });

    }
    //=============日程编辑界面start==============================
    public void gotoSetting(){//初始化新建日程界面

        setContentView(R.layout.new_schedule);
        curr = Layout.SETTING;

        TextView tvTitle = (TextView) findViewById(R.id.tvnewscheduleTitle);
        if(wcNewOrEdit == Constant.WhoCall.NEW){
            tvTitle.setText("新建日程");
        }else if(wcNewOrEdit == Constant.WhoCall.EDIT){
            tvTitle.setText("修改日程");
        }
        final Spinner spType = (Spinner) findViewById(R.id.spxjrcType);
        Button bNewType = (Button) findViewById(R.id.bxjrcNewType);
        final EditText etTitle = (EditText) findViewById(R.id.etxjrcTitle);
        final EditText etNote = (EditText) findViewById(R.id.etxjrcNote);
        TextView tvDate = (TextView) findViewById(R.id.tvnewscheduleDate);
        Button bSetDate = (Button) findViewById(R.id.bxjrcSetDate);
        TextView tvTime = (TextView) findViewById(R.id.tvnewscheduleTime);
        TextView tvAlarm = (TextView) findViewById(R.id.tvnewscheduleAlarm);
        Button bSetAlarm = (Button) findViewById(R.id.bxjrcSetAlarm);
        Button bDone = (Button) findViewById(R.id.bxjrcDone);
        Button bCancel = (Button) findViewById(R.id.bxjrcCancel);

        etTitle.setText(schTemp.getTitle());
        etNote.setText(schTemp.getNote());
        tvDate.setText(schTemp.getDate1());
        tvTime.setText(schTemp.isTimeSet()?schTemp.getTime1():"无具体时间");
        tvAlarm.setText(schTemp.isAlarmSet()?schTemp.getDate2()+" "+schTemp.getTime2():"无闹钟");

        //类型spinner设置
        spType.setAdapter(
                new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return alType.size();
                    }

                    @Override
                    public Object getItem(int i) {
                        return alType.get(i);
                    }

                    @Override
                    public long getItemId(int i) {
                        return 0;
                    }

                    @Override
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        LinearLayout ll = new LinearLayout(RcActivity.this);
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        TextView tv = new TextView(RcActivity.this);
                        tv.setText(alType.get(i));
                        tv.setTextSize(17);
                        tv.setTextColor(R.color.black);
                        return tv;
                    }
                }
        );
        spType.setSelection(sel);
        //新建日程类型按钮
        bNewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schTemp.setTitle(etTitle.getText().toString());//将已经输入的title和note存入schTemp，以防返回时被清空
                schTemp.setNote(etNote.getText().toString());
                sel = spType.getSelectedItemPosition();//存储spType的当前选择
                gotoTypeManager();//进入日程类型管理界面
            }
        });

        bSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schTemp.setTitle(etTitle.getText().toString());//将已经输入的主题和备注存入schTemp，以防设置完时间或闹钟返回时被清空
                schTemp.setNote(etNote.getText().toString());
                sel=spType.getSelectedItemPosition();
                wcSetTimeOrAlarm = WhoCall.SETTING_DATE;//调用设置日期时间对话框的时设置日程日期按钮


            }
        });
    }
    //========================类型管理界面start===============
    public void gotoTypeManager(){
        setContentView(R.layout.typemanager);
        curr = Layout.TYPE_MANAGER;
        final ListView lvType = (ListView) findViewById(R.id.lvtypemanagerType);//列表列出所有已有类型
        final EditText etNew = (EditText) findViewById(R.id.ettypemanagerNewType);//输入新类型名称的TextView
        Button bNew = (Button) findViewById(R.id.btypemanagerNewType);//新建类型按钮
        Button bBack = (Button) findViewById(R.id.btypemanagerBack);//返回上一页按钮

        //返回上一页按钮
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSetting();
            }
        });

        lvType.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return alType.size();
            }

            @Override
            public Object getItem(int i) {
                return alType.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                LinearLayout ll = new LinearLayout(RcActivity.this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setGravity(Gravity.CENTER_VERTICAL);
                TextView tv = new TextView(RcActivity.this);
                tv.setText(alType.get(i));
                tv.setTextSize(17);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(20,0,0,0);
                ll.addView(tv);

                //软件自带的类型不能删除，其他自建类型后面添加一个红叉用来删除自建类型
                if(i>=defultType.length){
                    ImageButton ib = new ImageButton(RcActivity.this);
                    ib.setBackgroundResource(R.drawable.cross);
                    ib.setLayoutParams(new LinearLayoutCompat.LayoutParams(24,24));
                    ib.setPadding(20,0,0,0);

                    ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DBUtil.deleteType(RcActivity.this,lvType.getItemAtPosition(i).toString());
                            loadType(RcActivity.this);
                            gotoTypeManager();
                        }
                    });
                    ll.addView(ib);
                }
                return ll;
            }
        });

        //添加类型管理按钮
        bNew.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newType = etNew.getText().toString().trim();
                        if(newType.equals("")){
                            Toast.makeText(RcActivity.this,"类型名称不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DBUtil.insertType(RcActivity.this,newType);
                        gotoTypeManager();

                    }
                }
        );







    }







}

