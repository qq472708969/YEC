package com.yy.yec.entity.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by zzq on 2016/11/8.
 */
@XStreamAlias("oschina")
public class LoginUser extends Entity {

    @XStreamAlias("result")
    private Result result;

    @XStreamAlias("user")
    private User user;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}