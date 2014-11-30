package com.android.potlach.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.Gift;
import com.android.potlach.security.SecurityUtils;
import com.android.potlach.ui.MainActivity;
import com.android.potlach.ui.SearchResultsActivity;
import com.android.potlach.ui.fragment.GiftsFragment;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by diyanfilipov on 11/24/14.
 */
public class SearchGiftsTask extends AsyncTask<Integer, Void, List<Gift>> {
    private static final String TAG = SearchGiftsTask.class.getSimpleName();

    private String query;
    private boolean overrideData;

    private WeakReference<SearchResultsActivity> mActivity;

    public SearchGiftsTask(String query, boolean overrideData, SearchResultsActivity activity){
        this.query = query;
        this.overrideData = overrideData;
        this.mActivity = new WeakReference<SearchResultsActivity>(activity);
    }

    @Override
    protected List<Gift> doInBackground(Integer... params) {
        Log.d(TAG, "Start search task with query=" + query);
        int page = params[0];
        int size = params[1];

        PotlachErrorHandler errorHandler = new PotlachErrorHandler();

        PotlachSvcApi potlachSvcApi = ClientUtils.createReadPotlachApi(errorHandler);

        List<Gift> gifts = Lists.newArrayList(potlachSvcApi.findByTitle(query, page, size, MainActivity.showObscene));
        if(gifts != null){
            return gifts;
        }
        return Lists.newArrayList();
    }

    @Override
    protected void onPostExecute(List<Gift> gifts) {
        SearchResultsActivity activity = mActivity.get();
        if(activity != null){
            activity.displaySearchResults(gifts, overrideData);
        }
    }
}
