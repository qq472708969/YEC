package com.yy.yec.able.basic;

/**
 * Created by zzq on 2016/10/27.
 */
public interface DetailContract {
    interface Operator<TData> {
        // 获取当前数据
        TData getData();

        void hideLoading();

        // 回写布局View
//        void setDataView(TView view);

        // 回写顶部评论数
        void setCommentCount(int count);
    }
//可有可无
//    interface View {
//        // 滚动到评论区域
//        void scrollToComment();
//    }
}