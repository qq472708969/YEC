package com.yy.yec.ui.activity.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.common.widget.LoadStateLayout;
import com.yy.yec.R;
import com.yy.yec.ui.activity.detail.base.BaseBackActivity;
import com.yy.yec.ui.fragment.detail.SimpleSettingFragment;

import butterknife.Bind;

/**
 * Created by zzq on 2016/9/2.
 */
public class SimpleActivity extends BaseBackActivity {
    public static final int UserSetting = 0;//用户设置
    private int mPageType;
    @Bind(R.id.stateLayout)
    protected LoadStateLayout mStateLayout;
    private String title = "";

    @Override
    protected int setContentView() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initWidget() {
        if (mPageType == UserSetting)
            mStateLayout.setVisibility(View.GONE);
        initPageTypeFragment();
        setActionBarTitle(title);
    }

    private void initPageTypeFragment() {
        try {
            Fragment fragment = null;
            switch (mPageType) {
                case UserSetting:
                    fragment = SimpleSettingFragment.class.newInstance();
                    break;
            }
            if (fragment == null) return;
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.lay_container, fragment);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        Intent intent = getIntent();
        mPageType = intent.getIntExtra("pageType", -1);
        title = intent.getStringExtra("title");
        return mPageType != -1;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}