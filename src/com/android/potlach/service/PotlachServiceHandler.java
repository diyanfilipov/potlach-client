package com.android.potlach.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.potlach.ui.fragment.GiftsFragment;

import java.lang.ref.WeakReference;

/**
 * Created by diyanfilipov on 11/16/14.
 */
public class PotlachServiceHandler extends Handler{
    private static final String TAG = PotlachServiceHandler.class.getSimpleName();

    private WeakReference<GiftsFragment> mFragment;

    public PotlachServiceHandler(GiftsFragment fragment){
        this.mFragment = new WeakReference<GiftsFragment>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {

        final GiftsFragment fragment = mFragment.get();
        if(fragment != null){
            final Bundle data = msg.getData();
            Log.d(TAG, "handleMessage with data " + data);
            fragment.processMessageBundleData(data);
        }
        super.handleMessage(msg);
    }
}
