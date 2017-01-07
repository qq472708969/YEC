package com.yy.yec.entity.detail;

import com.yy.yec.entity.common.Author;

/**
 * 动态赞和取消赞
 * Created by zzq on 2016/10/25.
 */
public class ReverseTweetLike {
    private Author author;
    private boolean liked;
    private long likeCount;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
}