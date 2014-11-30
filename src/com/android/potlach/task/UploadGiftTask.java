package com.android.potlach.task;

import android.os.AsyncTask;
import android.util.Log;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.Gift;
import com.android.potlach.entity.GiftStatus;
import com.android.potlach.ui.CreateGiftActivity;

import java.io.File;
import java.lang.ref.WeakReference;

import retrofit.mime.TypedFile;

public class UploadGiftTask extends AsyncTask<Void, Void, Gift>{
	private String username;
	private String password;
	private String clientId;
    private String giftChain;
    private Gift gift;
    private File imageFile;
    
    /**
     * Allows Activity to be garbage collected properly.
     */
    private WeakReference<CreateGiftActivity> mActivity;
	
	public UploadGiftTask(String username, String password, String clientId, String giftChain, Gift gift, File imageFile, CreateGiftActivity activity) {
		super();
		this.username = username;
		this.password = password;
		this.clientId = clientId;
        this.giftChain = giftChain;
        this.gift = gift;
        this.imageFile = imageFile;
		this.mActivity = new WeakReference<CreateGiftActivity>(activity);
	}

	protected void onPreExecute() {
        /**
         * Show the progress dialog before starting the upload
         * in a Background Thread.
         */
        final CreateGiftActivity activity = mActivity.get();
        if(activity != null){
        	activity.showDialog("Posting Gift to Cloud");
        }
    }

	@Override
	protected Gift doInBackground(Void... params) {
        PotlachSvcApi potlachSvcApi = ClientUtils.createWritePolachApi(username, password, clientId);
//        PotlachSvcApi potlachSvcApi = ClientUtils.createWritePolachApi(username, password, clientId);

        Gift received = potlachSvcApi.postGift(gift, giftChain);
        if(received != null){
            GiftStatus status = potlachSvcApi.addGiftImage(received.getId(), new TypedFile("image/jpeg", imageFile));
            Log.d("Task", status.getState().name());
        }
        return received;
	}

	@Override
	protected void onPostExecute(Gift result) {
        final CreateGiftActivity activity = mActivity.get();
        if(activity != null){
            Log.d("Test", "Dismissing CreateGiftActivity");
            activity.dismissDialog();
            activity.finish();
        }

	}
}
