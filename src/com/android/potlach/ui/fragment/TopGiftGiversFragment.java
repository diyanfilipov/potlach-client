package com.android.potlach.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.potlach.R;
import com.android.potlach.task.ListGiftsTask;
import com.android.potlach.task.TopGiftGiversTask;
import com.android.potlach.ui.adapter.TopGiftGiversListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diyanfilipov on 11/7/14.
 */
public class TopGiftGiversFragment extends PotlachFragment{
    private static final String TAG = TopGiftGiversFragment.class.getSimpleName();
    public static boolean loadNewDataOnCreate = true;

    private TopGiftGiversListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_top_gift_givers, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        final ListView listView = (ListView) view.findViewById(R.id.top_gift_givers_list_view);
        if(listAdapter == null){
            listAdapter = new TopGiftGiversListAdapter(getActivity(), new ArrayList<Object[]>());
        }
        listView.setAdapter(listAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && !refreshing && !loadedAllData) {
                    // End has been reached
//                    Toast.makeText(getActivity(), "END REACHED", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "ListView end reached.");
                    performRefresh(true, false);
                }
            }
        });

        performRefresh(loadNewDataOnCreate, !loadNewDataOnCreate);
        loadNewDataOnCreate = true;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        performRefresh(false, true);
    }

    @Override
    public void onRefresh() {
        performRefresh(false, true);
    }

    private void performRefresh(boolean loadNewData, boolean overrideData){
        if(loadNewData && !refreshing){
            startTopGiftGiversTask(overrideData, currentPage, 15);
            currentPage += 1;
        }else if(!refreshing) {
            if(listAdapter.getCount() == 0){
                startTopGiftGiversTask(overrideData, currentPage, 15);
                currentPage += 1;
            }else{
                startTopGiftGiversTask(overrideData, 0, listAdapter.getCount() + lastAddedCount);
            }
        }
    }

    private void startTopGiftGiversTask(boolean overrideData, int page, int size){
        Log.d(TAG, "Start loading top Gifts givers. page = " + page + " size=" + size);
        refreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        new TopGiftGiversTask(overrideData, this).execute(page, size);
    }

    public void displayTopGiftGivers(List<Object[]> topGiftGiversList, boolean overrideData) {
        Log.d(TAG, "Loading finished. Refreshing List with " + topGiftGiversList.size() + " items.");
        if(!topGiftGiversList.isEmpty()) {
            listAdapter.onDataChanged(topGiftGiversList, overrideData);
            lastAddedCount = topGiftGiversList.size();
            loadedAllData = false;

//            set.addAll(gifts);

//            startService();
        }else{
            loadedAllData = true;
        }
        refreshing = false;
        swipeRefreshLayout.setRefreshing(false);
    }
}
