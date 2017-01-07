package com.yy.yec.entity.detail;

import com.yy.yec.entity.common.Author;

import java.io.Serializable;

/**
 * Created by zzq on 2016/11/22.
 */
public class TweetLike implements Serializable {
    private String pubDate;
    private Author author;

    public String getPubDate() {
        return pubDate;
    }

    public Author getAuthor() {
        return author;
    }
}
