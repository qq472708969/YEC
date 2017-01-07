package com.yy.yec.ui.widget.comment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yy.yec.R;
import com.yy.yec.entity.common.Comment;
import com.yy.yec.global.App;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.ui.activity.basic.LoginActivity;
import com.yy.yec.ui.factory.DetailActivityFactory;
import com.yy.yec.utils.DeviceUtil;
import com.yy.yec.utils.ToastUtil;
import com.yy.yec.utils.UIUtil;

/**
 * Created by zzq on 2016/9/31.
 */
public class CommentActive {
    /**
     * 是否允许发布评论，（检查当前数据，并检查网络状况，返回当前登录用户Id, 未登录或者未通过检查返回-1）
     * @param c
     * @return UserID
     */
    public static int allowSendComment(Context c) {
        if (!DeviceUtil.internet()) {
            ToastUtil.showToast(R.string.tip_no_internet);
            return -1;
        }
        if (!AppCurrentUser.getInstance().getLoginOK()) {
            DetailActivityFactory.startActivity(c, -1, LoginActivity.class);//跳转登录页面
            return -1;
        }
        // 返回当前登录用户ID
        return AppCurrentUser.getInstance().getLoginUserId();
    }

    public static void addRefer(ViewGroup parent, Comment.Refer refer) {
        ViewGroup lay_referArea = (ViewGroup) UIUtil.inflate(App.context(), R.layout.comment_area_refer);
        TextView tv_area_curr_author = (TextView) lay_referArea.findViewById(R.id.tv_area_curr_author);
        TextView tv_hf = (TextView) lay_referArea.findViewById(R.id.tv_hf);
        TextView tv_comment_suffix = (TextView) lay_referArea.findViewById(R.id.tv_comment_suffix);

        ((TextView) lay_referArea.findViewById(R.id.tv_area_content)).setText(refer.content);
        parent.addView(lay_referArea, 0);

        if (refer.refer != null) {
            tv_comment_suffix.setVisibility(View.GONE);//如果是回复则隐藏后缀-》的评论：
            tv_area_curr_author.setText(refer.author);
            ((TextView) lay_referArea.findViewById(R.id.tv_area_refr)).setText(refer.refer.author);
            addRefer(parent, refer.refer);//递归添加评论内容
        } else {
            tv_area_curr_author.setVisibility(View.GONE);
            tv_hf.setText(R.string.the_landlord);
            ((TextView) lay_referArea.findViewById(R.id.tv_area_refr)).setText(refer.author);
        }
    }
}