package com.actor.testapplication.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actor.myandroidframework.utils.MyOkhttpUtils.BaseCallback;
import com.actor.myandroidframework.utils.MyOkhttpUtils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.SPUtils;
import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;

/**
 * 检查更新并Lock
 * <uses-permission android:name="android.permission.INTERNET" />
 * <service android:name=".utils.CheckUpdateLService" />
 *
 * startService(new Intent(this, CheckUpdateLService.class));
 *
 * @Override
 * public void startActivity(Intent intent) {
 *     if (SPUtils.getBoolean(CheckUpdateLService.Eenable, true)) {
 *         super.startActivity(intent);
 *     } else toast(CheckUpdateLService.ERR_MSG);
 * }
 * @Override
 * public void startActivityForResult(Intent intent, int requestCode) {
 *     if (SPUtils.getBoolean(CheckUpdateLService.Eenable, true)) {
 *         super.startActivityForResult(intent, requestCode);
 *     } else toast(CheckUpdateLService.ERR_MSG);
 * }
 *
 * @version 1.0
 */
public class CheckUpdateLService extends Service {

//    private static final String  E       = "https://raw.githubusercontent.com/actor20170211030627/TestApplication/master/app/build/outputs/apk/debug/yunweipei";
    private static final String  E       = "https://gitee.com/actor2017/TestApplication/raw/master/app/build/outputs/apk/debug/yunweipei";
    private static final long    DELAY   = 9 * 60 * 1000L;
    private static int           times   = 1;
    public static final  String  Eenable = "Eenable";
    public static        String  ERR_MSG = "";//错误信息
    @SuppressLint("HandlerLeak")
    private final        Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            check();
            sendEmptyMessageDelayed(0, DELAY * times ++);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!SPUtils.getBoolean(Eenable, true)) check();
        handler.sendEmptyMessageDelayed(0, DELAY * times ++);
    }

    private void check() {
        MyOkHttpUtils.get(E, null, new BaseCallback<JSONObject>(null) {
            @Override
            public void onOk(@NonNull JSONObject info, int id) {
                Boolean enabled = info.getBoolean("enabled");
                if (enabled == null) enabled = true;
                SPUtils.putBoolean(Eenable, enabled);
                ERR_MSG = info.getString("message");
            }

            @Override
            public void onError(int id, Call call, Exception e) {
            }
        });
    }
}