package com.konka.ipctest;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.List;

/**
 * Created by HwanJ.Choi on 2018-8-1.
 */
public class App extends Application {

    private static App sInstance;
    private int id;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("chj", "Application onCreate" + toString());
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        Log.d("chj", "myPid:" + Process.myPid());
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            Log.d("chj", "pid:" + info.pid + ",process:" + info.processName);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
    }

    public static App getInstance() {
        return sInstance;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
}
