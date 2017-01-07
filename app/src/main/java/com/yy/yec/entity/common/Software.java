package com.yy.yec.entity.common;

import java.io.Serializable;

/**
 * Created by zzq on 2016/10/29.
 */
public class Software implements Serializable {

    private long id;
    private String name;
    private String href;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
