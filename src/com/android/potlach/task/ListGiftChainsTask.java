package com.android.potlach.task;

import android.os.AsyncTask;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.Gift;
import com.android.potlach.ui.CreateGiftActivity;
import com.android.potlach.ui.GiftChainContainer;
import com.android.potlach.ui.MainActivity;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by diyanfilipov on 11/6/14.
 */
public class ListGiftChainsTask extends AsyncTask<Void, Void, List<String>> {
    private WeakReference<GiftChainContainer> mActivity;

    public ListGiftChainsTask(GiftChainContainer activity){
        this.mActivity = new WeakReference<GiftChainContainer>(activity);
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        PotlachErrorHandler errorHandler = new PotlachErrorHandler();
        PotlachSvcApi potlachSvcApi = ClientUtils.createReadPotlachApi(errorHandler);
        Collection<Gift> allGiftChains = potlachSvcApi.getAllGiftChains();
        if(allGiftChains != null){
            List<String> titles = new ArrayList<String>();
            for(Gift gift : allGiftChains){
                titles.add(gift.getTitle());
            }
            return Lists.newArrayList(titles);
        }
        return Lists.newArrayList();
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        GiftChainContainer activity = mActivity.get();
        if(activity != null){
            activity.addGiftChains(strings);
        }
    }
}
