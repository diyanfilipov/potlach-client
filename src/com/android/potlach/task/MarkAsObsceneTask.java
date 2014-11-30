package com.android.potlach.task;

import android.os.AsyncTask;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.GiftObsceneStatus;
import com.android.potlach.entity.GiftTouchStatus;
import com.android.potlach.ui.ViewGiftActivity;

import java.lang.ref.WeakReference;

/**
 * Created by diyanfilipov on 11/18/14.
 */
public class MarkAsObsceneTask extends AsyncTask<Void, Void, GiftObsceneStatus> {
    private static final String TAG = MarkAsObsceneTask.class.getSimpleName();
    private long giftId;
    private String username;
    private String password;
    private String clientId;

    private WeakReference<ViewGiftActivity> mActivity;

    public MarkAsObsceneTask(long giftId, String username, String password, String clientId, ViewGiftActivity activity){
        this.giftId = giftId;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.mActivity = new WeakReference<ViewGiftActivity>(activity);
    }

    @Override
    protected GiftObsceneStatus doInBackground(Void... voids) {
        PotlachSvcApi writePotlachApi = ClientUtils.createWritePolachApi(username, password, clientId);
        try {
            GiftObsceneStatus giftObsceneStatus = writePotlachApi.obsceneStatus(giftId);
            if(giftObsceneStatus.getObsceneState() == GiftObsceneStatus.ObsceneState.UNMARKED){
                giftObsceneStatus = writePotlachApi.obscene(giftId);
            }
            return giftObsceneStatus;
        }catch (Exception e){ //cannot touch gift twice
            Log.e(TAG, "Gift with id=" + giftId + " cannot be touched twice.");
        }
        return null;
    }

    @Override
    protected void onPostExecute(GiftObsceneStatus giftObsceneStatus) {
        ViewGiftActivity activity = mActivity.get();
        if(activity != null){
            activity.refreshGiftObsceneStatus(giftObsceneStatus);
        }
    }

}
