package com.android.potlach.task;

import android.os.AsyncTask;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.Gift;
import com.android.potlach.entity.GiftTouchStatus;
import com.android.potlach.ui.ViewGiftActivity;

import java.lang.ref.WeakReference;

/**
 * Created by diyanfilipov on 11/8/14.
 */
public class TouchGiftTask extends AsyncTask<Void, Void, GiftTouchStatus> {
    private static final String TAG = TouchGiftTask.class.getSimpleName();
    private long giftId;
    private String username;
    private String password;
    private String clientId;
    private int touchCount;

    private WeakReference<ViewGiftActivity> mActivity;

    public TouchGiftTask(long giftId, String username, String password, String clientId, ViewGiftActivity activity){
        this.giftId = giftId;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.mActivity = new WeakReference<ViewGiftActivity>(activity);
    }

    @Override
    protected GiftTouchStatus doInBackground(Void... voids) {
        PotlachSvcApi writePotlachApi = ClientUtils.createWritePolachApi(username, password, clientId);
        try {
            touchCount = writePotlachApi.getTouchesCount(giftId);
            GiftTouchStatus giftTouchStatus = writePotlachApi.touch(giftId);
            touchCount = writePotlachApi.getTouchesCount(giftId);
            return giftTouchStatus;
        }catch (Exception e){ //cannot touch gift twice
            Log.e(TAG, "Gift with id=" + giftId + " cannot be touched twice.");
        }
        return new GiftTouchStatus(GiftTouchStatus.TouchState.UNTOUCHED);
    }

    @Override
    protected void onPostExecute(GiftTouchStatus giftTouchStatus) {
        ViewGiftActivity activity = mActivity.get();
        if(activity != null){
            activity.refreshGiftTouch(giftTouchStatus, touchCount);
        }
    }
}
