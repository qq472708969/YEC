package com.common.widget.comment.brow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import java.util.List;

/**
 * Created by zzq on 2016/12/4.
 */
@SuppressLint("ValidFragment")
//忽略警告
class BrowFragment extends Fragment {
    private final EditText editText;
    private GridView gv;//表情布局
    private List<BrowEntity> listBrow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        gv = new GridView(getContext());
        gv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {//手指触摸和屏幕停止滑动时调用垃圾回收
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    System.gc();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        gv.setNumColumns(BrowParam.HorizontalNum);//横向格数
        BrowGridAdapter bga = new BrowGridAdapter(listBrow);
        gv.setAdapter(bga);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int start = editText.getSelectionStart();//选中文本的开始索引位置
                int end = editText.getSelectionEnd();//结束位置
                CharSequence charSequence = BrowParam.showBrow(getResources(), listBrow.get(position));
                if (start == end)
                    editText.getText().insert(start, charSequence);//在当前位置插入表情
                else //选中区域替换
                    editText.getText().replace(Math.min(start, end), Math.max(start, end), charSequence, 0, charSequence.length());//如果有选中区域则将选中区域替换为表情
            }
        });
        gv.setSelector(android.R.color.transparent);//选中时无表现
        return gv;
    }

    BrowFragment(List<BrowEntity> list, EditText e) {
        listBrow = list;
        editText = e;
    }
}