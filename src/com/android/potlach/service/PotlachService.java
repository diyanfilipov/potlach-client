package com.android.potlach.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;

import static com.android.potlach.util.Constants.GIFTS_IDS_KEY;
import static com.android.potlach.util.Constants.MESSENGER_KEY;
import static com.android.potlach.util.Constants.UPDATE_PERIOD_KEY;

/**
 * Created by diyanfilipov on 11/12/14.
 */
public class PotlachService extends Service {
    private static final String TAG = PotlachService.class.getSimpleName();

    /**
     * A class constant that determines the maximum number of threads
     * used to service download requests.
     */
    private static final int MAX_THREADS = 4;

    /**
     * The ExecutorService that references a ThreadPool.
     */
//    ExecutorService mExecutor;
    private static final Object lock = new Object();
    private Timer timer;

    private static Map<Long, Integer> giftsTouchesMap = Maps.newConcurrentMap();

    private Messenger messenger;

    public class PotlachServiceBinder extends Binder {
        public PotlachService getPotlachService(){
            return PotlachService.this;
        }
    }

    private final IBinder mBinder = new PotlachServiceBinder();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
//        mExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        Log.d(TAG, new Date(System.currentTimeMillis()).toString());

        final PotlachErrorHandler errorHandler = new PotlachErrorHandler();

        final PotlachSvcApi potlachSvcApi = new RestAdapter.Builder()
                .setClient(ClientUtils.getClient())
                .setEndpoint(ClientUtils.TEST_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(errorHandler)
                .build()
                .create(PotlachSvcApi.class);

        if(intent != null) {
            Bundle args = intent.getExtras();
            messenger = (Messenger) args.get(MESSENGER_KEY);
            int updatePeriod = args.getInt(UPDATE_PERIOD_KEY);
            long[] ids = args.getLongArray(GIFTS_IDS_KEY);
            if (ids != null) {
                Log.d(TAG, "Timer set for " + (updatePeriod/(60*1000) + "min(s)"));
                Log.d(TAG, "will update gifts touches count for " + Arrays.toString(ids));
                for (int i = 0; i < ids.length; i++) {
                    if (!giftsTouchesMap.containsKey(ids[i])) {
                        giftsTouchesMap.put(ids[i], 0);
                    }
                }

                if(timer != null){
                    timer.cancel();
                }
                timer = new Timer();
                timer.scheduleAtFixedRate(
                        new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(TAG, new Date(System.currentTimeMillis()).toString());
                                if(intent != null) {
                                    Map<Long, Integer> mapResult = new HashMap<Long, Integer>();
                                    for(Map.Entry<Long, Integer> entry : giftsTouchesMap.entrySet()){
                                        int touches = potlachSvcApi.getTouchesCount(entry.getKey());
                                        mapResult.put(entry.getKey(), touches);
                                    }
                                    taskFinished(mapResult);
                                }
                            }
                        },
                        0,
                        updatePeriod
                );
            } else {
                Log.d(TAG, "will update nothing");
            }
        }



        return Service.START_REDELIVER_INTENT;
    }

//    public void start(final Intent intent){
//        final PotlachErrorHandler errorHandler = new PotlachErrorHandler();
//
//        final PotlachSvcApi potlachSvcApi = new RestAdapter.Builder()
//                .setClient(ClientUtils.getClient())
//                .setEndpoint(ClientUtils.TEST_URL)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .setErrorHandler(errorHandler)
//                .build()
//                .create(PotlachSvcApi.class);
//
//        if(intent != null) {
//            Bundle args = intent.getExtras();
//            messenger = (Messenger) args.get("MESSENGER");
//            long[] ids = args.getLongArray("GIFTS_IDS");
//            if (ids != null) {
//
//                Log.d(TAG, "will update gifts touches count for " + Arrays.toString(ids));
//                for (int i = 0; i < ids.length; i++) {
//                    if (!giftsTouchesMap.containsKey(ids[i])) {
//                        giftsTouchesMap.put(ids[i], 0);
//                    }
//                }
//            } else {
//                Log.d(TAG, "will update nothing");
//            }
//        }
//
//        if(timer != null){
//            timer.cancel();
//        }
//        timer = new Timer();
//        timer.scheduleAtFixedRate(
//                new TimerTask() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, new Date(System.currentTimeMillis()).toString());
//                        if(intent != null) {
//                            Map<Long, Integer> mapResult = new HashMap<Long, Integer>();
//                            for(Map.Entry<Long, Integer> entry : giftsTouchesMap.entrySet()){
//                                int touches = potlachSvcApi.getTouchesCount(entry.getKey());
//                                mapResult.put(entry.getKey(), touches);
//                            }
//                            taskFinished(mapResult);
//                        }
//                    }
//                },
//                0,
//                10000
//        );
//    }

    public void taskFinished(Map<Long, Integer> map){
        synchronized (lock) {
            Message msg = Message.obtain();
            Bundle data = new Bundle();
            for (Map.Entry<Long, Integer> entry : map.entrySet()) {
                data.putInt("GIFT_" + entry.getKey(), entry.getValue());
            }

            // Make the Bundle the "data" of the Message.
            msg.setData(data);

            try {
                // Send the Message back to the client Activity.
                messenger.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Message data sending failed. " + e.getMessage());
//                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        if(timer != null) {
            timer.cancel();
        }
        timer = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
}
