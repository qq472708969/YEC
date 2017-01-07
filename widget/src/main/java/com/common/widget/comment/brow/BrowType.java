package com.common.widget.comment.brow;

/**
 * Created by zzq on 2016/10/6.
 */
public enum BrowType {
    QQ(0), E(1), Other(2);
    private final int val;

    BrowType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}