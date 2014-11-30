package com.android.potlach.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.potlach.R;
import com.android.potlach.entity.Gift;

import java.util.List;

/**
 * Created by diyanfilipov on 11/20/14.
 */
public class TopGiftGiversListAdapter extends BaseAdapter {
    private static final String TAG = TopGiftGiversListAdapter.class.getSimpleName();

    private Context context;
    private List<Object[]> topGiftGiversList;

    public TopGiftGiversListAdapter(Context context, List<Object[]> topGiftGivers){
        this.context = context;
        this.topGiftGiversList = topGiftGivers;
    }

    @Override
    public int getCount() {
        return topGiftGiversList.size();
    }

    @Override
    public Object getItem(int position) {
        return topGiftGiversList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View listView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            //grid = new View(context);
            listView = inflater.inflate(R.layout.top_gift_givers_single, null);
        }else{
            listView = convertView;
        }

        TextView txtUsername = (TextView) listView.findViewById(R.id.top_gift_giver_name);
        TextView txtTouches = (TextView) listView.findViewById(R.id.top_gift_giver_count);

        Object[] data = topGiftGiversList.get(position);
        txtUsername.setText(String.valueOf(data[1]));
        Double touches = (Double) data[0];
        txtTouches.setText(String.valueOf(touches.intValue()));

//        if(position % 2 == 0){
//            listView.setBackgroundColor(context.getResources().getColor(R.color.gray));
//        }


        return listView;
    }

    public void onDataChanged(List<Object[]> topGiftGiversList, boolean overrideData) {
        if(overrideData){
            this.topGiftGiversList = topGiftGiversList;
        }else {
            this.topGiftGiversList.addAll(topGiftGiversList);
        }
        notifyDataSetChanged();
    }
}
