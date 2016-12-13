package com.cyq7on.greendaodemo;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.cyq7on.greendao.DaoMaster;
import com.cyq7on.greendao.DaoSession;

/**
 * Created by cyq7on on 2016/12/9.
 */

public class MyApplication extends Application {
    private static MyApplication context;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        devOpenHelper = new DaoMaster.DevOpenHelper(context,"test_db");
        sqLiteDatabase = devOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(sqLiteDatabase);
        daoSession = daoMaster.newSession();
    }

    public static MyApplication getContext() {
        return context;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }
}
