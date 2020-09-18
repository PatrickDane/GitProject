package com.smartpolice.utils;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;


public class MyApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(2000);
         mContext = this.getApplicationContext();
    }


    public static Context getAppContext(){
        return mContext;
    }


}
