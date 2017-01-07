package com.common.widget.comment.brow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.common.widget.R;

import java.util.List;

/**
 * Created by zzq on 2016/10/4.
 */
class BrowGridAdapter extends BaseAdapter {
    private final List<BrowEntity> list;

    BrowGridAdapter(List<BrowEntity> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context c = parent.getContext();
        if (convertView == null) {
            convertView = new ImageView(c);
            int bound = (int) c.getResources().getDimension(R.dimen.space_size_48);
            int padding = (int) c.getResources().getDimension(R.dimen.space_size_8);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(bound, bound);
            convertView.setLayoutParams(params);
            convertView.setPadding(padding, padding, padding, padding);
        }
        Glide.with(c).load(list.get(position).getResId()).into((ImageView) convertView);
        return convertView;
    }
}