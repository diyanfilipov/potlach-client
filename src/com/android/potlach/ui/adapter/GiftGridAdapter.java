package com.android.potlach.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.potlach.R;
import com.android.potlach.cloud.client.ClientUtils;
import com.android.potlach.cloud.client.PotlachErrorHandler;
import com.android.potlach.cloud.client.PotlachSvcApi;
import com.android.potlach.entity.Gift;
import com.android.potlach.task.BitmapWorkerTask;
import com.android.potlach.util.BitmapUtils;
import com.android.potlach.util.GiftUtils;
import com.android.potlach.util.StorageUtils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by diyanfilipov on 10/29/14.
 */
public class GiftGridAdapter extends BaseAdapter {
    private static final String TAG = GiftGridAdapter.class.getSimpleName();

    private Context context;
    private List<Gift> gifts;

    public GiftGridAdapter(Context context, List<Gift> gifts){
        this.context = context;
        this.gifts = gifts;
    }

    @Override
    public int getCount() {
        return gifts.size();
    }

    @Override
    public Object getItem(int position) {
        return gifts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return gifts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if(convertView == null){
//            //grid = new View(context);
//            grid = inflater.inflate(R.layout.gift_single, null);
//        }else{
//            grid = convertView;
//        }
        grid = inflater.inflate(R.layout.gift_single, null);
        final ImageView giftImage = (ImageView) grid.findViewById(R.id.gift_image);
        TextView giftTitle = (TextView) grid.findViewById(R.id.gift_title);
        TextView giftTouches = (TextView) grid.findViewById(R.id.gift_touches);

        final Gift gift = gifts.get(position);
//        Log.d(TAG, "Gifts size=" + gifts.size() + ". Getting Gift at position " + position + " " + gift);

        final Bitmap bitmap = BitmapUtils.getBitmapFromMemCache(gift.getId());
        if(bitmap == null){
            Log.d(TAG, "Start new BitmapWorkerTask for Gift with id=" + gift.getId());
            BitmapWorkerTask task = new BitmapWorkerTask(giftImage);
            task.execute(gift.getId());
        }else{
            Log.d(TAG, "Bitmap for Gift with id=" + gift.getId() + " loaded from cache.");
            giftImage.setImageBitmap(bitmap);
        }

        giftTitle.setText(gift.getTitle() + "[" + gift.getId() + "]");
        giftTouches.setText(String.valueOf(gift.getTouches()));
        if(gift.isObscene()){
            grid.setBackgroundColor(grid.getResources().getColor(R.color.red));
            giftTitle.setTextColor(grid.getResources().getColor(R.color.black));
            giftTouches.setTextColor(grid.getResources().getColor(R.color.black));
        }

        return grid;
    }

    public void onDataChanged(List<Gift> gifts, boolean override){
        if(override){
            this.gifts = gifts;
        }else {
            for(Gift gift : gifts){
                if(!this.gifts.contains(gift)){
                    this.gifts.add(gift);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateGiftTouches(int position, int touches){
        gifts.get(position).setTouches(touches);
    }
}
