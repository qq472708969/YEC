package com.yy.yec.entity.common;

import java.io.Serializable;

/**
 * Created by zzq on 2016/10/29.
 */
public class UserRelation implements Serializable {
    private int relation;
    private String author;
    private long authorId;

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }
}
