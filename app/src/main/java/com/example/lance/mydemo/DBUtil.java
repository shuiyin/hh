package com.example.lance.mydemo;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by lance on 2017/2/10.
 */

public class DBUtil {
    public static SQLiteDatabase sld;

    //=================所有处理类型数据库的方法start==========
    public static void loadType(RcActivity father){
        //从类型数据库中读取数据
        try{
            sld = SQLiteDatabase.openDatabase(
              "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY
            );
            String sql = "create table if not exists type(tno integer primary key,tname varchar2(20));";
            sld.execSQL(sql);
            Cursor cursor = sld.query("type",null,null,null,null,null,"tno");
            int count = cursor.getCount();
            if(count == 0){//如果是第一次运行程序，自动创建3个缺省类型
                for(int i=0;i<father.defultType.length;i++){
                    sql = "insert into type values("+i+",'"+father.defultType[i]+"')";
                    sld.execSQL(sql);
                }
                cursor = sld.query("type",null,null,null,null,null,"tno");
                count = cursor.getCount();
            }
            father.alType.clear();
            while (cursor.moveToNext()){
                father.alType.add(cursor.getString(1));
            }
            sld.close();
            cursor.close();
        }catch (Exception e){
            Toast.makeText(father,"类型数据库打开创建错误:"+e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    public static boolean insertType(RcActivity father,String newType){//更新类型数据库
        Cursor cursor = null;
        boolean duplicate = false;//false代表没有类型名称重复，true代表有重复
        try{
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            cursor = sld.query("type",null,null,null,null,null,"tno");
            father.alType.clear();
            while (cursor.moveToNext()){//存入新日程时，与数据库中已有的日程进行比较，如果重复，则标志位设为true
                if(newType.equals(cursor.getString(1))){
                    duplicate = true;
                }
                father.alType.add(cursor.getString(1));
            }
            if(!duplicate){
                father.alType.add(newType);
                String sql = "delete from type";
                sld.execSQL(sql);
                for(int i=0;i<father.alType.size();i++){
                    sql = "insert into type values("+i+",'"+father.alType.get(i)+"')";
                    sld.execSQL(sql);
                }
                Toast.makeText(father,"成功添加类型"+newType,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(father,"类型名称重复！",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(father, "类型数据库更新错误："+e.toString(), Toast.LENGTH_LONG).show();
            return false;
        }finally {
            cursor.close();
            sld.close();
        }
        return true;
    }

    public static void deleteType(RcActivity father,String s){
        try{
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            String sql = "delete from type where tname = '"+s+"'" ;
            sld.execSQL(sql);
            Toast.makeText(father,"成功删除类型",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(father,"类型删除错误:" +e.toString(),Toast.LENGTH_LONG).show();
        }finally {
            sld.close();
        }
    }

    public static ArrayList<String> getAllType(RcActivity father){
        ArrayList<String> type = new ArrayList<>();
        type = father.alType;//把软件中存在的类型放入type
        try{
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            String sql = "select distinct type from schedule;";
            Cursor cursor = sld.rawQuery(sql,new String[]{});
            while (cursor.moveToNext()){
                if(!type.contains(cursor.getString(0))){
                    type.add(cursor.getString(0));
                }
            }
            sld.close();
            cursor.close();
        }catch (Exception e){
            Toast.makeText(father,"获取类型错误:"+e.toString(),Toast.LENGTH_LONG).show();
            Log.d("exception!!",e.toString());
        }
        return type;
    }
    //============================所有处理类型数据库的方法end==============================

    //============================所有处理日程数据库的方法start==============================
    public static void loadSchedule(RcActivity father){//从日程数据库读取日程数据
        try{
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY
            );
            String sql = "create table if not exists schedule(" +
                    "sn integer primary key," +
                    "date1 char(10)," +
                    "time1 char(5)," +
                    "date2 char(10)," +
                    "time2 char(5)," +
                    "title varchar2(40)," +
                    "note varchar2(120)," +
                    "type varchar2(20)," +
                    "timeset boolean," +
                    "alarmset boolean" +
                    ")";
            sld.execSQL(sql);
            Cursor cursor = sld.query("schedule",null,null,null,null,null,"date1 desc,time1 desc");//按datetime1倒排序
            while (cursor.moveToNext()){
                int sn = cursor.getInt(0);
                String date1 = cursor.getString(1);
                String time1 = cursor.getString(2);
                String date2 = cursor.getString(3);
                String time2 = cursor.getString(4);
                String title=cursor.getString(5);
                String note=cursor.getString(6);
                String type=cursor.getString(7);
                String timeSet=cursor.getString(8);
                String alarmSet=cursor.getString(9);
                Schedule schTemp = new Schedule(sn,date1,time1,date2,time2,title,note,type,timeSet,alarmSet);
                father.alSch.add(schTemp);
                Log.d("schdata",""+cursor.getPosition()+":sn="+sn+":"+date1+","+time1+","+date2+","+timeSet);
            }
            sld.close();
            cursor.close();
        }catch (Exception e){
            Toast.makeText(father,"日程数据库打开创建错误：" +e.toString(),Toast.LENGTH_LONG).show();
            Log.d("exception",e.toString());
        }
    }
    public static void insertSchedule(RcActivity father){//插入日程
        try {
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            String sql = father.schTemp.toInsertSql(father);
            sld.execSQL(sql);
            sld.close();
        }catch (Exception e){
            Toast.makeText(father,"日程数据库更新错误：" +e.toString(),Toast.LENGTH_LONG).show();
            Log.d("exception!!",e.toString());
        }
    }
    public static void updateSchedule(RcActivity father){//更新日程
        try {
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            String sql = father.schTemp.toUpdateSql(father);
            sld.execSQL(sql);
            sld.close();
        }catch (Exception e){
            Toast.makeText(father,"日程数据库更新错误："+e.toString(), Toast.LENGTH_LONG).show();
            Log.d("exception!!",e.toString());
        }
    }
    public static void deleteSchedule(RcActivity father){//删除日程
        try{
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            int sn = father.schTemp.getSn();
            String sql = "delete from schedule where sn="+sn;
            sld.execSQL(sql);
            sld.close();
            Toast.makeText(father,"删除成功",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(father,"日程删除错误："+e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public static void deletePassedSchedule(RcActivity father){//删除所有过期日程
        try{
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            String nowDate = Constant.getNowDateString();
            String nowTime = Constant.getNowTimeString();
            String sql = "date1<'"+nowDate+"' or date1='"+nowDate+"' and time1<'"+nowTime+"'";
            sql = "delete from schedule where date1<'"+nowDate+"' or date1='"+nowDate+"' and time1<'"+nowTime+"'";
            sld.execSQL(sql);
            sld.close();
            Toast.makeText(father,"成功删除过期日程",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(father,"日程删除错误:" + e.toString(),Toast.LENGTH_LONG).show();
            Log.d("error",e.toString());
        }
    }
    public static void searchSchedule(RcActivity father,ArrayList<String> allKindsType){//查找日程
        ArrayList<Boolean> alSelectedType = father.alSelectedType;
        try{
            sld = SQLiteDatabase.openDatabase(
                    "/data/data/com.example.lance.mydemo/myDb.db",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
            String[] args = new String[2];
            args[0] = father.rangeFrom;
            args[1] = father.rangeTo;
            String sql = "select * from schedule where date1 between ? and ?";
            StringBuffer sbtmp = new StringBuffer();
            sbtmp.append(" and (type=");
            for(int i=0;i<alSelectedType.size();i++){
                if(alSelectedType.get(i)){
                    sbtmp.append("'");
                    sbtmp.append(allKindsType.get(i));
                    sbtmp.append("' or type=");
                }
            }
            String strSelectedType = sbtmp.toString();
            strSelectedType = strSelectedType.substring(0,strSelectedType.length()-9);//最后去掉后面的" or type="
            sql += strSelectedType+")";

            Log.d("search sql:",sql);
            Cursor cursor = sld.rawQuery(sql,args);
            Toast.makeText(father,"搜索到"+cursor.getCount()+"条日程",Toast.LENGTH_SHORT).show();
            father.alSch.clear();
            while (cursor.moveToNext()){
                int sn = cursor.getInt(0);
                String date1=cursor.getString(1);
                String time1=cursor.getString(2);
                String date2=cursor.getString(3);
                String time2=cursor.getString(4);
                String title=cursor.getString(5);
                String note=cursor.getString(6);
                String type=cursor.getString(7);
                String timeSet=cursor.getString(8);
                String alarmSet=cursor.getString(9);
                Schedule schTemp=new Schedule(sn,date1,time1,date2,time2,title,note,type,timeSet,alarmSet);
                father.alSch.add(schTemp);
            }
        }catch (Exception e){
            Toast.makeText(father,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    //=========================所有处理日程数据库的方法end================
    public static int getSNFromPrefs(RcActivity father){//读取preferences里面的日程sn
        SharedPreferences sp = father.getSharedPreferences("SN", Context.MODE_PRIVATE);
        int sn = sp.getInt("sn",0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sn",sn+1);
        editor.commit();
        return sn;
    }

}
