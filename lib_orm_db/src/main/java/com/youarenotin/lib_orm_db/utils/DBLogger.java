package com.youarenotin.lib_orm_db.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;

/**
 * Created by lubo on 2016/6/10.
 * lubo_wen@126.com
 */
public class DBLogger {
    private static final String TAG = "Logger";
    private static boolean DEBUG = true;

    public static void v(Object obj){
        if (DEBUG){
            Log.v(TAG,toJson(obj));
        }
    }
    public static void v(String tag , Object o){
        if (DEBUG){
            Log.v(tag,toJson(o));
        }
    }

    public static void v(String tag, String format, Object... args) {
        if (DEBUG)
            Log.v(tag, String.format(format, args));
    }

    public static void d(Object o) {
        if (DEBUG)
            Log.d(TAG, toJson(o));
    }

    public static void d(String tag, Object msg) {
        if (DEBUG)
            Log.d(tag, toJson(msg));
    }

    public static void d(String tag, String format, Object... args) {
        if (DEBUG)
            Log.d(tag, String.format(format, args));
    }

    public static void i(Object o) {
        if (DEBUG)
            Log.i(TAG, toJson(o));
    }

    public static void i(String tag, Object msg) {
        if (DEBUG)
            Log.i(tag, toJson(msg));
    }

    public static void i(String tag, String format, Object... args) {
        if (DEBUG)
            Log.i(tag, String.format(format, args));
    }

    public static void w(Object o) {
        if (DEBUG)
            Log.w(TAG, toJson(o));
    }

    public static void w(String tag, Object msg) {
        if (DEBUG)
            Log.w(tag, toJson(msg));
    }

    public static void w(String tag, String format, Object... msg) {
        if (DEBUG)
            Log.w(tag, String.format(format, msg));
    }

    public static void e(Object o) {
        if (DEBUG)
            Log.e(TAG, toJson(o));
    }

    public static void e(String tag, Object msg) {
        if (DEBUG)
            Log.e(tag, toJson(msg));
    }

    public static void e(String tag, String format, String msg) {
        if (DEBUG)
            Log.w(tag, String.format(format, msg));
    }

    public static void logExc(Exception e) {
        if (DEBUG)
            e.printStackTrace();
    }
    private static String toJson(Object obj){
        if (obj instanceof String){
            return obj.toString();
        }
        String jsonString = JSON.toJSONString(obj);
        if (jsonString.length() > 500){
            jsonString.substring(0,500);
        }
        return jsonString;
    }
}
