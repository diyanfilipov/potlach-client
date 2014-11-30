package com.android.potlach.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.cloud.client.UnsafeHttpsClient;
import com.android.potlach.util.BitmapUtils;
import com.android.potlach.util.StorageUtils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import retrofit.RestAdapter;
import retrofit.client.ApacheClient;
import retrofit.client.Response;

/**
 * Created by diyanfilipov on 11/4/14.
 */
public class BitmapWorkerTask extends AsyncTask<Long, Void, Bitmap> {
    private static final String TAG = BitmapWorkerTask.class.getSimpleName();

    private WeakReference<ImageView> mImageView;

    public BitmapWorkerTask(ImageView imageView){
        this.mImageView = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Long... params) {
        long giftId = params[0];
        try {
            PotlachErrorHandler errorHandler = new PotlachErrorHandler();
            final PotlachSvcApi potlachSvcApi = new RestAdapter.Builder()
                    .setClient(ClientUtils.getClient())
                    .setEndpoint(ClientUtils.TEST_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setErrorHandler(errorHandler)
                    .build()
                    .create(PotlachSvcApi.class);

            Response response = potlachSvcApi.getGiftImage(giftId);
            if(response.getStatus() == 200){
                String imageFileName = "IMG_" + giftId;
                File mediaStorage = StorageUtils.getMediaStorage("Potlach");
                if(mediaStorage != null) {
                    File imageFile = new File(mediaStorage.getPath(), imageFileName + ".jpg");
                    InputStream in = response.getBody().in();
                    byte[] data = IOUtils.toByteArray(in);

//                    if (!imageFile.exists()) {
                        try {
                            FileOutputStream fos = new FileOutputStream(imageFile);
                            fos.write(data);
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
//                    }

                    final Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromByteArray(data, 75, 75);
                    BitmapUtils.addBitmapToMemoryCache(giftId, bitmap);
                    return bitmap;
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
//                    return bitmap;
                }





            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error occurred while obtaining image data for Gift with id = " + giftId);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView view = mImageView.get();
        if(view != null){
            view.setImageBitmap(bitmap);
        }
    }
}
