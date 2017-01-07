package com.yy.yec.ui.fragment.detail;

import android.view.View;
import android.widget.RelativeLayout;

import com.yy.yec.R;
import com.yy.yec.global.AppCurrentUser;
import com.yy.yec.ui.fragment.detail.base.BaseDetailFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zzq on 2016/9/2.
 */
public class SimpleSettingFragment extends BaseDetailFragment {
    @Bind(R.id.fl_logout)
    protected RelativeLayout logout;

    @OnClick(R.id.fl_logout)
    protected void fl_logout_onClick(View v) {
        AppCurrentUser.getInstance().userLogout();
        getActivity().finish();
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidget(View view) {
        if (!AppCurrentUser.getInstance().getLoginOK())
            logout.setVisibility(View.GONE);
    }
}