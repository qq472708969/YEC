package com.yy.yec.adapter.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yy.yec.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzq on 2016/10/9.
 * <p/>
 * 可动态填充ViewHolder的Adapter
 */
public abstract class ListViewAdapter<T> extends BaseAdapter {
    private List<T> mData;

    public ListViewAdapter() {
        mData = new ArrayList<T>();
    }

    public List<T> getList() {
        return mData;
    }

    @Override
    public int getCount() {
        int size = mData.size();
        return size > 0 ? size : 0;
    }

    @Override
    public T getItem(int position) {
        T item = null;
        if (position < mData.size() && position >= 0)
            item = mData.get(position);
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = getItem(position);
        int layoutId = setContentView();
        ViewHolder vh = ViewHolder.getViewHolder(parent.getContext(), convertView, layoutId);
        processHolder(vh, item, parent.getContext(), position);
        return vh.getConvertView();
    }

    protected abstract void processHolder(ViewHolder vh, T item, Context c, int position);

    @LayoutRes
    protected abstract int setContentView();

    public void addItem(T item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int location, T item) {
        mData.add(location, item);//指定索引位置插入
        notifyDataSetChanged();
    }

    public void addItem(List<T> items) {
        mData.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(int location, List<T> items) {
        mData.addAll(location, items);
        notifyDataSetChanged();
    }

    public void removeItem(T item) {
        if (mData.isEmpty()) return;
        mData.remove(item);
        notifyDataSetChanged();
    }

    public void removeItem(int location) {
        if (mData.isEmpty()) return;
        mData.remove(location);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mData.isEmpty()) return;
        mData.clear();
        notifyDataSetChanged();
    }
}