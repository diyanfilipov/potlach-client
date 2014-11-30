package com.android.potlach.task;

import android.os.AsyncTask;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.ui.fragment.TopGiftGiversFragment;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by diyanfilipov on 11/23/14.
 */
public class TopGiftGiversTask extends AsyncTask<Integer, Void, List<Object[]>> {
    private static final String TAG = TopGiftGiversTask.class.getSimpleName();

    private boolean overrideData;

    private WeakReference<TopGiftGiversFragment> mTopGiftGiversFragment;

    public TopGiftGiversTask(boolean overrideData, TopGiftGiversFragment topGiftGiversFragment){
        this.overrideData = overrideData;
        this.mTopGiftGiversFragment = new WeakReference<TopGiftGiversFragment>(topGiftGiversFragment);
    }

    @Override
    protected List<Object[]> doInBackground(Integer... params) {
        Log.d(TAG, "Start retrieving top Gifts\' givers");
        int start = params[0];
        int end = params[1];

        PotlachErrorHandler errorHandler = new PotlachErrorHandler();

        PotlachSvcApi potlachSvcApi = ClientUtils.createReadPotlachApi(errorHandler);
        List<Object[]> topGiftGivers = Lists.newArrayList(potlachSvcApi.getTopGiftGivers(start, end));
        if(topGiftGivers != null){
            return topGiftGivers;
        }

        return Lists.newArrayList();
    }

    @Override
    protected void onPostExecute(List<Object[]> topGiftGiversList) {
        TopGiftGiversFragment fragment = mTopGiftGiversFragment.get();
        if(fragment != null){
            fragment.displayTopGiftGivers(topGiftGiversList, overrideData);
        }
    }
}
