package com.yy.yec.entity.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {

    @XStreamAlias("notice")
    protected Notice notice;

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }
}
