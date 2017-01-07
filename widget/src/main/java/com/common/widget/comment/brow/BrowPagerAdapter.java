package com.common.widget.comment.brow;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzq on 2016/10/3.
 */
public class BrowPagerAdapter extends FragmentPagerAdapter {
    private final EditText editText;
    private List<BrowEntity> qqList = null;
    private List<BrowEntity> eList = null;
    private List<BrowEntity> otherList = null;

    public BrowPagerAdapter(FragmentManager fm, List<BrowEntity> list, EditText et) {
        super(fm);
        editText = et;
        qqList = new ArrayList<>();
        eList = new ArrayList<>();
        otherList = new ArrayList<>();
        for (BrowEntity data : list) {
            switch (data.getType()) {
                case QQ:
                    qqList.add(data);
                    break;
                case E:
                    eList.add(data);
                    break;
                case Other:
                    otherList.add(data);
                    break;
            }
        }
    }

    @Override
    public int getCount() {
        return BrowParam.Group;
    }

    @Override
    public BrowFragment getItem(int position) {
        List<BrowEntity> list = null;
        if (position == BrowType.QQ.getVal())
            list = qqList;
        else if (position == BrowType.E.getVal())
            list = eList;
        else if (position == BrowType.Other.getVal())
            list = otherList;
        return new BrowFragment(list, editText);
    }

}