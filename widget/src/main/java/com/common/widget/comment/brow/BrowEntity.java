package com.common.widget.comment.brow;

/**
 * Created by zzq on 2016/10/4.
 */
public class BrowEntity {
    private final BrowType type; //图片类别
    private final int resId; //图片资源
    private final String browTagStr; // emoji在互联网传递的字符串
    private final String remote;

    public BrowEntity(BrowType type, int id, String name, String remote) {
        this.type = type;
        this.resId = id;
        this.browTagStr = name;
        this.remote = remote;
    }

    public int getResId() {
        return resId;
    }

    public String getRemote() {
        return remote;
    }

    public BrowType getType() {
        return type;
    }

    public String getBrowTagStr() {
        return browTagStr;
    }
}