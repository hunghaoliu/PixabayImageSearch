package com.henry.pixabayimagesearch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.henry.pixabayimagesearch.R;

import java.util.ArrayList;

/**
 * Created by henry on 16/12/8.
 */
public class PixabayListAdapter extends BaseAdapter {
    private final ArrayList<String> mUrls;
    private final Context mContext;

    public PixabayListAdapter(Context context, ArrayList<String> urls) {
        mUrls = urls;
        mContext = context;
    }


    @Override
    public int getCount() {
        if (mUrls == null) {
            return 0;
        }

        return mUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_item, viewGroup, false);

            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageDrawable(null);
        holder.title.setText("Loading");

        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        public TextView title;
    }
}
