package com.yy.yec.entity.detail;

import com.google.gson.annotations.Expose;
import com.yy.yec.entity.common.Comment;
import com.yy.yec.entity.common.About;
import com.yy.yec.entity.common.Software;
import com.yy.yec.entity.common.UserRelation;

import java.util.List;

/**
 * Created by zzq on 2016/10/29.
 */
public class NewsDetail extends News {
    private boolean favorite;
    private long authorId;
    private String authorPortrait;
    private List<About> abouts;
    private Software software;
    @Expose
    private UserRelation userRelation;
    @Expose
    private List<Comment> comments;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPortrait() {
        return authorPortrait;
    }

    public void setAuthorPortrait(String authorPortrait) {
        this.authorPortrait = authorPortrait;
    }

    public List<About> getAbouts() {
        return abouts;
    }

    public void setAbouts(List<About> abouts) {
        this.abouts = abouts;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }


    public UserRelation getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(UserRelation userRelation) {
        this.userRelation = userRelation;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}
