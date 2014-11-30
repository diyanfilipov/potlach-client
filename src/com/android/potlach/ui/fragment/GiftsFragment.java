package com.android.potlach.ui.fragment;

import static com.android.potlach.util.Constants.*;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.android.potlach.R;
import com.android.potlach.entity.Gift;
import com.android.potlach.service.PotlachService;
import com.android.potlach.service.PotlachServiceHandler;
import com.android.potlach.task.ListGiftChainsTask;
import com.android.potlach.task.ListGiftsTask;
import com.android.potlach.ui.GiftChainContainer;
import com.android.potlach.ui.ViewGiftActivity;
import com.android.potlach.ui.adapter.GiftGridAdapter;
import com.android.potlach.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by diyanfilipov on 10/25/14.
 */
public class GiftsFragment extends PotlachFragment implements
        PotlachGiftImageDisplayableFragment, GiftChainContainer {
    public static final String TAG = GiftsFragment.class.getSimpleName();
    public static boolean loadNewDataOnCreate = true;
    private static String lastSelectedGiftChain;

    private Spinner spinner;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private GiftGridAdapter adapter;

//    private int currentPage = 0;
//    private int lastAddedCount = 0;
//    private boolean refreshing;
//    private boolean loadedAllData;
    private boolean showGiftChainSpinner;
    private boolean spinnerSelectedInternally;

    private Set<Gift> set = new HashSet<Gift>();
    private Intent serviceIntent;
    private PotlachService potlachService;

    private PotlachServiceHandler potlachServiceHandler;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PotlachService.PotlachServiceBinder binder = (PotlachService.PotlachServiceBinder) iBinder;
            potlachService = binder.getPotlachService();
            Log.d(TAG, "PotlachService connected");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        serviceIntent = new Intent(getActivity(), PotlachService.class);

        Bundle args = getArguments();
        if(args != null){
            Boolean showGiftChainSpinner = args.getBoolean("showGiftChainSpinner");
            this.showGiftChainSpinner = showGiftChainSpinner;
        }

        potlachServiceHandler = new PotlachServiceHandler(this);

        getActivity().bindService(serviceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(showGiftChainSpinner) {
            new ListGiftChainsTask(this).execute();
        }

        startService();
    }

    @Override
    public void onPause() {
        Log.d(TAG + String.valueOf(showGiftChainSpinner), "Stopping service in fragment");
//        getActivity().stopService(serviceIntent);
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        pendingIntent.cancel();
//        alarmManager.cancel(pendingIntent);
        potlachService.stopSelf();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gifts, container, false);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        final GridView gridView = (GridView) view.findViewById(R.id.grid);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        if(adapter == null) {
            adapter = new GiftGridAdapter(getActivity(), new ArrayList<Gift>());
        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Clicked on postion=" + position);
            Intent viewGiftIntent = new Intent();
            viewGiftIntent.setClass(GiftsFragment.this.getActivity(), ViewGiftActivity.class);
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


        if(showGiftChainSpinner){
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(!spinnerSelectedInternally) {
                        lastSelectedGiftChain = spinner.getSelectedItem().toString();
                        currentPage = 0;
                        lastAddedCount = 0;
                        adapter.onDataChanged(new ArrayList<Gift>(), true);
                        performRefresh(loadNewDataOnCreate, !loadNewDataOnCreate);
                        loadNewDataOnCreate = true;
                        Log.d(TAG, "Item selected=" + lastSelectedGiftChain);
                    }
                    spinnerSelectedInternally = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
        }else {
            performRefresh(loadNewDataOnCreate, !loadNewDataOnCreate);
            loadNewDataOnCreate = true;
        }

        return view;
    }

    @Override
    public void displayGifts(List<Gift> gifts, boolean overrideGifts){
        Log.d(TAG, "Loading finished. Refreshing Grid with " + gifts.size() + " items.");
        if(!gifts.isEmpty()) {
            adapter.onDataChanged(gifts, overrideGifts);
            lastAddedCount = gifts.size();
            loadedAllData = false;

            set.addAll(gifts);

            startService();
        }else{
            loadedAllData = true;
        }
        refreshing = false;
        swipeRefreshLayout.setRefreshing(false);
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
                currentPage = 0;
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
        if(showGiftChainSpinner){
            if(!TextUtils.isEmpty(lastSelectedGiftChain)) {
                Log.d(TAG, "Start loading Gifts for chain=" + lastSelectedGiftChain + " page = " + page + " size=" + size);
                refreshing = true;
                swipeRefreshLayout.setRefreshing(true);
                new ListGiftsTask(lastSelectedGiftChain, overrideGifts, this).execute(page, size);
            }
        }else{
            Log.d(TAG, "Start loading Gifts for user. page = " + page + " size=" + size);
            refreshing = true;
            swipeRefreshLayout.setRefreshing(true);
            new ListGiftsTask(null, overrideGifts, this).execute(page, size);
        }

    }

    @Override
    public void addGiftChains(List<String> giftChains){
        if(showGiftChainSpinner) {
            if(giftChains != null){
                final ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this.getActivity(),
                        android.R.layout.simple_list_item_1, giftChains.toArray());
                spinner.setAdapter(adapter);

                for(int i = 0; i < giftChains.size(); i++){
                    if(TextUtils.equals(giftChains.get(i), lastSelectedGiftChain)){
                        onRefresh();
                        spinnerSelectedInternally = true;
                        spinner.setSelection(i);
                        break;
                    }
                }
                spinner.setVisibility(View.VISIBLE);
//                spinner.invalidate();
            }
        }
    }

    public void startService(){
        if(!set.isEmpty()){
            Log.d(TAG + String.valueOf(showGiftChainSpinner), "Starting service from fragment");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int updatePeriod = Integer.parseInt(preferences.getString("pref_updateTouchesInterval", "5"));
            Log.d(TAG, "Update Period=" + updatePeriod + "min(s)");

            Messenger messenger = new Messenger(potlachServiceHandler);
            long[] ids = new long[set.size()];
            int c = 0;
            for(Gift gift : set){
                ids[c++] = gift.getId();
            }

            Bundle args = new Bundle();
            args.putLongArray(GIFTS_IDS_KEY, ids);
            args.putInt(UPDATE_PERIOD_KEY, updatePeriod * 60 * 1000);
            serviceIntent = new Intent(getActivity(), PotlachService.class);
            serviceIntent.putExtra(MESSENGER_KEY, messenger);
            serviceIntent.putExtras(args);

            if(potlachService != null) {
//                potlachService.start(serviceIntent);
                getActivity().startService(serviceIntent);
            }

    //            pendingIntent = PendingIntent.getService(getActivity(), 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    //
    //            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
    //            alarmManager.cancel(pendingIntent);
    //            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
    //                    0, 10000, pendingIntent);

    //            getActivity().startService(serviceIntent);
                //            bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
    //                getActivity().startService(serviceIntent);



        }
    }

    public String getLastSelectedGiftChain(){
        return lastSelectedGiftChain;
    }

//    public void restartService(){
//        if(potlachService != null){
//            potlachService.stopSelf();
//        }
//        startService();
//    }

    public void processMessageBundleData(Bundle data) {
        Log.d(TAG, "Processing Message Bundle data from Service");

        StringBuilder sb = new StringBuilder();
        for(String key : data.keySet()){
            sb.append("key=" + key + ", value=" + data.get(key));
            sb.append("\n");
        }
        Log.d(TAG, sb.toString());
        for(int i = 0; i < adapter.getCount(); i++){
            long giftId = adapter.getItemId(i);
            int touches = data.getInt("GIFT_" + giftId);
            adapter.updateGiftTouches(i, touches);
        }
        adapter.notifyDataSetChanged();
    }

//    public void unBindService() {
//        if(potlachService != null){
//            getActivity().unbindService(mServiceConnection);
//        }
//    }
}
