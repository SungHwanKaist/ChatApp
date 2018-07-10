package com.example.q.chatapp;

import com.hems.socketio.client.ChatList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    private static final String TAG = "FragmentAdapter";

    public FragmentAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount=tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        if(position > this.getCount() || position < 0)
            Log.d(TAG, "Error On Setting Fragment");

        switch (position) {
            case 0 :
                return new ChatList();
            case 1 :
                return new ChatList();
//            return new GridViewFragment();
            case 2 :
                return new ChatList();
//                return new Temp();
            default:
                Log.e("MainFragmentAdapter : ", "CANNOT GET FRAGMENTS");
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
