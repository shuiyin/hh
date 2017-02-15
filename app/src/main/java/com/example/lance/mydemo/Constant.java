package com.example.lance.mydemo;

import java.util.Calendar;

/**
 * Created by lance on 2017/2/10.
 */

public class Constant {

    public static enum WhoCall{
        //判断谁调用了dialogSetRange,以决定哪个控件该gone或者visible
        SETTING_ALARM,//表示设置闹钟按钮
        SETTING_DATE,//表示设置日期按钮
        SETTING_RANGE,//表示设置日程查找范围按钮
        NEW,//表示新建日程按钮
        EDIT,//表示修改日程按钮
        SEARCH_RESULT//表示查找按钮
    }
    public static enum Layout
    {
        WELCOME_VIEW,
        MAIN,//主界面
        SETTING,//日程设置
        TYPE_MANAGER,//类型管理
        SEARCH,//查找
        SEARCH_RESULT,//查找结果界面
        HELP,//帮助界面
        ABOUT
    }
    public static String getNowDateString(){//获得当前日期方法并转换格式YYYY/MM/DD
        Calendar c = Calendar.getInstance();
        String nowDate = Schedule.toDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
        return nowDate;
    }
    public static String getNowTimeString(){//获得当前时间，并转换成格式HH:MM
        Calendar c=Calendar.getInstance();
        int nowh=c.get(Calendar.HOUR_OF_DAY);
        int nowm=c.get(Calendar.MINUTE);
        String nowTime=(nowh<10?"0"+nowh:""+nowh)+":"+(nowm<10?"0"+nowm:""+nowm);
        return nowTime;
    }

}
