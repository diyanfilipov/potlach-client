package com.android.potlach.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.potlach.R;
import com.android.potlach.entity.Gift;
import com.android.potlach.task.SearchGiftsTask;
import com.android.potlach.ui.adapter.GiftGridAdapter;
import com.android.potlach.ui.fragment.GiftsFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
	private static final String TAG = SearchResultsActivity.class.getSimpleName();


    private TextView txtQuery;

    private GiftGridAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentPage = 0;
    private int lastAddedCount = 0;
    private boolean refreshing;
    private boolean loadedAllData;

    private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		// get the action bar
//		ActionBar actionBar = getActionBar();

		// Enabling Back navigation on Action Bar icon
//		actionBar.setDisplayHomeAsUpEnabled(true);



//		txtQuery = (TextView) findViewById(R.id.txtQuery);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
//		setIntent(intent);
//		handleIntent(intent);
	}

	/**
	 * Handling intent data
	 */
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);

            getActionBar().setTitle(query);

            final GridView gridView = (GridView) findViewById(R.id.grid);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorScheme(
                    android.R.color.holo_green_dark,
                    android.R.color.holo_red_dark,
                    android.R.color.holo_blue_dark,
                    android.R.color.holo_orange_dark);

            if(adapter == null) {
                adapter = new GiftGridAdapter(this, new ArrayList<Gift>());
            }
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Clicked on position=" + position);
                    Intent viewGiftIntent = new Intent();
                    viewGiftIntent.setClass(SearchResultsActivity.this, ViewGiftActivity.class);
                    viewGiftIntent.putExtra("giftId", adapter.getItemId(position));
                    startActivity(viewGiftIntent);

                }
            });

            gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.d(TAG, String.valueOf(gridView.getHeight() + " " + view.getBottom()));
//                View lastGridChild = gridView.getChildAt(gridView.getChildCount() - 1);
//                if(lastGridChild != null) {
//                    int diff = (lastGridChild.getBottom() - (view.getHeight() + view.getScrollY()));
//                    if (diff == 0 && !refreshing && !loadedAllData) {
//                        Log.d(TAG, "GridView end reached.");
//                        performRefresh(true, false);
//                    }
//                }
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && !refreshing && !loadedAllData) {
                        // End has been reached
//                    Toast.makeText(getActivity(), "END REACHED", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "GridView end reached.");
                        performRefresh(true, false);
                    }
                }
            });

            performRefresh(true, false);
		}
	}


    @Override
    public void onRefresh() {
        performRefresh(false, true);
    }

    private void performRefresh(boolean loadNewData, boolean overrideGifts){
        if(loadNewData && !refreshing){
            startListGiftTask(overrideGifts, currentPage, 15);
//            new ListGiftsTask("admin", null, overrideGifts, this).execute(currentPage, 15);
            currentPage += 1;
        }else if(!refreshing) {
            if(adapter.getCount() == 0){
                startListGiftTask(overrideGifts, currentPage, 15);
//                new ListGiftsTask("admin", null, overrideGifts, this).execute(currentPage, 15);
                currentPage += 1;
            }else{
                startListGiftTask(overrideGifts, 0, adapter.getCount() + lastAddedCount);
//                new ListGiftsTask("admin", null, overrideGifts, this).execute(0, adapter.getCount() + lastAddedCount);
            }
        }
    }

    private void startListGiftTask(boolean overrideGifts, int page, int size){
        refreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        new SearchGiftsTask(query, overrideGifts, this).execute(page, size);

    }

    public void displaySearchResults(List<Gift> gifts, boolean overrideData) {
        if(!gifts.isEmpty()) {
            Log.d(TAG, "Searching finished. Refreshing Grid with " + gifts.size() + " items.");
            adapter.onDataChanged(gifts, overrideData);
            lastAddedCount = gifts.size();
            loadedAllData = false;
            if(lastAddedCount < 15){
                loadedAllData = true;
            }
        }else{
            loadedAllData = true;
        }
        refreshing = false;
        swipeRefreshLayout.setRefreshing(false);
    }
}
