package com.hems.socketio.client;

import android.app.Application;
import android.content.Context;

public class ChatContext extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ChatContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ChatContext.context;
    }
}