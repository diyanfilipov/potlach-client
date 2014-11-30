package com.android.potlach.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.Gift;
import com.android.potlach.security.SecurityUtils;
import com.android.potlach.ui.MainActivity;
import com.android.potlach.ui.fragment.GiftsFragment;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by diyanfilipov on 10/29/14.
 */
public class ListGiftsTask extends AsyncTask<Integer, Void, List<Gift>> {
    private static final String TAG = ListGiftsTask.class.getSimpleName();

    private String uploader;
    private String giftChain;
    private boolean overrideData;

    //private Map<Integer, GiftImageData> images = new HashMap<Integer, GiftImageData>();

    /**
     * Allows Activity to be garbage collected properly.
     */
    private WeakReference<GiftsFragment> mActivity;

    public ListGiftsTask(String giftChain, boolean overrideData, GiftsFragment mActivity) {
//        this.uploader = uploader;
        this.giftChain = giftChain;
        this.overrideData = overrideData;
        this.mActivity = new WeakReference<GiftsFragment>(mActivity);
    }

    @Override
    protected List<Gift> doInBackground(Integer... params) {
        Log.d(TAG, "Start retrieving Gifts\' images");
        int start = params[0];
        int end = params[1];

        PotlachErrorHandler errorHandler = new PotlachErrorHandler();

        PotlachSvcApi potlachSvcApi = ClientUtils.createReadPotlachApi(errorHandler);

        GiftsFragment fragment = mActivity.get();
        if(fragment != null){
            final List<String> accountInfo = SecurityUtils.getAccountInfo(mActivity.get().getActivity());
            if(accountInfo != null){
                try {
                    if (giftChain == null) {
                        return Lists.newArrayList(potlachSvcApi.findByUploader(accountInfo.get(0), start, end, MainActivity.showObscene));
                    } else {
                        Log.d(TAG, String.valueOf(MainActivity.showObscene));
                        return Lists.newArrayList(potlachSvcApi.getGiftsByChain(giftChain, start, end, MainActivity.showObscene));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(giftChain != null){
                return Lists.newArrayList(potlachSvcApi.getGiftsByChain(giftChain, start, end, MainActivity.showObscene));

            }
        }
        return Lists.newArrayList();
    }

    @Override
    protected void onPostExecute(List<Gift> gifts) {
        GiftsFragment fragment = mActivity.get();
        if(fragment != null){
            fragment.displayGifts(gifts, overrideData);
        }

    }
}
