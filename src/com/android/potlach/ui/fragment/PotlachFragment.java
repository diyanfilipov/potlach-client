package com.android.potlach.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.BaseAdapter;

import com.android.potlach.ui.adapter.GiftGridAdapter;

/**
 * Created by diyanfilipov on 11/23/14.
 */
public abstract class PotlachFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout swipeRefreshLayout;

    protected int currentPage = 0;
    protected int lastAddedCount = 0;
    protected boolean refreshing;
    protected boolean loadedAllData;
}
