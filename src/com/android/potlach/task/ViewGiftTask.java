package com.android.potlach.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.Gift;
import com.android.potlach.entity.GiftTouches;
import com.android.potlach.ui.ViewGiftActivity;
import com.android.potlach.util.BitmapUtils;
import com.android.potlach.util.StorageUtils;

import java.lang.ref.WeakReference;

/**
 * Created by diyanfilipov on 11/8/14.
 */
public class ViewGiftTask extends AsyncTask<Void, Void, Gift> {
    private static final String TAG = ViewGiftTask.class.getSimpleName();
    private long giftId;
    private String username;
    private boolean isTouchedByUser;
    private boolean isObscene;
    private Bitmap bitmap;

    private WeakReference<ViewGiftActivity> mActivity;

    public ViewGiftTask(long giftId, String username, ViewGiftActivity activity){
        this.giftId = giftId;
        this.username = username;
        this.mActivity = new WeakReference<ViewGiftActivity>(activity);
    }

    @Override
    protected Gift doInBackground(Void... voids) {
        Log.d(TAG, "Start retrieving Gift data...");

        PotlachErrorHandler errorHandler = new PotlachErrorHandler();
        PotlachSvcApi potlachSvcApi = ClientUtils.createReadPotlachApi(errorHandler);
        Gift gift = potlachSvcApi.getGiftById(giftId);
        if(gift != null){

            bitmap = BitmapUtils.decodeSampledBitmapFromFile(
                        StorageUtils.getOutputMediaFile("IMG_" + giftId).getAbsolutePath(),
                        500,
                        500,
                        false);
            if(username != null) {
                GiftTouches giftTouchesByUser = potlachSvcApi.getTouchesByUser(giftId, username);
                if (giftTouchesByUser != null) {
                    isTouchedByUser = true;
                }
            }else{
                isTouchedByUser = true;
            }

        }
        return gift;
    }

    @Override
    protected void onPostExecute(Gift gift) {
        ViewGiftActivity activity = mActivity.get();
        if(activity != null){
            activity.displayGift(gift, isTouchedByUser, bitmap);
        }
    }
}
