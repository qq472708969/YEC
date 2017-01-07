package com.yy.yec.ui.activity.basic;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.yy.yec.R;
import com.yy.yec.ui.base.BaseActivity;
import com.yy.yec.ui.widget.BadgeView;
import com.yy.yec.ui.widget.SimpleFragmentTabHost;
import com.yy.yec.global.App;
import com.yy.yec.ui.enumerate.MenuTab;
import com.yy.yec.ui.widget.TipDialog;
import com.yy.yec.utils.UIUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnTouchListener, TabHost.OnTabChangeListener {
    private CharSequence mTitle;
    @Bind(android.R.id.tabhost)
    SimpleFragmentTabHost mSTabHost;//不能为private
    private BadgeView msgTag;

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    private void initView() {
        initNav();
    }

    private void initNav() {
        mTitle = getTitle();
        mSTabHost.setup(this, getSupportFragmentManager(), R.id.fl_tabContent);//指定tableHost的manager。
        mSTabHost.getTabWidget().setShowDividers(0);//取消分割线
        MenuTab currTab;
        MenuTab[] mTs = MenuTab.values();
        TabHost.TabSpec ts;
        View indicator;
        ImageView topImg;
        TextView title;
        for (int i = 0; i < mTs.length; i++) {
            currTab = mTs[i];//当前Tab
            ts = mSTabHost.newTabSpec(getString(currTab.getResName()));
            if (i == 2) {
                indicator = UIUtil.inflate(this, R.layout.tab_empty_indicator);//中间的加号特殊处理
                mSTabHost.setNoTabChangedTag(getString(currTab.getResName()));
            } else {
                indicator = UIUtil.inflate(this, R.layout.tab_indicator);//底部导航按钮
                topImg = (ImageView) indicator.findViewById(R.id.iv_icon);
                topImg.setImageDrawable(App.resources().getDrawable(currTab.getResImgId()));
                title = (TextView) indicator.findViewById(R.id.tab_title);
                title.setText(currTab.getResName());
            }
            ts.setIndicator(indicator);
            mSTabHost.addTab(ts, currTab.getC(), null);
            if (currTab.equals(MenuTab.MY)) {//如果是Tab的选项是“我的”添加右上角小红点提示
                View redTag = indicator.findViewById(R.id.tab_mes);
                msgTag = new BadgeView(MainActivity.this, redTag);
                msgTag.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                msgTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                msgTag.setBackgroundResource(R.drawable.msg_tag);
                msgTag.setGravity(Gravity.CENTER);
            }
            mSTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
        mSTabHost.setCurrentTab(0);//停留到第一个
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onTabChanged(String tabId) {

    }

    @OnClick(R.id.iv_add)
    public void iv_addClick(View v) {
        TipDialog d = new TipDialog(this);
        d.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.context().appExit();
    }
}